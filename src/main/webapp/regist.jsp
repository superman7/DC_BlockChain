<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="weaver.conn.RecordSet"%>
<%@ page import="weaver.general.BaseBean"%>
<%@ page import="weaver.file.FileUpload"%>
<%@ page import="weaver.hrm.*" %>
<%@ page import="weaver.general.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="author" content="Weaver E-Mobile Dev Group" />
        <meta name="description" content="Weaver E-mobile" />
        <meta name="keywords" content="weaver,e-mobile" />
        <meta name="viewport" content="width=device-width,minimum-scale=1.0, maximum-scale=1.0" />
        <title>注册页面</title>
        <link rel="stylesheet" href="../css/trav.css"/>
        <link rel="stylesheet" href="../css/bootstrap.min.css" type="text/css" />
        <script src="../js/jquery-1.11.3.min.js" type="text/javascript"></script>
        <script src="../js/bootstrap.min.js" type="text/javascript"></script>
        <link rel="stylesheet" href="../css/weui.min.css" />
        <link rel="stylesheet" href="../css/jquery-weui.min.css" />
        <link rel="stylesheet" href="../css/icon.css" />
        <link rel="stylesheet" href="../css/task.css" />
        <link rel="stylesheet" href="../css/bootstrapValidator.min.css" />
        <link rel="stylesheet" href="../css/aui_css/aui.css" />
        <link rel="stylesheet" type="text/css" href="../css/aui_css/aui-pull-refresh.css" />
        <script type="text/javascript" src='../js/cookie_util.js'></script>
        <script type='text/javascript' src='../js/jquery.textarea.autoheight.js'></script>
        <script type='text/javascript' src='../js/jquery.form.js'></script>
        <script type='text/javascript' src="../js/jquery-weui.js"></script>
        <script type='text/javascript' src='../js/fastclick.min.js'></script>
        <script type='text/javascript' src='../js/web3.min.js'></script>
        <script type='text/javascript' src='../js/bignumber.js'></script>
        <script type='text/javascript' src='../js/bootstrapValidator.min.js'></script>
        <script type="text/javascript" src="../js/aui_script/api.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-tab.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-pull-refresh.js"></script>
        <script type="text/javascript" src="../js/aui_script/aui-toast.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-dialog.js" ></script>
		<script type="text/javascript" src='../js/cookie_util.js'></script>
			<script type="text/javascript">
			var baseUrl = '/';
			$(function(){
				$("#regist_btn").click(regist);
			});
			function regist(){
			//	获取参数
				var username = $("#username").val().trim();
				var u_pwd = $("#u_pwd").val().trim();
				var final_u_pwd = $("#final_u_pwd").val().trim();
				$("#username_msg").html("");
				$("#pwd_msg").html("");
				$("#final_pwd_msg").html("");
			//	检查格式
				ok = true;
				if(username == ""){
					ok = false;
					$("#username_msg").html("账号不能为空");
				}
				if(!(username.length > 2 && username.length < 17)){
					ok = false;
					$("#username_msg").html("账号长度不合法(3-16位)");
				}
				
				if(u_pwd == ""){
					ok = false;
					$("#pwd_msg").html("密码不能为空");
				}
				if(!(u_pwd.length>8 && u_pwd.length < 17)){
					ok = false;
					$("#pwd_msg").html("密码长度太短(9-16位)");
				}
				var reg = /^[a-zA-Z0-9_\.]+$/;
				if(!(reg.test(u_pwd))){
					ok = false;
					$("#pwd_msg").html("密码格式不正确(密码由字母、数字、下划线和点组成，区分大小写)");
				}
				if(final_u_pwd == ""){
					ok = false;
					$("#final_pwd_msg").html("确认密码不能为空");
				}
				
				if(!(u_pwd == final_u_pwd)){
					ok = false;
					$("#final_pwd_msg").html("密码不一致");
				}
				if(ok){
					$.ajax({
						url:"/eth/getBalance",
						type:"get",
						data:{"username":username,"password":u_pwd},
						dataType:"text",
						success:function(data){
							alert(data);
							alert("注册成功,将跳转至登录界面!");							
							window.location.href= "/login.jsp";
						},
						error:function(){
							alert("注册异常");
						}
					});
				}
			}
			</script>
</head>
<body>
					 <h1 align="center"class=" aui-text-info">注册</h1>
                    <form >
                        <input type="text" id="username" class="aui-input" placeholder="请输入账号"><span id="username_msg"></span>
                        <input type="password" id="u_pwd" class="aui-input" placeholder="请输入密码"><span id="pwd_msg"></span>
                        <input type="password" id="final_u_pwd" class="aui-input" placeholder="请再次输入密码"><span id="final_pwd_msg"></span>
                        <div align="center" ><button type="button" id="regist_btn" class="aui-text-info " >注册</button></div>
                    </form>
</body>
</html>