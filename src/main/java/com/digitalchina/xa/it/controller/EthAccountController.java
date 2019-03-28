package com.digitalchina.xa.it.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digitalchina.xa.it.kafkaConsumer.KafkaUtil;
import com.digitalchina.xa.it.model.KafkaConsumerBean;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.MnemonicService;
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
    private EthAccountService eth;
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
			@RequestParam(name = "itcode",required = true) String itcode,
			@RequestParam(name = "u_pwd", required = true) String u_pwd) {
		ResultUtil result = eth.selectBackup1ByItcode(itcode, u_pwd);
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
	
}
