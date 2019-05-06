package com.digitalchina.xa.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.controller.GameController;
import com.digitalchina.xa.it.controller.PaidLotteryController;
import com.digitalchina.xa.it.dao.Single_double_games_detailsDao;
import com.digitalchina.xa.it.dao.Single_double_games_lottery_infoDao;
import com.digitalchina.xa.it.dao.TConfigDAO;
import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;
import com.digitalchina.xa.it.model.TConfigDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.EthAccountService;
//import com.digitalchina.xa.it.dao.Single_double_games_detailsDao;
import com.digitalchina.xa.it.service.GameService;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.TConfigUtils;

import net.sf.jsqlparser.statement.select.Select;
import scala.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
	// @Autowired
	// private JdbcTemplate jdbc;
	// @Autowired
	// private GameService gameService;
	// @Autowired
	// private Single_double_games_detailsDao single_double_games_detailsDao;
	// @Autowired
	// private EthAccountService ethAccountService;
	// @Autowired
	// private GameController gameController;
	// @Autowired
	// private TConfigDAO tconfigDAO;
	// @Autowired
	// private PaidLotteryController p;
	// private static String[] ip =
	// {"http://10.7.10.124:8545","http://10.7.10.125:8545","http://10.0.5.217:8545","http://10.0.5.218:8545","http://10.0.5.219:8545"};
	// @Autowired
	// private Single_double_games_lottery_infoDao
	// single_double_games_lottery_infoDao;
	@org.junit.Test
	public void insertNewBlock() throws IOException, ClassNotFoundException, SQLException {
		FileInputStream inputStream = new FileInputStream(new File("F:/123.xls"));
		String sheetName = "hhhppp";
		HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
		System.out.println(sheetName);
		String itcode = "fannl";
		String sql = "";
		String fieldNames = "";
		String fieldValues = "";
		HSSFSheet sheetAt = workbook.getSheetAt(0);
		int lastRowNum = sheetAt.getLastRowNum();
		for (int i = 0; i <= lastRowNum; i++) {
			HSSFRow row = sheetAt.getRow(i);
			short lastCellNum = row.getLastCellNum();
			if (i == 0) {
				for (int j = 0; j <= lastCellNum; j++) {
					if (row.getCell(j) == null) {
					} else {
						fieldNames += row.getCell(j) + " varchar(255),";
					}
				}
				String lString = "select table_name from information_schema.tables where table_name = '" + sheetName
						+ "'";
				int querySQL = executeQuerySQL(lString, itcode);
				if (querySQL == 1) {
					System.out.println("表已存在");
				} else {
					sql = "CREATE TABLE " + sheetName + "(" + fieldNames.substring(0, fieldNames.length() - 1) + ")";
					System.out.println(sql);
					executeSQL(sql, itcode);
					System.out.println(fieldNames);
				}
			} else {
				fieldValues = "";
				for (int j = 0; j <= lastCellNum; j++) {
					if (!(row.getCell(j) == null)) {
						fieldValues += "'"+row.getCell(j) + "',";
					}
				}
				sql = "INSERT INTO " + sheetName + " VALUES(" + fieldValues.substring(0, fieldValues.length() - 1) + ")";
				System.out.println(sql);
				executeSQL(sql, itcode);
			}
		}
	}

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	// URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
	private static final String URL = "jdbc:mysql://10.0.5.106:4001/";
	private static final String PASSWORD = "0x189a";

	// 连接数据库
	public Connection getConn(String url, String username) throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER); // 动态加载mysql驱动
		Connection conn = DriverManager.getConnection(url, username, PASSWORD); // 建立数据库链接
		return conn; // 返回数据库连接对象
	}

	// 释放资源
	public void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		if (conn != null) {
			conn.close();
		}
	}

	// 执行SQL语句，可以进行增、删、改的操作
	// return 影响条数
	public int executeSQL(String sql, String itcode) throws ClassNotFoundException, SQLException {
		Connection conn = this.getConn(URL + itcode, itcode);
		Statement stmt = conn.createStatement();
		// 对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate 的返回值总为零
		int number = stmt.executeUpdate(sql);
		this.closeAll(conn, stmt, null);
		return number;
	}

	// 执行SQL语句，可以进行查询操作
	// return 影响条数
	public int executeQuerySQL(String sql, String itcode) throws ClassNotFoundException, SQLException {
		Connection conn = this.getConn(URL + itcode, itcode);
		Statement stmt = conn.createStatement();
		// 对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate 的返回值总为零
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String string = rs.getString(1);
			this.closeAll(conn, stmt, null);
			return 1;
		}
		return 0;
	}

}