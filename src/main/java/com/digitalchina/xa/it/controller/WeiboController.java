package com.digitalchina.xa.it.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.digitalchina.xa.it.util.GetPersonalDBPwdUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.JDBCUtils;
import com.digitalchina.xa.it.util.TConfigUtils;
import com.digitalchina.xa.it.weibo.weibo4j.Oauth;
import com.digitalchina.xa.it.weibo.weibo4j.Timeline;
import com.digitalchina.xa.it.weibo.weibo4j.http.AccessToken;
import com.digitalchina.xa.it.weibo.weibo4j.model.Status;
import com.digitalchina.xa.it.weibo.weibo4j.model.StatusWapper;
import com.digitalchina.xa.it.weibo.weibo4j.model.WeiboException;
import com.digitalchina.xa.it.weibo.weibo4j.util.BareBonesBrowserLaunch;



@Controller
@RequestMapping(value = "/weibo")
public class WeiboController {
	
	@Autowired
	private JdbcTemplate jdbc;
	
	/**
	 * @throws WeiboException 
	 * @throws IOException 
	 * @api {get} /table/uploadFile 将上传的excel表格中数据保存到数据库中
	 * @apiVersion 0.0.1
	 * 
	 * @apiName uploadFile
	 * @apiGroup TiDBGroupCreate
	 *
	 * @apiParam {String} itcode 用户的itcode.
	 * @apiParam {MultipartFile} file 上传的Excel文件.文件名不能以数字开头。上传文件内容格式必须为第一行为表字段，其他行是相应字段数据
	 * @apiParam file文件例 文件名：学生.xls    第一行：姓名 年龄 班级 。。。  第二行：杜伟 21 应数151 。。。 第三行。。。

	 *
	 * @apiSuccess {Boolean} success  是否上传成功，false未成功，可能原因：文件名以数字开头（数据库不支持表名为数字开头）；所传文件格式不正确（非Excel表形式）.
	 * @apiSuccess {String} msg  查询结果信息提示.
	 * 
	 * @apiSuccessExample Success-Response: 返回结果示例1
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "上传成功",
	 *         "success": true
	 *     }
	 *     
	 * @apiSuccessExample Success-Response: 返回结果示例2
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "文件名不能以数字开头哦",
	 *         "success": false
	 *     }
	 * @apiSuccessExample Success-Response: 返回结果示例3
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "请检查文件格式是否为Excel格式",
	 *         "success": false
	 *     }
	 * @apiSuccessExample Success-Response: 返回结果示例3
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "插入数据失败，请检查数据格式",
	 *         "success": false
	 *     }
	 */
	@ResponseBody
	@RequestMapping("/saveFriends")
	public Map<String, Object> saveFriends(@RequestParam String itcode) throws WeiboException, IOException{

		Oauth oauth = new Oauth();
		BareBonesBrowserLaunch.openURL(oauth.authorize("code"));
		System.out.println(oauth.authorize("code"));
		System.out.print("Hit enter when it's done.[Enter]:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();
//		Log.logInfo("code: " + code);
		try{
			AccessToken accessToken = oauth.getAccessTokenByCode(code);
			String token = accessToken.getAccessToken();
//			String token = "2.00D5amKDKJkQfE8a57353facqDv5UD";
			Timeline tm = new Timeline(token);
			try {
				StatusWapper status = tm.getUserTimeline();
				List<Status> statuses = status.getStatuses();
				for (Status status2 : statuses) {
					System.out.println(status2.getUser()+"1111111");
				}
//				Log.logInfo(status.toString());
			} catch (WeiboException e) {
				e.printStackTrace();
			}
		} catch (WeiboException e) {
			if(401 == e.getStatusCode()){
//				Log.logInfo("Unable to get the access token.");
			}else{
				e.printStackTrace();
			}
		}
	
		return null;
		
	}

	
}
