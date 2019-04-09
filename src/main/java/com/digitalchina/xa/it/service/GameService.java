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
	//获取游戏列表
	SingleDoubleGamesInfoDomain selectOneSmbTpid();
	//查找最新获奖信息
	List<SingleDoubleGamesInfoDomain> selectNewOpen(int count);
	//根据id获取抽奖Info
	SingleDoubleGamesInfoDomain selectLotteryInfoById(int id);
}
