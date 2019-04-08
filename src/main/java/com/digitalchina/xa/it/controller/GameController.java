package com.digitalchina.xa.it.controller;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
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
import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.GameService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.TConfigUtils;

/**
 * @apiDescription 插入押注信息
 * @author dc
 *
 */
@Controller
@RequestMapping(value = "/game")
public class GameController {
	@Autowired
	private GameService gameService;
	@Autowired
	private EthAccountService ethAccountService;
	
	
	/**
	 * @apiDescription 向表中添加压住信息
	 * @param param
	 * @return
	 */
	@Transactional
	@ResponseBody
	@GetMapping("/insertGameDetails")
	public Map<String, Object> insertGameDetails(
		@RequestParam(name = "param",required = true) String param){
		String jsonValue = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if (!(boolean)modelMap.get("success")) {
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer game_no = Integer.valueOf(jsonObj.getString("id"));
		Integer backup4 = Integer.valueOf(jsonObj.getString("backup4"));
		String itcode = jsonObj.getString("itcode");
		String account = jsonObj.getString("account");
		BigInteger turnMoney = BigInteger.valueOf( Long.valueOf(jsonObj.getString("money")) * 10000000000000000L);
		//余额判断
		try {
			Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if (Double.valueOf(jsonObj.getString("turnMoney"))>Double.valueOf(balance.toString())-10) {
				modelMap.put("data", "以太坊账户余额不足");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
		synchronized (this) {
			
		//更新表奖池金额 backup4（待确认交易数）
		gameService.updateNowSumAmountAndBackup4(game_no);
		}
	    //向detail表中插入信息，参数为lotteryId,itcode,result(0),buytime
		SingleDoubleGamesDetailsDomain sdgd = new SingleDoubleGamesDetailsDomain(game_no, itcode, "", "", "", 0, "", turnMoney.toString(), new Timestamp(new Date().getTime()), "", "", 0,backup4,0);
		int transactionId = gameService.insertGameBaseInfo(sdgd);
		System.out.println("transactionId"+transactionId);
		
		return null;
			
		
	}

}
