package com.digitalchina.xa.it.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;
import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;

public interface GameService {
	//更新目前奖池和参与人员个数
	Boolean updateNowSumAmountAndBackup4(int id);
	int insertGameBaseInfo(SingleDoubleGamesDetailsDomain singleDoubleGamesDetailsDomain);
	//获取未开奖游戏列表
	SingleDoubleGamesInfoDomain selectOneSmbTpid();
	//查找最新获奖信息
	List<SingleDoubleGamesInfoDomain> selectNewOpen(int count);
	//根据id获取抽奖Info
	SingleDoubleGamesInfoDomain selectLotteryInfoById(int id);
	//根据itcode、lotteryId查询某用户的某次押注购买记录
	List<SingleDoubleGamesDetailsDomain> selectGameDetailsByItcodeAndLotteryId(String itcode,int lotteryId);
	//生成中奖号码
	int  generateWinTicketNew(int lotteryId, int winCount, int option);
	//定時任務自動開獎
	void runALottery(SingleDoubleGamesInfoDomain sdid);
	 
}
