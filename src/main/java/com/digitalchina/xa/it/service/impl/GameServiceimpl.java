package com.digitalchina.xa.it.service.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;

import com.digitalchina.xa.it.dao.Single_double_games_detailsDao;
import com.digitalchina.xa.it.dao.Single_double_games_lottery_infoDao;
import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;
import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.GameService;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.MerkleTrees;
import com.digitalchina.xa.it.util.TConfigUtils;
@Service(value="gameService")
public class GameServiceimpl implements GameService{
	@Autowired
	private Single_double_games_lottery_infoDao single_double_games_lottery_infoDao;
	@Autowired
	private Single_double_games_detailsDao single_double_games_detailsDao;
	@Autowired
	private Single_double_games_detailsDao gameDetailsDao;
	@Override
	public Boolean updateNowSumAmountAndBackup4(int id,int money) {
		try {
			Integer effectedNumber = single_double_games_lottery_infoDao.updateNowSumAmountAndBackup4(id,money);
			System.out.println(effectedNumber);
			if(effectedNumber > 0) {
				return true;
			} else {
				throw new RuntimeException("updateNowSumAmountAndBackup4失败");
			}
		} catch(Exception e) {
			throw new RuntimeException("updateNowSumAmountAndBackup4失败 " + e.getMessage());
		}
	}

	@Override
	public int insertGameBaseInfo(SingleDoubleGamesDetailsDomain singleDoubleGamesDetailsDomain) {
		if(singleDoubleGamesDetailsDomain != null) {
			try {
				Integer effectedNumber = gameDetailsDao.insertLotteryBaseInfo(singleDoubleGamesDetailsDomain);
				if(effectedNumber > 0) {
					System.out.println(singleDoubleGamesDetailsDomain.getId());
					return singleDoubleGamesDetailsDomain.getId();
				} else {
					throw new RuntimeException("插入购买奖票信息失败");
				}
			} catch(Exception e) {
				throw new RuntimeException("插入购买奖票信息失败 " + e.getMessage());
			}
		} else {
			throw new RuntimeException("tPaidlotteryDetailsDomain为null");
		}
	}

	@Override
	public SingleDoubleGamesInfoDomain selectOneSmbTpid() {
		// TODO Auto-generated method stub
		return single_double_games_lottery_infoDao.selectOneSmbTpid();
	}

	@Override
	public List<SingleDoubleGamesInfoDomain> selectNewOpen(int count) {
		// TODO Auto-generated method stub
		return single_double_games_lottery_infoDao.selectNewOpen(count);
	}

	@Override
	public SingleDoubleGamesInfoDomain selectLotteryInfoById(int id) {
		// TODO Auto-generated method stub
		return single_double_games_lottery_infoDao.selectLotteryInfoById(id);
	}

	@Override
	public List<SingleDoubleGamesDetailsDomain> selectGameDetailsByItcodeAndLotteryId(String itcode, int lotteryId) {
		// TODO Auto-generated method stub
		return single_double_games_detailsDao.selectGameDetailsByItcodeAndLotteryId(itcode, lotteryId);
	}

	
	@Override
	public int generateWinTicketNew(int lotteryId, int winCount, int option) {
		Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
		int i = 404;
		try {
			Block winBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getResult();
			BigInteger blockNumber = winBlock.getNumber();
			System.out.println(blockNumber);
			String winBlockHash = String.valueOf(winBlock.getHash());
			System.out.println(winBlockHash);
			i = winBlockHash.charAt(winBlockHash.length()-1)%2;
//			i为中奖号码，单或双
			System.out.println(i);
			
//		    List<String> ticketList = single_double_games_detailsDao.generateWinTicketNew1(lotteryId, option);
//		    BigInteger ticketListSize = new BigInteger(String.valueOf(ticketList.size() - 1));
//		    System.err.println(ticketListSize);
//		    System.err.println(temp1.divideAndRemainder(ticketListSize)[1].toString());
//		    System.err.println(ticketList.get(Integer.valueOf(temp1.divideAndRemainder(ticketListSize)[1].toString())));
		    //添加中奖数字
//		    result.add(i);
		    //更新当期开奖区块hash
		    single_double_games_lottery_infoDao.updateLotteryWinBlockHash(lotteryId,winBlockHash);
		    return i;
		} catch (IOException e) {
			
			e.printStackTrace();
			return i;
		}
	}

	@Override
	public void runALottery(SingleDoubleGamesInfoDomain sdid) {
		//开奖，根据lotteryId，更新此次参与者的result，winTicket，winReword字段,更新t_paidlottery_info表flag，lotteryTime，winner，winTicket
		int ticket = generateWinTicketNew(sdid.getId(), sdid.getWinCount(), 0);
		List<SingleDoubleGamesDetailsDomain> tpddList = single_double_games_detailsDao.selectLotteryDetailsByLotteryId(sdid.getId());
		String winTickets = String.valueOf(ticket);
		String winItcodes = "";
		int winSumAmount = 0;
		for(int index1 = 0; index1 < tpddList.size(); index1++) {
			SingleDoubleGamesDetailsDomain sdddTemp = tpddList.get(index1);
			System.out.println(sdddTemp.getId());
			System.out.println(sdddTemp.getTicket());
			System.out.println(ticket);
			if(sdddTemp.getTicket().equals(String.valueOf(ticket))) {
				int lotteryFinished = single_double_games_detailsDao.updateDetailAfterLotteryFinished(sdddTemp.getId(), 2, winTickets, sdddTemp.getBackup5().toString());
				sdddTemp.setResult(2);
				winItcodes +=sdddTemp.getItcode()+"&";
				winSumAmount += sdddTemp.getBackup6();
				String url = TConfigUtils.selectValueByKey("kafka_address") + "/gameKafka/issueReward";
				String postParam = "itcode="+sdddTemp.getItcode() + "&turnBalance=" + sdddTemp.getBackup6() + "&transactionDetailId="+sdddTemp.getLotteryId()+"&choosed="+sdddTemp.getBackup5();
				System.out.println(url);
				System.out.println(postParam);
				//向kafka发送请求，参数为itcode, transactionId,  金额？， lotteryId？; 产生hashcode，更新account字段，并返回hashcode与transactionId。
				HttpRequest.sendPost(url, postParam);
			}else if(sdddTemp.getResult() != 2) {
				single_double_games_detailsDao.updateDetailAfterLotteryFinished(sdddTemp.getId(), 1, winTickets, "无");
			}
		}
		try {			
			System.out.println(winItcodes);
			winItcodes = winItcodes.substring(0, winItcodes.length() - 1);
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		int i = single_double_games_lottery_infoDao.updateAfterLotteryFinished(sdid.getId(),new Timestamp(new Date().getTime()), winItcodes ,winTickets,winSumAmount,0);
		System.out.println(i);
	}

	@Override
	public Boolean updateHashcodeAndJudge(String hashcode, int transactionId) {
		//根据transactionId获取lotteryId
		SingleDoubleGamesDetailsDomain tpdd = single_double_games_detailsDao.selectLotteryDetailsById(transactionId);
		int lotteryId = tpdd.getLotteryId();
		//计算ticket值,更新该用户的ticket值。
		String ticket = tpdd.getBackup5().toString();
		single_double_games_detailsDao.updateTicket(ticket, transactionId);			
		return true;
	}
}
