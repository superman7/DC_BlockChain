package com.digitalchina.xa.it.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.dao.TPaidlotteryDetailsDAO;
import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.TPaidlotteryService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.TConfigUtils;
@Controller
@RequestMapping("/") //创建路由规则http://xxxx/cat
public class RouteController {
	@Autowired
	private TPaidlotteryService tPaidlotteryService;
	@Autowired
	private EthAccountService ethAccountService;
	@Autowired
	private TPaidlotteryDetailsDAO tPaidlotteryDetailsDAO;
    /**
     * 默认路由方法
     *
     * @return
     */
    @RequestMapping("index")
    public ModelAndView index(@RequestParam(name = "itcode", required = true) String itcode) {
        ModelAndView modelAndView = new ModelAndView("index"); //设置对应JSP的模板文件
        modelAndView.addObject("hi", itcode); //设置${hi}标签的值为Hello,Cat
        return modelAndView;
    }
    
    @RequestMapping("mainPage")
    public ModelAndView mainPage(@RequestParam(name = "itcode", required = true) String itcode) {
        ModelAndView modelAndView = new ModelAndView("lottery/lotteryPage"); //设置对应JSP的模板文件
        modelAndView.addObject("itcode", itcode);
        return modelAndView;
    }
    
    @RequestMapping("lotteryBuyPage")
    public ModelAndView lotteryBuyPage(
    		@RequestParam(name = "itcode", required = true) String itcode,
    		@RequestParam(name = "id", required = true) Integer id) {
        ModelAndView modelAndView = new ModelAndView("lottery/lotteryBuyPage"); //设置对应JSP的模板文件
        modelAndView.addObject("itcode", itcode);
        modelAndView.addObject("id", id);
        return modelAndView;
    }
    
    @RequestMapping("lotteryIntroduce")
    public ModelAndView lotteryIntroduce() {
        ModelAndView modelAndView = new ModelAndView("lottery/lotteryIntroduce"); //设置对应JSP的模板文件
        return modelAndView;
    }
    
    @RequestMapping("lotteryInviteIntroduce")
    public ModelAndView lotteryInviteIntroduce() {
        ModelAndView modelAndView = new ModelAndView("lottery/lotteryInviteIntroduce"); //设置对应JSP的模板文件
        return modelAndView;
    }
    
    @RequestMapping(value = "getLotteryInfo", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getLotteryInfo(
    		@RequestParam(name = "jsonStr", required = true) String jsonStr) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data", jsonStr);
		JSONObject jsonObj = JSONObject.parseObject(jsonStr);
		String itcode = jsonObj.getString("itcode");
		int id = Integer.valueOf(jsonObj.getString("id"));
		
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(id);
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectLotteryDetailsByItcodeAndLotteryId(itcode, id);
		
		for(TPaidlotteryDetailsDomain tpldd : tpddList){
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tpldd.setHashcode(sdf.format(tpldd.getBuyTime()));
		}
		modelMap.put("infoData", JSONObject.toJSON(tpid));
		modelMap.put("detailData", JSONObject.toJSON(tpddList));
		return modelMap;
	}
    
    @RequestMapping(value = "getLotteryData", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getLotteryData(
    		@RequestParam(name = "itcode", required = true) String itcode) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
//		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		//查询当前的未开奖SMB抽奖
		TPaidlotteryInfoDomain smbTpid = tPaidlotteryService.selectOneSmbTpid();
		//查询当前的未开奖RMB红包抽奖
		List<TPaidlotteryInfoDomain> hbTpidList = tPaidlotteryService.selectHbTpids();
		//查询多选项的抽奖
		List<TPaidlotteryInfoDomain> otherTpidList = tPaidlotteryService.selectOtherTpids();
		List<TPaidlotteryInfoDomain> newOpenList = tPaidlotteryService.selectNewOpen(Integer.valueOf(TConfigUtils.selectValueByKey("lottery_show_finish_size")));
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(TPaidlotteryInfoDomain tpid : newOpenList){
	        tpid.setBackup1(sdf.format(tpid.getLotteryTime()));
		}
		
		modelMap.put("smbData", JSONObject.toJSON(smbTpid));
		modelMap.put("hbData", JSONObject.toJSON(hbTpidList));
		modelMap.put("otherData", JSONObject.toJSON(otherTpidList));
		modelMap.put("newOpen", JSONObject.toJSON(newOpenList));
		
		return modelMap;
	}
    
    @Transactional
	@ResponseBody
	@GetMapping("/lotteryInfo/getOne")
	public Map<String, Object> selectLotteryInfoById(
			@RequestParam(name = "itcode", required = true) String itcode,
			@RequestParam(name = "id", required = true) int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(id);
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectLotteryDetailsByItcodeAndLotteryId(itcode, id);
		
		for(TPaidlotteryDetailsDomain tpldd : tpddList){
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tpldd.setHashcode(sdf.format(tpldd.getBuyTime()));
		}
		modelMap.put("infoData", JSONObject.toJSON(tpid));
		modelMap.put("detailData", JSONObject.toJSON(tpddList));
		return modelMap;
	}
    
    @ResponseBody
	@GetMapping("/lotteryInfo/updateReward")
	public Map<String, Object> updateReward(
			@RequestParam(name = "reward", required = true) String reward,
			@RequestParam(name = "id", required = true) int id){
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		tPaidlotteryService.updateLotteryReward(id, reward);
		
		modelMap.put("data", "true");
		return modelMap;
	}
    
    @ResponseBody
	@GetMapping("/lotteryInfo/runOptionLottery")
	public Map<String, Object> runOptionLottery(
			@RequestParam(name = "lotteryId", required = true) Integer id,
			@RequestParam(name = "option", required = true) Integer option){
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		tPaidlotteryService.runOptionLottery(id, option);
		
		modelMap.put("data", "true");
		return modelMap;
	}
    
    @Transactional
	@ResponseBody
	@GetMapping("/inviteLotteryDetails")
	public Map<String, Object> inviteLotteryDetails(
			@RequestParam(name = "itcode", required = true) String itcode,
			@RequestParam(name = "unitPrice", required = true) String unitPrice,
			@RequestParam(name = "lotteryId", required = true) Integer lotteryId,
			@RequestParam(name = "inviteItcode", required = true) String inviteItcode,
			@RequestParam(name = "option", required = true) Integer backup4){
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		//不允许邀请自身
		if(itcode == inviteItcode){
			modelMap.put("data", "feifa");
			return modelMap;
		}
		//itcode合法性
		EthAccountDomain ead = ethAccountService.selectDefaultEthAccount(inviteItcode);
		if(ead == null){
			modelMap.put("data", "InviteItcodeIsIllegaly");
			return modelMap;
		}
		//用户邀请已达上限
		List<TPaidlotteryDetailsDomain> tempz1z = tPaidlotteryService.selectHaveInvitedByItcodeAndLotteryId(itcode, lotteryId);
		if(tempz1z.size() - 20 >= 0){
			modelMap.put("data", "InviteCountMoreThanLimit");
			return modelMap;
		}
		
		//该用户已邀请
		List<TPaidlotteryDetailsDomain> tempz2z = tPaidlotteryService.selectIfInvitedByItcodeAndLotteryId(itcode, inviteItcode, lotteryId);
		if(!tempz2z.isEmpty()){
			modelMap.put("data", "ThisItcodeHasBeenInvited");
			return modelMap;
		}
				
		//查询inviteItcode是否已被邀请
//		List<TPaidlotteryDetailsDomain> tempzz = tPaidlotteryService.selectUninviteLotteryDetailsByItcodeAndLotteryId(inviteItcode, lotteryId);
//		if(!tempzz.isEmpty()){
//			modelMap.put("data", "UserHasBeenInvited");
//			return modelMap;
//		}
		//未被邀请则进行下述步骤
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(unitPrice) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(unitPrice) > Double.valueOf(balance.toString()) - 10) {
				modelMap.put("data", "balanceNotEnough");
				return modelMap;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
		
		//判断是否达到所需金额
		synchronized(this){
			TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
			if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
				modelMap.put("data", "LotteryOver");
				return modelMap;
			}
			//直接更新Info表nowSumAmount、backup4（待确认交易笔数）
			tPaidlotteryService.updateNowSumAmountAndBackup4(lotteryId);
		}
		
		//向t_paidlottery_details表中插入信息， 参数为lotteryId, itcode, result(0), buyTime
		//20180114 添加option（backup4）选项
		//20180118 添加inviteItcode（backup1）选项
		TPaidlotteryDetailsDomain tpdd = new TPaidlotteryDetailsDomain(lotteryId, itcode, "", "", "", 0, "", "", new Timestamp(new Date().getTime()), inviteItcode, itcode, 0, backup4);
		int transactionId = tPaidlotteryService.insertLotteryBaseInfo(tpdd);
		System.out.println("transactionId" + transactionId);
		
		//向kafka发送请求，参数为itcode, transactionId,  金额？， lotteryId？; 产生hashcode，更新account字段，并返回hashcode与transactionId。
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/lottery/buyTicket";
		System.err.println(url);
		String postParam = "itcode=" + itcode + "&turnBalance=" + turnBalance.toString() + "&transactionDetailId=" + transactionId;
		HttpRequest.sendPost(url, postParam);
		//kafka那边更新account和hashcode
		//定时任务，查询到
		
		modelMap.put("data", "success");
		return modelMap;
	}
    
    @Transactional
	@ResponseBody
	@GetMapping("/lotteryInfo/getInvite")
	public Map<String, Object> selectInviteLotteryInfoById(
			@RequestParam(name = "itcode", required = true) String itcode,
			@RequestParam(name = "id", required = true) Integer id){
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(id);
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectInviteLotteryDetailsByItcodeAndLotteryId(itcode, id);
		
		for(TPaidlotteryDetailsDomain tpldd : tpddList){
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tpldd.setHashcode(sdf.format(tpldd.getBuyTime()));
		}
		modelMap.put("infoData", JSONObject.toJSON(tpid));
		modelMap.put("detailData", JSONObject.toJSON(tpddList));
		return modelMap;
	}
    
    @Transactional
	@ResponseBody
	@GetMapping("/selectLotteryInfo")
	public Map<String, Object> selectLotteryInfo(
			@RequestParam(name = "lotteryId", required = true) Integer lotteryId){
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
		if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
			modelMap.put("data", "LotteryOver");
			return modelMap;
		}
		modelMap.put("data", "success");
		return modelMap;
	}
    
    @Transactional
	@ResponseBody
	@GetMapping("/insertLotteryDetails")
	public Map<String, Object> insertLotterydetails(
			@RequestParam(name = "itcode", required = true) String itcode,
			@RequestParam(name = "unitPrice", required = true) String unitPrice,
			@RequestParam(name = "lotteryId", required = true) Integer lotteryId,
			@RequestParam(name = "option", required = true) Integer backup4){
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(unitPrice) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(unitPrice) > Double.valueOf(balance.toString())-10) {
				modelMap.put("data", "balanceNotEnough");
				return modelMap;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
		
		//判断是否达到所需金额
		synchronized(this){
			TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
			if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
				modelMap.put("data", "LotteryOver");
				return modelMap;
			}
			//直接更新Info表nowSumAmount、backup4（待确认交易笔数）
			tPaidlotteryService.updateNowSumAmountAndBackup4(lotteryId);
		}
		
		//向t_paidlottery_details表中插入信息， 参数为lotteryId, itcode, result(0), buyTime
		//20180114 添加option选项
		TPaidlotteryDetailsDomain tpdd = new TPaidlotteryDetailsDomain(lotteryId, itcode, "", "", "", 0, "", "", new Timestamp(new Date().getTime()), "", "", 0, backup4);
		int transactionId = tPaidlotteryService.insertLotteryBaseInfo(tpdd);
		System.out.println("transactionId" + transactionId);
		
		//向kafka发送请求，参数为itcode, transactionId,  金额？， lotteryId？; 产生hashcode，更新account字段，并返回hashcode与transactionId。
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/lottery/buyTicket";
		System.err.println(url);
		String postParam = "itcode=" + itcode + "&turnBalance=" + turnBalance.toString() + "&transactionDetailId=" + transactionId;
		HttpRequest.sendPost(url, postParam);
		//kafka那边更新account和hashcode
		//定时任务，查询到
		
		modelMap.put("data", "success");
		return modelMap;
	}
    
    @Transactional
	@ResponseBody
	@GetMapping("/acceptInvite")
	public Map<String, Object> acceptInvite(
			@RequestParam(name = "id", required = true) Integer idKey,
			@RequestParam(name = "unitPrice", required = true) String unitPrice){
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	modelMap.put("success", true);
		
		TPaidlotteryDetailsDomain tpldd = tPaidlotteryService.selectLotteryDetailsById(idKey);
		String itcode = tpldd.getBackup1();
		String inviteItcode = tpldd.getBackup2();
		Integer lotteryId = tpldd.getLotteryId();
		//1.查出该条记录，将backup4置为0,其余都置为7；(添加受邀请上限)
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectInviteLotteryDetailsByItcodeAndLotteryId(itcode, lotteryId);
		List<TPaidlotteryDetailsDomain> tpddList1 = tPaidlotteryService.selectAcceptInviteLotteryDetailsByItcodeAndLotteryId(itcode, lotteryId);
		Integer limit = Integer.valueOf(TConfigUtils.selectValueByKey("accept_invite_limit"));
		if(tpddList1.size() + 1 < limit){
			tPaidlotteryDetailsDAO.updateBackup4From5To0(idKey);
		}else if(tpddList1.size() + 1 == limit){
			tPaidlotteryDetailsDAO.updateBackup4From5To0(idKey);
			for(TPaidlotteryDetailsDomain tplddTemp : tpddList){
				if((tplddTemp.getBackup4() != 0) && (tplddTemp.getId() != idKey) ){
					tPaidlotteryDetailsDAO.updateBackup4From5To7(tplddTemp.getId());
				}
			}
		}else {
			modelMap.put("data", "acceptInviteLimit");
			return modelMap;
		}
		
		//2.为自己再购买一张夺宝券，backup1=admin，backup2=邀请人		
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(unitPrice) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(unitPrice) > Double.valueOf(balance.toString()) - 10) {
				modelMap.put("data", "balanceNotEnough");
				return modelMap;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
			
		//判断是否达到所需金额
		synchronized(this){
			TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
			if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
				modelMap.put("data", "LotteryOver");
				return modelMap;
			}
			//直接更新Info表nowSumAmount、backup4（待确认交易笔数）
			tPaidlotteryService.updateNowSumAmountAndBackup4(lotteryId);
		}
		
		//向t_paidlottery_details表中插入信息， 参数为lotteryId, itcode, result(0), buyTime
		//20180114 添加option（backup4）选项
		//20180118 添加inviteItcode（backup1）选项
		TPaidlotteryDetailsDomain tpdd = new TPaidlotteryDetailsDomain(lotteryId, itcode, "", "", "", 0, "", "", new Timestamp(new Date().getTime()), "admin", inviteItcode, 0, 0);
		int transactionId = tPaidlotteryService.insertLotteryBaseInfo(tpdd);
		System.out.println("transactionId" + transactionId);
		
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/lottery/buyTicket";
		System.err.println(url);
		String postParam = "itcode=" + itcode + "&turnBalance=" + turnBalance.toString() + "&transactionDetailId=" + transactionId;
		HttpRequest.sendPost(url, postParam);
		modelMap.put("data", "success");
		return modelMap;
	}
}