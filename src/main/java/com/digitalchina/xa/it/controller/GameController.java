package com.digitalchina.xa.it.controller;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;
import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.GameService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.HttpRequest;
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
	 * @apiDescription 向表中添加押注信息
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
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(jsonObj.getString("money")) * 10000000000000000L);
		//余额判断
		try {
			Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if (Double.valueOf(jsonObj.getString("turnBalance"))>Double.valueOf(balance.toString())-10) {
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
		SingleDoubleGamesDetailsDomain sdgd = new SingleDoubleGamesDetailsDomain(game_no, itcode, "", "", "", 0, "", turnBalance.toString(), new Timestamp(new Date().getTime()), "", "", 0,backup4,0);
		int transactionId = gameService.insertGameBaseInfo(sdgd);
		System.out.println("transactionId"+transactionId);
		//向kafka发送请求，参数为itcode，transactionId，金额？，lotteryId，产生hashcode，更新account字段，并返回hashcode与transactionId。
		String url = TConfigUtils.selectValueByKey("kafka_address")+"game/buyTicket";
		System.out.println(url);
		String postParam = "itcode="+itcode+"&turnBalance="+turnBalance.toString()+"&transactionId="+transactionId;
		HttpRequest.sendPost(url, postParam);
		//kafka那边更新account和hashcode
		//定时任务，查询到
		modelMap.put("data", "success");
		return modelMap;
	}
	
	/**
	 * 查找已开奖信息和未开奖信息
	 * @param param
	 * @return
	 */
	@ResponseBody
	@GetMapping("/gameInfo/getData")
	public Map<String, Object> getData(
			@RequestParam(name = "param", required = true) String param){
			String jsonValue = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
//		String itcode = jsonObj.getString("itcode");
//		int id = Integer.valueOf(jsonObj.getString("id"));
		//查询当前的未开奖SMB押注
		SingleDoubleGamesInfoDomain sdgid = gameService.selectOneSmbTpid();
//		TPaidlotteryInfoDomain smbTpid = tPaidlotteryService.selectOneSmbTpid();
//		List<TPaidlotteryInfoDomain> hbTpidList = tPaidlotteryService.selectHbTpids();
		//查询多选项的抽奖
//		List<TPaidlotteryInfoDomain> otherTpidList = tPaidlotteryService.selectOtherTpids();
		List<SingleDoubleGamesInfoDomain> newOpenList = gameService.selectNewOpen(Integer.valueOf(TConfigUtils.selectValueByKey("game_show_finsh_size")));
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SingleDoubleGamesInfoDomain tpid : newOpenList){
	        tpid.setBackup1(sdf.format(tpid.getLotteryTime()));
//	        System.out.println(sdf.format(tpid.getLotteryTime()));
		}		
		modelMap.put("smbData", JSONObject.toJSON(sdgid));
//		modelMap.put("hbData", JSONObject.toJSON(hbTpidList));
//		modelMap.put("otherData", JSONObject.toJSON(otherTpidList));
		modelMap.put("newOpen", JSONObject.toJSON(newOpenList));
		
		return modelMap;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/lotteryInfo/getOne")
	public Map<String, Object> selectLotteryInfoById(
			@RequestParam(name = "param", required = true) String param){
			String jsonValue = param.trim();
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		int id = Integer.valueOf(jsonObj.getString("id"));
		
		TPaidlotteryInfoDomain tpid = gameService.selectLotteryInfoById(id);
		List<TPaidlotteryDetailsDomain> tpddList = gameService.selectLotteryDetailsByItcodeAndLotteryId(itcode, id);
		
		for(TPaidlotteryDetailsDomain tpldd : tpddList){
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tpldd.setHashcode(sdf.format(tpldd.getBuyTime()));
		}
		modelMap.put("infoData", JSONObject.toJSON(tpid));
		modelMap.put("detailData", JSONObject.toJSON(tpddList));
		return modelMap;
	}

}
