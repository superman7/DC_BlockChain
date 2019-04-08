package com.digitalchina.xa.it.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitalchina.xa.it.dao.Single_double_games_detailsDao;
import com.digitalchina.xa.it.dao.Single_double_games_lottery_infoDao;
import com.digitalchina.xa.it.model.SingleDoubleGamesDetailsDomain;
import com.digitalchina.xa.it.service.GameService;
@Service(value="gameService")
public class GameServiceimpl implements GameService{
	@Autowired
	private Single_double_games_lottery_infoDao single_double_games_lottery_infoDao;
	@Autowired
	private Single_double_games_detailsDao gameDetailsDao;
	@Override
	public Boolean updateNowSumAmountAndBackup4(int id) {
		try {
			Integer effectedNumber = single_double_games_lottery_infoDao.updateNowSumAmountAndBackup4(id);
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

}
