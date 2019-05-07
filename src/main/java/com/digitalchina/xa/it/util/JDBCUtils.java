package com.digitalchina.xa.it.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
	
	//动态链接数据库所需参数
		private static final String DRIVER = "com.mysql.jdbc.Driver";
		// URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
		private static final String URL = "jdbc:mysql://10.0.5.106:4001/";
		
	
	
	// 连接数据库
		public static Connection getConn(String url, String username,String password) throws ClassNotFoundException, SQLException {
			Class.forName(DRIVER); // 动态加载mysql驱动
			Connection conn = DriverManager.getConnection(url, username, password); // 建立数据库链接
			return conn; // 返回数据库连接对象
		}

		// 释放资源
		public static void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
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
		public static int executeSQL(String sql, String itcode,String password) throws ClassNotFoundException, SQLException {
			Connection conn = JDBCUtils.getConn(URL + itcode, itcode,password);
			Statement stmt = conn.createStatement();
			// 对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate 的返回值总为零
			int number = stmt.executeUpdate(sql);
			JDBCUtils.closeAll(conn, stmt, null);
			return number;
		}

		// 执行SQL语句，可以进行查询操作
		// return 影响条数
		public static int executeQuerySQL(String sql, String itcode, String password) throws ClassNotFoundException, SQLException {
			Connection conn = JDBCUtils.getConn(URL + itcode, itcode,password);
			Statement stmt = conn.createStatement();
			// 对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate 的返回值总为零
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				JDBCUtils.closeAll(conn, stmt, null);
				return 1;
			}
			return 0;
		}
}
