package com.digitalchina.xa.it;




import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//import com.digitalchina.xa.it.dao.Single_double_games_detailsDao;
import com.digitalchina.xa.it.service.GameService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
	@Autowired
	private GameService gameService;
	
//	@Autowired
//	private Single_double_games_detailsDao dao;
	@org.junit.Test
	public void insertNewBlock(){
		
		int list = gameService.generateWinTicketNew(1, 2, 2);
		System.out.println(list);
		//		List<String> list = dao.generateWinTicketNew1(2, 0);
//		for (String string : list) {
//			System.out.println(string.toString());
//		}
	}
	
}