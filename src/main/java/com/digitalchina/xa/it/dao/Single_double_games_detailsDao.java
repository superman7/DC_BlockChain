package com.digitalchina.xa.it.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;

public interface Single_double_games_detailsDao {
	Integer insertLotteryBaseInfo(SingleDoubleGamesDetailsDomain singleDoubleGamesDetailsDomain);
	List<SingleDoubleGamesDetailsDomain> selectGameDetailsByItcodeAndLotteryId(@Param("itcode")String itcode, @Param("lotteryId")int lotteryId);
}
