package com.digitalchina.xa.it.controller;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digitalchina.xa.it.kafkaConsumer.KafkaUtil;
import com.digitalchina.xa.it.model.KafkaConsumerBean;
import com.digitalchina.xa.it.util.TConfigUtils;

@Controller
@RequestMapping(value = "/lottery")
public class LotteryController {
    @Autowired
   	private JdbcTemplate jdbc;
    @Autowired
    private KafkaUtil kafkaUtil;

//    private static String address = "0x861b6f2ca079e1cfa5da9b429fa9d82a6645b419";
	@ResponseBody
	@PostMapping("/buyTicket")
	public void processLessonBuy(
		@RequestParam(name = "itcode", required = true) String itcode,
		@RequestParam(name = "transactionDetailId", required = true) Integer transactionDetailId,
		@RequestParam(name = "turnBalance", required = true) BigInteger turnBalance){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode + "' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = TConfigUtils.selectValueByKey("default_password");
        String contractName = "LotteryBuyTicket";
        
        String contractAddress = TConfigUtils.selectValueByKey("lottery_contract");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String turndate = sdf.format(System.currentTimeMillis());
        BigInteger balance = turnBalance.divide(BigInteger.valueOf(10000000000000000L));
        
        //system_transactiondetail表，根据contracttype，contractid更新交易哈希，flag，获取gas并更新？
      	//jdbc.execute("insert into system_transactiondetail (fromcount,tocount,value,gas,turndate,flag,remark,fromitcode,toitcode,turnhash,timer,contracttype,contractid) values(" + ")");
        String sqlqqq = "insert into system_transactiondetail (fromcount,tocount,value,turndate,flag,fromitcode,toitcode,contracttype,contractid) values('"
      			+ list.get(0).get("account")  +  "','"
      			+ contractAddress +  "',"
      			+ balance  +  ",'"
      			+ turndate 
      			+  "',0," 
      			+ "'" + itcode + "',"
      			+ "'LotteryAdmin',"
      			+ "'LotteryBuyTicket',"
      			+ transactionDetailId + ")";
        System.err.println(sqlqqq);
      	jdbc.execute(sqlqqq);
      	
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(transactionDetailId, contractName, TConfigUtils.selectValueByKey("lottery_contract"), turnBalance, password, keystoreFile);
        kafkaUtil.sendMessage("lotteryBuyTicket", "LotteryBuyTicket", kafkabean);
	}
	
	@ResponseBody
	@PostMapping("/issueReward")
	public void processIssueReward(
		@RequestParam(name = "itcode", required = true) String itcode,
		@RequestParam(name = "transactionDetailId", required = true) Integer transactionDetailId,
		@RequestParam(name = "turnBalance", required = true) BigInteger turnBalance){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode + "' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = TConfigUtils.selectValueByKey("default_password");
        String contractName = "LotteryIssueSZBReward";
        String contractAddress = TConfigUtils.selectValueByKey("lottery_contract");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String turndate = sdf.format(System.currentTimeMillis());
        
        //system_transactiondetail表，根据contracttype，contractid更新交易哈希，flag，获取gas并更新？
      	//jdbc.execute("insert into system_transactiondetail (fromcount,tocount,value,gas,turndate,flag,remark,fromitcode,toitcode,turnhash,timer,contracttype,contractid) values(" + ")");
      	jdbc.execute("insert into system_transactiondetail (fromcount,tocount,value,turndate,flag,fromitcode,toitcode,contracttype,contractid) values('"
      			+ contractAddress +  "','"
      			+ list.get(0).get("account")  +  "',"
      			+ turnBalance  +  ",'"
      			+ turndate +  "',0,'LotteryAdmin','"
      			+ itcode + "','LotteryIssueSZBReward',"
      			+ transactionDetailId + ")");
      		
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(transactionDetailId, contractName, contractAddress, turnBalance, password, keystoreFile);
        kafkaUtil.sendMessage("lotteryIssueSZBReward", "LotteryIssueSZBReward", kafkabean);
	}
}
