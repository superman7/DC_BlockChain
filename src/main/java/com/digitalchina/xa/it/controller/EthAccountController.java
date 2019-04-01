package com.digitalchina.xa.it.controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.dao.SystemTransactionDetailDAO;
import com.digitalchina.xa.it.kafkaConsumer.KafkaUtil;
import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.model.KafkaConsumerBean;
import com.digitalchina.xa.it.model.SystemTransactionDetailDomain;
import com.digitalchina.xa.it.model.WalletTransactionDomain;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.MnemonicService;
import com.digitalchina.xa.it.service.WalletTransactionService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.Encrypt;
import com.digitalchina.xa.it.util.EncryptImpl;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.ResultUtil;
import com.digitalchina.xa.it.util.TConfigUtils;

import scala.util.Random;


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
    @Autowired
	private WalletTransactionService walletTransactionService;
	@Autowired
	private SystemTransactionDetailDAO systemTransactionDetailDAO;
    
    private static String keystoreName = "keystore.json";
	private static final BigInteger tax = BigInteger.valueOf(5000000000000000L);
	private static String tempFilePath = "C://temp/";

//	确认提现请求，提交账户地址（TO），金额，钱包地址（FROM）
	/**
	 * @apiDescription 确认提交请求,提交账户地址,金额,钱包地址
	 * @param jsonValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/withdrawConfirm")
	public Map<String, Object> withdrawConfirm(
			@RequestParam(name = "param", required = true) String param) {
		String jsonValue = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		System.out.println(modelMap.get("data"));
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject withdrawJson = JSONObject.parseObject((String) modelMap.get("data"));
		
		String account = withdrawJson.getString("account");
		String defaultAcc = withdrawJson.getString("defaultAcc");
		String itcode = withdrawJson.getString("itcode");
		String alias = withdrawJson.getString("alias");
		Double money = (Double.parseDouble(withdrawJson.getString("money")))*10000000000000000L;
		BigDecimal moneyBigDecimal = new BigDecimal(money);// 转账金额
		
		//记录提现交易信息
		WalletTransactionDomain wtd = new WalletTransactionDomain();
		wtd.setItcode(itcode);
		wtd.setAccountFrom(defaultAcc);
		wtd.setAccountTo(account);
		wtd.setAliasFrom("默认账户");
		wtd.setAliasTo(alias);
		wtd.setBalance(money);
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		wtd.setConfirmTime(date);
		Integer transactionDetailId = walletTransactionService.insertBaseInfo(wtd);
		
		//向system_transactiondetail表记录信息 
		SystemTransactionDetailDomain stdd = new SystemTransactionDetailDomain(defaultAcc, account, money/10000000000000000L, null, date, 0, "remark", itcode, itcode, "", 0, "", transactionDetailId);
		systemTransactionDetailDAO.insertBaseInfo(stdd);
		
		//向kafka发送交易请求，参数为：account，itcode，金额，transactionDetailId
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/ethAccount/withdrawConfirm";
		String postParam = "itcode=" + itcode + "account" + account + "&transactionDetailId=" + transactionDetailId + "&turnBalance=" + moneyBigDecimal;
		HttpRequest.sendPost(url, postParam);
		
		return modelMap;
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
	
	//查询所有账户
	/**
	 * @apiDescription 根据itcode获取账户列表
	 * @param param
	 * @return
	 */
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
		return modelMap;
	}
	
//	确认充值请求，提交账户地址（FROM），密码，金额，钱包地址（TO）
	/**
	 * @apiDescription 确认账户请求,提交账户地址,密码,金额,钱包地址
	 * @param jsonValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/chargeConfirm")
	public Map<String, Object> chargeConfirm(
			@RequestParam(name = "param", required = true) String param) {
		String jsonValue = param.trim();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		System.out.println(jsonValue);
		Encrypt encrypt = new EncryptImpl();
    	String decrypt = null;
		try {
			decrypt = encrypt.decrypt(jsonValue);
		} catch (Exception e1) {
			e1.printStackTrace();
			modelMap.put("success", false);
			modelMap.put("errMsg", "解密失败！");
			return modelMap;
		}
    	String data = null;
		try {
			data = URLDecoder.decode(decrypt, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			modelMap.put("success", false);
			modelMap.put("errMsg", "解密失败！非utf-8编码。");
			return modelMap;
		}
    	System.err.println("解密的助记词，密码及itcode的JSON为:" + data);
    	
    	JSONObject chargeJson = JSONObject.parseObject(data);
		String account = chargeJson.getString("account");
		String password = chargeJson.getString("password");
		String defaultAcc = chargeJson.getString("defaultAcc");
		String itcode = chargeJson.getString("itcode");
		String alias = chargeJson.getString("alias");
		
		Double money = (Double.parseDouble(chargeJson.getString("money")))*10000000000000000L;
		BigDecimal moneyBigDecimal = new BigDecimal(money);// 转账金额
		EthAccountDomain ethAccountDomain = new EthAccountDomain();
		ethAccountDomain.setAccount(account);
		String keystore = ethAccountService.selectKeystoreByAccount(ethAccountDomain);
		try {
			List<Web3j> web3jList = new ArrayList<>();
			List<String> ipArr = TConfigUtils.selectIpArr();
			for(int i = 0; i < ipArr.size(); i++) {
				web3jList.add(Web3j.build(new HttpService(ipArr.get(i))));
			}
			File keystoreFile = keystoreToFile(keystore, account + ".json");
			System.out.println("开始解锁。。。");
			Credentials credentials = WalletUtils.loadCredentials(password, keystoreFile);
			System.out.println("解锁成功。。。");
			keystoreFile.delete();
			System.out.println("删除临时keystore文件成功。。。");
			
			EthGetTransactionCount ethGetTransactionCount = web3jList.get(new Random().nextInt(5)).ethGetTransactionCount(account, DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			System.err.println("nonce:" + nonce);
			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, BigInteger.valueOf(2200000000L), BigInteger.valueOf(2100000L), defaultAcc, moneyBigDecimal.toBigInteger());
			//签名Transaction，这里要对交易做签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			System.err.println("hexValue:" + hexValue);
			//发送交易
			String transactionHash = "";
			String realTransactionHash = "";
			for(int i = 0; i < web3jList.size(); i++) {
				transactionHash = web3jList.get(i).ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
				if(transactionHash != null) {
					realTransactionHash = transactionHash;
				}
			}
			
			WalletTransactionDomain wtd = new WalletTransactionDomain();
			wtd.setItcode(itcode);
			wtd.setAccountFrom(account);
			wtd.setAccountTo(defaultAcc);
			wtd.setAliasFrom(alias);
			wtd.setAliasTo("默认账户");
			wtd.setBalance(money);
			wtd.setTransactionHash(realTransactionHash);
			wtd.setConfirmTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			walletTransactionService.insertBaseInfo(wtd);
			
			modelMap.put("success", true);
			modelMap.put("transactionHash", transactionHash);
		} catch (Exception e) {
			System.out.println("解锁失败。。。");
			e.printStackTrace();
			if(e.getMessage().contains("Invalid password provided")) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "invalidPassword");
				return modelMap;
			}
		}
    	
		return modelMap;
	}
	
//	将keystore写入文件中
	/**
	 * @apiDescription 将keystore写入文件中
	 * @param keystore
	 * @param keystoreName
	 * @return
	 * @throws IOException
	 */
	private File keystoreToFile(String keystore, String keystoreName) throws IOException {
		File file = new File(tempFilePath + keystoreName);
        if(!file.exists()){
         file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(keystore);
        bw.close();
        System.out.println("创建keystore。。。");
        
        return file;
	}
	
	/** @api {get} /ethAccount/balanceQuery/:account 查询输入充值账户的余额
	* @apiVersion 0.1.0 
	* @apiGroup Wallet 
	* @apiParam {String} account 账户地址,格式为"yyyy-MM-dd". @apiParam {String} accountkey 账户,神州区块链账户地址.
	* @apiSuccess {String} do/did do,该账户今日未签到;did,该账户今日已签到.
	* @apiSuccessExample Success-Response: HTTP/1.1 200 OK
	* do */
//	查询输入充值账户的余额
	@ResponseBody
	@GetMapping("/balanceQuery")
	public Map<String, Object> balanceQuery(
			@RequestParam(name = "param", required = true) String param) {
		String jsonValue = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
		
		if((boolean) modelMap.get("success")){
			//获取前端发送的密语，密语密码，地址名和交易密码
			JSONObject accountJson = JSONObject.parseObject((String) modelMap.get("data"));
			String account = accountJson.getString("account");
			try {
				BigInteger balance = web3j.ethGetBalance(account,DefaultBlockParameterName.LATEST).send().getBalance();
				modelMap.put("balance", Double.parseDouble(balance.toString()));
				web3j.shutdown();
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "查询余额失败");
				System.out.println("查询余额失败");
				return modelMap;
			}
		}
		return modelMap;
	}
	/**@api {get} /ethAccount/chargeFromInput/:提交账户充值信息.
	* @apiVersion 0.1.0 
	* @apiGroup Wallet
	* @apiSuccess {String} do/did do,返回成功信息
	* @apiSuccessExample Success-Response: HTTP/1.1 200 OK
	* do */
//	输入账户充值请求，提交账户地址（FROM），密码，私钥，钱包地址（TO）
	@ResponseBody
	@GetMapping("/chargeFromInput")
	public Map<String, Object> chargeFromInput(
			@RequestParam(name = "param", required = true) String param) {
		String jsonValue = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject chargeJson = JSONObject.parseObject((String) modelMap.get("data"));
		String account = chargeJson.getString("account");
		String password = chargeJson.getString("password");
		String defaultAcc = chargeJson.getString("defaultAcc");
		String itcode = chargeJson.getString("itcode");
		String keystore = chargeJson.getString("keystore");
		
		try {
			List<Web3j> web3jList = new ArrayList<>();
			List<String> ipArr = TConfigUtils.selectIpArr();
			for(int i = 0; i < ipArr.size(); i++) {
				web3jList.add(Web3j.build(new HttpService(ipArr.get(i))));
			}
			File keystoreFile = keystoreToFile(keystore, account + ".json");
			System.out.println("开始解锁。。。");
			Credentials credentials = WalletUtils.loadCredentials(password, keystoreFile);
			System.out.println("解锁成功。。。");
			keystoreFile.delete();
			System.out.println("删除临时keystore文件成功。。。");
			
			BigInteger accountBalance = web3jList.get(new Random().nextInt(5)).ethGetBalance(account,DefaultBlockParameterName.LATEST).send().getBalance();
			accountBalance = accountBalance.subtract(tax);
			Double money = Double.parseDouble(accountBalance.toString());
			if(money < 1000000000000000L) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "balanceNotEnough");
				return modelMap;
			}
			
			EthGetTransactionCount ethGetTransactionCount = web3jList.get(new Random().nextInt(5)).ethGetTransactionCount(account, DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			System.err.println("nonce:" + nonce);
			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, BigInteger.valueOf(2200000000L), BigInteger.valueOf(2100000L), defaultAcc, accountBalance);
			//签名Transaction，这里要对交易做签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			System.err.println("hexValue:" + hexValue);
			//发送交易
			String transactionHash = "";
			String realTransactionHash = "";
			for(int i = 0; i < web3jList.size(); i++) {
				System.out.println(web3jList.size());
				transactionHash = web3jList.get(i).ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
				if(transactionHash != null) {
					realTransactionHash = transactionHash;
					System.out.println(transactionHash);
				}
			}
			
			WalletTransactionDomain wtd = new WalletTransactionDomain();
			wtd.setItcode(itcode);
			wtd.setAccountFrom(account);
			wtd.setAccountTo(defaultAcc);
			wtd.setAliasFrom("输入账户");
			wtd.setAliasTo("默认账户");
			wtd.setBalance(money);
			wtd.setTransactionHash(realTransactionHash);
			wtd.setConfirmTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			walletTransactionService.insertBaseInfo(wtd);
			
			modelMap.put("transactionHash", transactionHash);
		} catch (Exception e) {
			System.out.println("解锁失败。。。");
			if(e.getMessage().contains("Invalid password provided")) {
				System.out.println("密码错误");
				modelMap.put("success", false);
				modelMap.put("errMsg", "invalidPassword");
				return modelMap;
			}
		}
    	
		return modelMap;
	}
	
	
	
}
