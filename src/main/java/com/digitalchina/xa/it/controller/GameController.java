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
		System.out.println(jsonValue);
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		System.out.println(modelMap.get("success"));
//		if (!(boolean)modelMap.get("success")) {
//			return modelMap;
//		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		System.out.println(jsonObj);
		Integer game_no = Integer.valueOf(jsonObj.getString("lotteryId"));
		Integer backup4 = Integer.valueOf(jsonObj.getString("option"));
		String itcode = jsonObj.getString("itcode");
		Integer choosed = Integer.valueOf(jsonObj.getString("choosed"));
		System.out.println(backup4);
		System.out.println(game_no+backup4+itcode+choosed+"123123123123");
		int money1 = Integer.valueOf(jsonObj.getString("money"));
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(jsonObj.getString("money")) * 10000000000000000L);
		int money = turnBalance.intValue();
		System.out.println(turnBalance);
		//余额判断
		try {
			Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			System.out.println(balance);
			if (money1>balance.intValue()-10) {
				modelMap.put("data", "以太坊账户余额不足");
				return modelMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
		synchronized (this) {
			
		//更新表奖池金额,参加人数 backup4（待确认交易数）
		gameService.updateNowSumAmountAndBackup4(game_no,money1);
		}
	    //向detail表中插入信息，参数为lotteryId,itcode,result(0),buytime
		SingleDoubleGamesDetailsDomain sdgd = new SingleDoubleGamesDetailsDomain(game_no, itcode, "", "", "", 0, "", "", new Timestamp(new Date().getTime()), "", "", 0,backup4,choosed,money1);
		int transactionId = gameService.insertGameBaseInfo(sdgd);
		System.out.println("transactionId"+transactionId);
		//向kafka发送请求，参数为itcode，transactionId，金额？，lotteryId，产生hashcode，更新account字段，并返回hashcode与transactionId。
		String url = TConfigUtils.selectValueByKey("kafka_address")+"/gameKafka/buyTicket";
		System.out.println(url);
		String postParam = "itcode="+itcode+"&turnBalance="+turnBalance.toString()+"&transactionId="+transactionId+"&choosed="+choosed;
		System.out.println(postParam);
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
			@RequestParam(name = "itcode", required = true) String itcode){
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		System.out.println(itcode);
//		String itcode = jsonObj.getString("itcode");
//		int id = Integer.valueOf(jsonObj.getString("id"));
		//查询当前的未开奖SMB押注
		SingleDoubleGamesInfoDomain sdgid = gameService.selectOneSmbTpid();
//		TPaidlotteryInfoDomain smbTpid = tPaidlotteryService.selectOneSmbTpid();
//		List<TPaidlotteryInfoDomain> hbTpidList = tPaidlotteryService.selectHbTpids();
		//查询多选项的抽奖
//		List<TPaidlotteryInfoDomain> otherTpidList = tPaidlotteryService.selectOtherTpids();
		List<SingleDoubleGamesInfoDomain> newOpenList = gameService.selectNewOpen(Integer.valueOf(TConfigUtils.selectValueByKey("game_show_finsh_size")));
//		for (SingleDoubleGamesInfoDomain singleDoubleGamesInfoDomain : newOpenList) {
//			System.out.println(singleDoubleGamesInfoDomain.getNowSumPerson());
//		}
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SingleDoubleGamesInfoDomain tpid : newOpenList){
	        tpid.setBackup1(sdf.format(tpid.getLotteryTime()));
//	        System.out.println(sdf.format(tpid.getLotteryTime()));
		}		
		modelMap.put("success", true);
		modelMap.put("smbData", JSONObject.toJSON(sdgid));
//		modelMap.put("hbData", JSONObject.toJSON(hbTpidList));
//		modelMap.put("otherData", JSONObject.toJSON(otherTpidList));
		modelMap.put("newOpen", JSONObject.toJSON(newOpenList));
		return modelMap;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/getOne")
	public Map<String, Object> selectLotteryInfoById(
			@RequestParam(name = "itcode", required = true) String itcode,
			@RequestParam(name = "id", required = true) String id){
		 int id1 = Integer.parseInt(id); 
		Map<String, Object> modelMap = new HashMap<String, Object>();		
//		System.out.println(111);
//		System.out.println(itcode+id);
		SingleDoubleGamesInfoDomain tpid = gameService.selectLotteryInfoById(id1);
		List<SingleDoubleGamesDetailsDomain> tpddList = gameService.selectGameDetailsByItcodeAndLotteryId(itcode, id1);
		modelMap.put("success", true);
		modelMap.put("infoData", JSONObject.toJSON(tpid));
		modelMap.put("detailData", JSONObject.toJSON(tpddList));
		return modelMap;
	}
	
	//点击购买按钮后查询表内信息
	@Transactional
	@ResponseBody
	@GetMapping("/selectGameInfo")
	public Map<String, Object> selectLotteryInfo(
			@RequestParam(name = "lotteryId", required = true) String id){
		Map<String, Object> modelMap = new HashMap<String,Object>();
		Integer lotteryId = Integer.parseInt(id);
		SingleDoubleGamesInfoDomain tpid = gameService.selectLotteryInfoById(lotteryId);
		if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
			modelMap.put("data", "LotteryOver");
			return modelMap;
		}
		modelMap.put("data", "success");
		return modelMap;
	}

}
