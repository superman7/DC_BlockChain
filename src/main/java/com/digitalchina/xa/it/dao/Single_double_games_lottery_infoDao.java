package com.digitalchina.xa.it.dao;

import org.apache.ibatis.annotations.Param;

public interface Single_double_games_lottery_infoDao {
	int updateNowSumAmountAndBackup4(@Param("id")int id);

}
