<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.digitalchina.xa.it.util.Encrypt" %>
<%@ page import="com.digitalchina.xa.it.util.EncryptImpl" %>
<%
	request.setCharacterEncoding("UTF-8");
	String jsonStr = request.getParameter("jsonStr");
	
	try{
 		String s = URLEncoder.encode(jsonStr,"utf-8");
		Encrypt encrypt = new EncryptImpl();
		String result = encrypt.encrypt(s);
		out.print(result.trim());
	}catch(Exception e){
		out.print("加密异常为:"+e.toString());
		//bbbu.writeLog("加密异常为:"+e.toString());
	}
	// out.print(url + jsonStr);
%>