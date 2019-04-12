package com.digitalchina.xa.it.dao;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;

public interface Single_double_games_lottery_infoDao {
	int updateNowSumAmountAndBackup4(@Param("id")int id);
	
	SingleDoubleGamesInfoDomain selectOneSmbTpid();
	
	List<SingleDoubleGamesInfoDomain> selectNewOpen(@Param("count")int count);
	//根据Id获取抽奖信息
	SingleDoubleGamesInfoDomain selectLotteryInfoById(@Param("id")int id);
	
	void updateLotteryWinBlockHash(@Param("id")int lotteryId,@Param("winBlockHash")String winBlockHash);
	List<SingleDoubleGamesInfoDomain>  selectRunLottery();
	
	//更新flag，lotteryTime，winner，winTicket
	int updateAfterLotteryFinished(@Param("id")int id, @Param("lotteryTime")Timestamp lotteryTime, @Param("winner")String winner, @Param("winTicket")String winTicket, @Param("backup6")int backup6);
		
	//获取flag=0的奖项
	List<SingleDoubleGamesInfoDomain> selectUnfinishedLottery();
	
	//插入抽奖信息
	void insertLotteryInfo(TPaidlotteryInfoDomain tplid);
	//成功交易个数与backup4相等时，backup4更新为0
	int updateBackup4To0(@Param("id")int id);
		
}
