package com.digitalchina.xa.it.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.kafkaConsumer.KafkaUtil;
import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.model.KafkaConsumerBean;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.MnemonicService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.ResultUtil;
import com.digitalchina.xa.it.util.TConfigUtils;


@Controller
@RequestMapping(value = "/ethAccount")
public class EthAccountController {
    @Autowired
   	private JdbcTemplate jdbc;
    @Autowired
    private KafkaUtil kafkaUtil;
    @Autowired
    private EthAccountService ethAccountService;
    @Autowired
	private MnemonicService mnemonicService;

	@ResponseBody
	@PostMapping("/withdrawConfirm")
	public void balanceQuery(
		@RequestParam(name = "itcode", required = true) String itcode,
		@RequestParam(name = "account", required = true) String account,
		@RequestParam(name = "transactionDetailId", required = true) Integer transactionDetailId,
		@RequestParam(name = "turnBalance", required = true) BigDecimal turnBalance){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode + "' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
        String defaultAcc = list.get(0).get("account").toString();
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = TConfigUtils.selectValueByKey("default_password");
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(transactionDetailId, defaultAcc, account, turnBalance.toBigInteger(), password, keystoreFile);
        kafkaUtil.sendMessage("withdrawconfirm", "WithdrawConfirm", kafkabean);
	}
	@ResponseBody
	@RequestMapping("/login")
	public ResultUtil login(
			@RequestParam(name = "username",required = true) String username,
			@RequestParam(name = "u_pwd", required = true) String u_pwd) {
		ResultUtil result = ethAccountService.selectBackup1ByBackup2(username, u_pwd);
//		System.out.println(result.getStatus());
		return result;
	}
//	重选密语请求，返回新生成的密语
	/**
	 * @apiDefine 生成密语 [title]
    *            [生成密语传到前台]
	 * @return
	 */
	@ResponseBody
	@GetMapping("/refreshMnemonic")
	public Map<String, Object> refreshMnemonic() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String mnemonicSentence = mnemonicService.chooseMnemonic();
		modelMap.put("success", true);
		modelMap.put("mnemonic", mnemonicSentence);
		
		return modelMap;
	}
	
//	创建地址请求
	/**
	 * @apiDefine 生成新地址 [新地址]
    *            [根据前端发送的数据生成账户地址]
	 * @param jsonValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/newAddress")
	public Map<String, Object> newAddress(
            @RequestParam(name = "itcode", required = true) String itcode,
            @RequestParam(name = "mnemonic", required = true) String mnemonic,
            @RequestParam(name = "mnePassword", required = true) String mnePassword
            ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		if(!(boolean) modelMap.get("success")){
//			return modelMap;
//		}*/
		//获取前端发送的数据，包括密语，密语密码和itcode
//		JSONObject mnemonicJson = JSONObject.parseObject((String) modelMap.get("data"));
//		String mnemonic = mnemonicJson.getString("mnemonic");
//		String mnePassword = mnemonicJson.getString("mnePassword");
//		String itcode = mnemonicJson.getString("itcode");
//		System.out.println(itcode);
//		System.out.println(mnemonic);
//		System.out.println(mnePassword);
		//生成ECKeyPair，再得到账户地址
		ECKeyPair ecKeyPair= getECKeyPair(mnemonic, mnePassword);
		String address = "0x" + Keys.getAddress(ecKeyPair);
		
		//查询数据库中该用户拥有的账户个数，如果超过10个，返回false
		List<EthAccountDomain> accountList = ethAccountService.selectEthAccountByItcode(itcode);
		if(accountList.size() >= 10) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "overnumber");
		} else {
			//小于10个，将新生成的账户地址和itcode插入数据库，并向前端返回地址，密语和密语密码
			EthAccountDomain ethAccountDomain = new EthAccountDomain();
			ethAccountDomain.setItcode(itcode);
			ethAccountDomain.setAccount(address);
			ethAccountService.insertItcodeAndAccount(ethAccountDomain);
			modelMap.put("success", true);
			modelMap.put("address", address);
			modelMap.put("mnemonic", mnemonic);
			modelMap.put("mnePassword", mnePassword);
		}
		return modelMap;
	}
	
	//生成ECKeyPair
	/**
	 * @apiDefine 生成ECKeyPair [title]
    *            [根据助记词和密码生成ECKeyPair]
	 * @param mnemonic
	 * @param mnePassword
	 * @return
	 */
	private ECKeyPair getECKeyPair(String mnemonic, String mnePassword) {
		List<String> mnemonicList = mnemonicService.lockMnemonicByPwd(mnemonic, mnePassword);
		String ecKeyPairStr = mnemonicService.merkleTreeRoot(mnemonicList);
		ECKeyPair ecKeyPair= ECKeyPair.create(getSHA2HexValue(ecKeyPairStr));
		
		return ecKeyPair;
	}
	
	/**
	 * 
	 * @apiDescription 生成十六进制数
	 * @param str
	 * @return
	 */
	private byte[] getSHA2HexValue(String str) {
        byte[] cipher_byte;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            cipher_byte = md.digest();
            return cipher_byte;
        } catch (Exception e) {
                e.printStackTrace();
        }
        
        return null;
  }
	
	/**
	 * @apiDescription 检测地址是否重复
	 * @param jsonValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/checkUp")
	public Map<String, Object> checkUp(@RequestParam(name = "param", required = true) String param) {
		String pString = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(pString);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject aliasInfoJson = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = aliasInfoJson.getString("itcode");
		String alias = aliasInfoJson.getString("alias");
		
		List<EthAccountDomain> accountList = ethAccountService.selectEthAccountByItcode(itcode);
		for(int index = 0; index < accountList.size(); index++) {
			if(accountList.get(index).getAlias().equals(alias)) {
				modelMap.put("valid", false);
				return modelMap;
			}
		}
		modelMap.put("valid", true);
		
		return modelMap;
	}
//	创建账户请求
	/**
	 * @apiDescription 创建账户请求
	 * @param jsonValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/newAccount")
	public Map<String, Object> newAccount(@RequestParam(name = "param", required = true) String param) {
		String pString = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(pString);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		//获取前端发送的密语，密语密码，地址名和交易密码
		JSONObject allInfoSentenceJson = JSONObject.parseObject((String) modelMap.get("data"));
		String mnemonic = allInfoSentenceJson.getString("mnemonic");
		String mnePassword = allInfoSentenceJson.getString("mnePassword");
		String alias = allInfoSentenceJson.getString("alias");
		String traPassword = allInfoSentenceJson.getString("traPassword");
		ECKeyPair ecKeyPair = getECKeyPair(mnemonic, mnePassword);
		String address = "0x" + Keys.getAddress(ecKeyPair);
		
		//生成WalletFile(keystore)，更新数据库，根据address存入keystore和alias
		try {
			WalletFile walletFile = Wallet.createLight(traPassword, ecKeyPair);
			String keystore = ((JSONObject) JSONObject.toJSON(walletFile)).toJSONString();
			ethAccountService.updateKeystoreAndAlias(keystore, alias, address, 1);
		} catch (CipherException e) {
			e.printStackTrace();
		}
		
		return modelMap;
	}
	
//	获取keystore
	/**
	 * @apiDescription 获取keystore
	 * @param jsonValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getKeystore")
	public Map<String, Object> getKeystore(
			@RequestParam(name = "param", required = true) String param) {
		String pString = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(pString);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject accountJson = JSONObject.parseObject((String) modelMap.get("data"));
		String account = accountJson.getString("account");
		EthAccountDomain ethAccountDomain = new EthAccountDomain();
		ethAccountDomain.setAccount(account);
		String keystore = ethAccountService.selectKeystoreByAccount(ethAccountDomain);
		System.out.println(keystore);
		modelMap.put("keystore", keystore);
		
		return modelMap;
	}
	@ResponseBody
	@GetMapping("/accountList")
	@Transactional
	public Map<String, Object> accountList(
            @RequestParam(name = "param", required = true) String param) {
		String jsonValue = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject mnemonicJson = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = mnemonicJson.getString("itcode");
		ethAccountService.refreshBalance(itcode);
//		System.out.println("eeeeeeeeeeeeeeeeeee");
		modelMap.put("success", true);
//		System.out.println(modelMap.get("success"));
		List<EthAccountDomain> accountList = ethAccountService.selectEthAccountByItcode(itcode);
		modelMap.put("accountList", accountList);
//		System.out.println("123123"+modelMap.get("success"));
		return modelMap;
	}
	
}
