package com.digitalchina.xa.it.controller;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;
import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.GameService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.TConfigUtils;

import redis.clients.jedis.Jedis;

/**
 * @apiDescription 插入押注信息
 * @author dc
 *
 */
@Controller
@RequestMapping(value = "/test")
public class TestController {
	@Autowired
	private EthAccountService ethAccountService;
	@Autowired
	private GameService gameService;
	
	
	/**
	 * @apiDescription 向表中添加押注信息
	 * @param param
	 * @return
	 */
	@Transactional
	@ResponseBody
	@GetMapping("/insertGameDetails")
	public Map<String, Object> insertGameDetails(
		@RequestParam(name = "param",required = true) String itcode,Double money){
		Jedis jedis = new Jedis("10.0.6.52",6379);
		String valueString = jedis.get(itcode);
		System.out.println(valueString);
		JSONObject jsonObj = JSONObject.parseObject(valueString);
		String account = jsonObj.getString("account");
		if (account == null) {
			EthAccountDomain domain = ethAccountService.selectDefaultEthAccount(itcode);
			domain.setBackup3("0");
			jedis.set(itcode, JSONObject.toJSONString(domain));
			
		}
		return null;
		
	}
	


}
