package com.digitalchina.xa.it.service;

import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;

public interface GameService {
	//更新目前奖池和参与人员个数
	Boolean updateNowSumAmountAndBackup4(int id);
	int insertGameBaseInfo(SingleDoubleGamesDetailsDomain singleDoubleGamesDetailsDomain);
}
