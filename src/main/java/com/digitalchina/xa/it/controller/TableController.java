package com.digitalchina.xa.it.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.TConfigUtils;
import com.mysql.fabric.xmlrpc.base.Data;

@Controller
@RequestMapping(value = "/table")
public class TableController {
	@Autowired
	private JdbcTemplate jdbc;
	@ResponseBody
	@GetMapping("/createTable")
	@Transactional
	public Map<String, Object> getData(
			@RequestParam(name = "param", required = true) String param){
		
		String jsonValue = param.trim();
		System.out.println(jsonValue);
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		System.out.println(modelMap.get("success"));
		System.out.println(modelMap);
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		System.out.println(jsonObj);
		System.out.println(jsonObj.size());
		String field = "";
		String type = "";
		for (int i = 1; i <= jsonObj.size()/2-1; i++) {
			field +=jsonObj.getString("field"+i)+" "+jsonObj.getString("type"+i)+",";
		}
		String tableName = jsonObj.getString("tableName");
		String itcode = jsonObj.getString("itcode");
		System.out.println("查找数据库中有无重名表");
		List<Map<String,Object>> list = jdbc.queryForList("select table_name from information_schema.tables where table_schema='dc_blockchain' and table_name = '"+tableName+"'");
		System.out.println(list.size());
		if (!(list.size() == 0)) {
			modelMap.put("error", "表名已存在！");
			modelMap.put("success", false);
			return modelMap;
		}
		System.out.println("CREATE TABLE "+tableName+" ("
				+ field.substring(0, field.length()-1)
				+")");
		jdbc.execute("CREATE TABLE "+tableName+"("
				+ field.substring(0, field.length()-1)
				+")"
				);
		System.out.println("将操作记录记录至建表信息表中"+"INSERT INTO table_info (itcode,table_name,table_status,fields,create_time)"
				+ "VALUES('"+itcode+"','"+tableName+"',"+0+",'"+field.substring(0, field.length()-1)+new Timestamp(new Date().getTime())+"')");
		jdbc.execute("INSERT INTO table_info (itcode,table_name,table_status,fields)"
				+ "VALUES('"+itcode+"','"+tableName+"',"+0+",'"+field.substring(0, field.length()-1)+"')");
		modelMap.put("success", true);
		modelMap.put("msg", "建表成功");
		return modelMap;
	}
	@ResponseBody
	@GetMapping("/getTableList")
	public Map<String, Object> getTableList(@RequestParam(name = "itcode", required = true)String itcode){
		HashMap<String,Object> map = new HashMap<>();
		List<Map<String,Object>> list = jdbc.queryForList("select table_name from table_info where itcode = 'dede'");
		if (list.size()>0) {
			map.put("list", list);
			map.put("success", true);
			map.put("msg", "查找成功");
			return map;
		}else {
			map.put("msg", "您还没有建表");
			map.put("success", false);
			return map;
		}		
	}
	@ResponseBody
	@GetMapping("/getOne")
	public Map<String, Object> getTableInfoByTableName(@RequestParam(name = "tableName", required = true)String tableName){
		HashMap<String,Object> map = new HashMap<>();
		List<Map<String,Object>> list = jdbc.queryForList("select fields from table_info where table_name = '"+tableName+"'");
		if (list.size()>0) {
			map.put("list", list);
			map.put("success", true);
		}else {
			map.put("success", false);
			map.put("msg", "查找表数据异常");
		}
		return map;
	}
	@ResponseBody
	@GetMapping("/addDataToTable")
	@Transactional
	public Map<String, Object> addDataToTable(@RequestParam(name = "tableName",required = true)String tableName,
			@RequestParam(name = "itcode",required = true)String itcode,
			@RequestParam(name = "fieldNames",required = true)String fieldNames,
			@RequestParam(name = "fieldValues",required = true)String fieldValues){
		HashMap<String,Object> map = new HashMap<>();
		System.out.println("插入数据到表中"+"INSERT INTO "+tableName+"("+fieldNames+") VALUES ("+fieldValues+")");
		String data = "INSERT INTO "+tableName+"("+fieldNames+") VALUES ("+fieldValues+")";
		int result = jdbc.update(data);
		System.out.println(result);
		if (result == 0) {
			map.put("success", false);
			map.put("msg", "插入失败，请检查字段格式后重试");
			return map;
		}
		map.put("success", true);
		map.put("msg", "插入成功");
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp time = new Timestamp(new Date().getTime());
		String time1 = sdf.format(time);
		System.out.println("INSERT INTO add_data_detail (tableName,itcode,data,time) VALUES ('"+tableName+"','"+itcode+"',\""+data+"\",'"+time1+"')");
		jdbc.execute("INSERT INTO add_data_detail (tableName,itcode,data,time) VALUES ('"+tableName+"','"+itcode+"',\""+data+"\",'"+time1+"')");
		System.out.println(time);
		System.out.println("SELECT id FROM add_data_detail WHERE time = '"+time1+"'");
		List<Map<String,Object>> list = jdbc.queryForList("SELECT id FROM add_data_detail WHERE time = '"+time1+"'");
		int id = 0;
		for (Map<String, Object> map2 : list) {
			id = (int) map2.get("id");
			System.out.println(id);
		}
		String url = TConfigUtils.selectValueByKey("kafka_address")+"/tableKafka/addData";
		System.out.println(url);
		String postParam = "itcode="+itcode+"&tableName="+tableName+"&DataId = "+id;
		System.out.println(postParam);
		HttpRequest.sendPost(url, postParam);
		return map;
	}
	@ResponseBody
	@RequestMapping("/uploadFile")
	public Map<String, Object> uploadFile(@RequestParam MultipartFile file) throws IllegalStateException, IOException, ClassNotFoundException, SQLException {
		HashMap<String, Object> map = new HashMap<>();
		//获取文件名
		String filename = file.getOriginalFilename();
		HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
		int index = filename.indexOf(".");//首先获取字符的位置
		//去除文件后缀名做表名
		filename = filename.substring(0,index);
		System.out.println(filename);
		String itcode = "fannl";
		String sql = "";
		String fieldNames = "";
		String fieldValues = "";
		//获取表中第一个sheet
		HSSFSheet sheetAt = workbook.getSheetAt(0);
		//获取总行数
		int lastRowNum = sheetAt.getLastRowNum();
		for (int i = 0; i <= lastRowNum; i++) {
			HSSFRow row = sheetAt.getRow(i);
			//获取总列数
			short lastCellNum = row.getLastCellNum();
			if (i == 0) {
				for (int j = 0; j <= lastCellNum; j++) {
					if (row.getCell(j) == null) {
					} else {
						fieldNames += row.getCell(j) + " varchar(255),";
					}
				}
				String lString = "select table_name from information_schema.tables where table_name = '" + filename
						+ "'";
				//判断表名是否存在，如果存在不用创建新表，直接插入值
				int querySQL = executeQuerySQL(lString, itcode);
				if (querySQL == 1) {
					System.out.println("表已存在");
				} else {
					//创建表
					sql = "CREATE TABLE " + filename + "(" + fieldNames.substring(0, fieldNames.length() - 1) + ")";
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
				//每次插入一行值
				sql = "INSERT INTO " + filename + " VALUES(" + fieldValues.substring(0, fieldValues.length() - 1) + ")";
				System.out.println(sql);
				executeSQL(sql, itcode);
			}
		}
		
		return null;
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
