package com.digitalchina.xa.it.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;

public interface Single_double_games_lottery_infoDao {
	int updateNowSumAmountAndBackup4(@Param("id")int id);
	
	SingleDoubleGamesInfoDomain selectOneSmbTpid();
	
	List<SingleDoubleGamesInfoDomain> selectNewOpen(@Param("count")int count);
	//根据Id获取抽奖信息
	SingleDoubleGamesInfoDomain selectLotteryInfoById(@Param("id")int id);

}
