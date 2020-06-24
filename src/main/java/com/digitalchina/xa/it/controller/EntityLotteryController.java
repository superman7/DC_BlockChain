package com.digitalchina.xa.it.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.dao.TPaidlotteryDetailsDAO;
import com.digitalchina.xa.it.dao.TPaidlotteryInfoDAO;
import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.model.TConfigDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.TPaidlotteryService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.TConfigUtils;

@Controller
@RequestMapping(value = "/entityLottery")
public class EntityLotteryController {
	
	@Autowired
	private TPaidlotteryService tPaidlotteryService;
	@Autowired
	private EthAccountService ethAccountService;
	@Autowired
	private TPaidlotteryDetailsDAO tPaidlotteryDetailsDAO;
	@Autowired
	private TPaidlotteryInfoDAO tPaidlotteryInfoDAO;
	
	@ResponseBody
	@PostMapping("/insertLotteryInfo")
	public Map<String, Object> insertLotteryInfo(
			@RequestParam MultipartFile file,
	        @RequestParam(name = "title", required = true) String title,
	        @RequestParam(name = "description", required = true) String description,
	        @RequestParam(name = "price", required = true) String price,
	        @RequestParam(name = "address", required = true) String address,
	        @RequestParam(name = "days", required = true) String days
	        ){
		String name = file.getName();
		System.out.println(file.getOriginalFilename()+title+description+price+address+days);
		File targetFile=null;
        String url="E:\\images\\";//返回存储路径
        int code=1;
        System.out.println(file);
        String fileName=file.getOriginalFilename();//获取文件名加后缀
        if(fileName!=null&&fileName!=""){   
            //获取文件夹路径
            File file1 =new File(url+fileName); 
            //如果文件夹不存在则创建    
            if(!file1 .exists()  && !file1 .isDirectory()){       
                file1 .mkdir();  
            }
            //将图片存入文件夹
            targetFile = new File(file1, fileName);
            
            try {
            	//将上传的文件写到服务器上指定的文件。
                file.transferTo(targetFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		
		TPaidlotteryInfoDomain tpid = new TPaidlotteryInfoDomain();
		tpid.setName(title);
		tpid.setDescription(description);
		tpid.setWinSumAmount(Integer.valueOf(999990));
		tpid.setWinSumPerson(Integer.valueOf(99999));
		tpid.setReward(title);
		tpid.setUnitPrice(Integer.valueOf(price));
		tpid.setLimitEveryday(Integer.valueOf(999990));
		tpid.setWinCount(Integer.valueOf(1));
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 2);
		Timestamp time1 = new Timestamp(c.getTimeInMillis());
		System.out.println(time1);
		tpid.setFlag(0);
		//2为多选项抽奖
		tpid.setTypeCode(2);
		tpid.setNowSumAmount(0);
		tpid.setBackup4(0);
		tpid.setBackup5(0);
		tpid.setLotteryTime(new Timestamp(new Date().getTime()));
		tpid.setNowSumPerson(0);
		tpid.setWinDate("");
		tpid.setBackup1(time1.toString());
		tpid.setBackup2(address);
		tpid.setBackup3(fileName);
		tPaidlotteryInfoDAO.insertLotteryInfo(tpid);
		return null;
	}
	@Transactional
	@ResponseBody
	@GetMapping("/selectLotteryInfo")
	public Map<String, Object> selectLotteryInfo(
			@RequestParam(name = "param", required = true) String jsonValue){
		/*
		 * 1.插入detail信息
		 * 2.调用kafka进行合约交易
		 */
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer lotteryId = Integer.valueOf(jsonObj.getString("lotteryId"));
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
		if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
			modelMap.put("data", "LotteryOver");
			return modelMap;
		}
		modelMap.put("data", "success");
		return modelMap;
	}
	
	
	@ResponseBody
	@GetMapping("/lotteryInfo/updateReward")
	public Map<String, Object> updateReward(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer id = Integer.valueOf(jsonObj.getString("id"));
		String reward = jsonObj.getString("reward");
		tPaidlotteryService.updateLotteryReward(id, reward);
		
		modelMap.put("data", "true");
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/runOptionLottery")
	public Map<String, Object> runOptionLottery(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer id = Integer.valueOf(jsonObj.getString("lotteryId"));
		Integer option = Integer.valueOf(jsonObj.getString("option"));
		tPaidlotteryService.runOptionLottery(id, option);
		
		modelMap.put("data", "true");
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/runOptionLottery1")
	public Map<String, Object> runOptionLottery1(
			@RequestParam(name = "id", required = true) Integer id,
			@RequestParam(name = "option", required = true) Integer option){
		tPaidlotteryService.runOptionLottery(id, option);
		
		return null;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/insertLotteryDetails")
	public Map<String, Object> insertLotterydetails(
			@RequestParam(name = "param", required = true) String jsonValue){
		/*
		 * 1.插入detail信息
		 * 2.调用kafka进行合约交易
		 */
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer lotteryId = Integer.valueOf(jsonObj.getString("lotteryId"));
		Integer backup4 = Integer.valueOf(jsonObj.getString("option"));
		String itcode = jsonObj.getString("itcode");
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(jsonObj.getString("unitPrice")) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(jsonObj.getString("unitPrice")) > Double.valueOf(balance.toString())-10) {
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
//		String url = TConfigUtils.selectValueByKey("kafka_address_test") + "/lottery/buyTicket";
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
	@GetMapping("/inviteLotteryDetails")
	public Map<String, Object> inviteLotteryDetails(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer lotteryId = Integer.valueOf(jsonObj.getString("lotteryId"));
		//此处默认为5，代表未激活的夺宝码
		Integer backup4 = Integer.valueOf(jsonObj.getString("option"));
		String itcode = jsonObj.getString("itcode").trim();
		String inviteItcode = jsonObj.getString("inviteItcode").trim();
		//不允许邀请自身
		if(itcode.equalsIgnoreCase(inviteItcode)){
			modelMap.put("data", "feifa");
			return modelMap;
		}
		//itcode合法性
		EthAccountDomain ead = ethAccountService.selectDefaultEthAccount(inviteItcode);
		if(ead == null){
			modelMap.put("data", "InviteItcodeIsIllegaly");
			return modelMap;
		}
		//本人邀请已达上限
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
				
		//查询inviteItcode是否已达邀请上限
		List<TPaidlotteryDetailsDomain> tempzz = tPaidlotteryService.selectUninviteLotteryDetailsByItcodeAndLotteryId(inviteItcode, lotteryId);
		if(tempzz.size() - 20 >= 0){
			modelMap.put("data", "UserHasBeenInvitedLimit");
			return modelMap;
		}
		//未被邀请则进行下述步骤
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(jsonObj.getString("unitPrice")) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(jsonObj.getString("unitPrice")) > Double.valueOf(balance.toString()) - 10) {
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
//		String url = TConfigUtils.selectValueByKey("kafka_address_test") + "/lottery/buyTicket";
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/lottery/buyTicket";
		System.err.println(url);
		String postParam = "itcode=" + itcode + "&turnBalance=" + turnBalance.toString() + "&transactionDetailId=" + transactionId;
		HttpRequest.sendPost(url, postParam);
		//kafka那边更新account和hashcode
		//定时任务，查询到
		
		modelMap.put("data", "success");
		return modelMap;
	}
	
	@ResponseBody
	@PostMapping("/getResult")
	public Map<String, Object> kafkaUpdateDetails(
			@RequestParam(name = "param", required = true) String jsonValue){
		return null;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/lotteryInfo/getOne")
	public Map<String, Object> selectLotteryInfoById(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
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
	
	@ResponseBody
	@GetMapping("/lotteryInfo/getData")
	public Map<String, Object> getData(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
//		String itcode = jsonObj.getString("itcode");
//		int id = Integer.valueOf(jsonObj.getString("id"));
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
	
	@ResponseBody
	@GetMapping("/lotteryInfo/unfinished")
	public Map<String, Object> selectLotteryInfoUnfinished(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<TPaidlotteryInfoDomain> tpidList = tPaidlotteryService.selectLotteryInfoByFlag(0);
		modelMap.put("success", true);
		modelMap.put("data", JSONObject.toJSON(tpidList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/finished")
	public Map<String, Object> selectLotteryInfofinished(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<TPaidlotteryInfoDomain> tpidList = tPaidlotteryService.selectLotteryInfoByFlag(1);
		modelMap.put("success", true);
		modelMap.put("data", JSONObject.toJSON(tpidList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/all")
	public void selectLotteryInfoAll(){
		/*
		 * 排序逻辑
		 * 1.按时间，当前时间到开奖时间（XX%）
		 * 2.按奖池，当前奖池到开奖金额（XX%）
		 * 3.按参与人数，当前人数到开奖人数（XX%）
		 */
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/{lotteryDetailId}")
	public void selectlotteryDetailById(
			@RequestParam(name = "param", required = true) String jsonValue){
		
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/myJoin")
	public Map<String, Object> selectlotteryDetailByItcode(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectLotteryDetailsByItcode(itcode);
		
		modelMap.put("data", JSONObject.toJSON(tpddList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/myWin")
	public Map<String, Object> selectMyWin(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectLotteryDetailsByItcodeAndResult(itcode,2);
		
		modelMap.put("data", JSONObject.toJSON(tpddList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/{lotteryInfoId}")
	public void selectlotteryDetailByLotteryInfoId(
			@RequestParam(name = "param", required = true) String jsonValue){
		/*
		 * 某抽奖参与详情。
		 */
		//tPaidlotteryService.selectLotteryDetailsByLotteryId(lotteryId);
	}
	//查询邀请我的及界面展示
	@Transactional
	@ResponseBody
	@GetMapping("/lotteryInfo/getInvite")
	public Map<String, Object> selectInviteLotteryInfoById(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		int id = Integer.valueOf(jsonObj.getString("id"));
		
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
	@GetMapping("/selectInfoByAddress")
	public Map<String, Object> acceptInvite(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String address = jsonObj.getString("address");
		
		return modelMap;
	}
}
