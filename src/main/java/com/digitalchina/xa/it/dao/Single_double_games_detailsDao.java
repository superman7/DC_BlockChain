package com.digitalchina.xa.it.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;

public interface Single_double_games_detailsDao {
	Integer insertLotteryBaseInfo(SingleDoubleGamesDetailsDomain singleDoubleGamesDetailsDomain);
	List<SingleDoubleGamesDetailsDomain> selectGameDetailsByItcodeAndLotteryId(@Param("itcode")String itcode, @Param("lotteryId")int lotteryId);
	List<String> generateWinTicketNew1(@Param("lotteryId")int id,@Param("backup4")int backup4);
	List<SingleDoubleGamesDetailsDomain> selectLotteryDetailsByLotteryId(@Param("lotteryId")int lotteryId);
	//开奖后更新result，winTicket，winReword
	int updateDetailAfterLotteryFinished(@Param("id")int id, @Param("result")int result, @Param("winTicket")String winTicket, @Param("winReward")String winReward);
	//根据lottery，backup3查询个数
	int selectCountByBackup3(@Param("lotteryId")int lotteryId, @Param("backup3")int backup3);
	//定时任务使用，更新超时任务
	void updateLotteryDetailsWhereTimeOut();
	//定时任务使用，查询Hash值不为空，backup为
	List<SingleDoubleGamesDetailsDomain> selectLotteryDetailsWhereHashIsNotNullAndBackup3Is0();
	//根据lottery，backup3查询个数
	List<SingleDoubleGamesDetailsDomain> selectDetailByBackup3(@Param("lotteryId")int lotteryId, @Param("backup3")int backup3);
	//将backup3=2改为3
	int updateBackup3From2To3(@Param("id")int id);
}
