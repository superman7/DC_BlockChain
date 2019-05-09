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
        <title>登录页面</title>
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
			//	用户点击登录按钮时候，触发事件
				$("#login_btn").click(checkLogin);
			});
			function checkLogin(){
			//	获取参数
				var username = $("#username").val();
				var u_pwd = $("#u_pwd").val().trim();
				$("#username_msg").html("");
				$("#pwd_msg").html("");
			//	检查格式
				ok = true;
				if(username == ""){
					ok = false;
					$("#username_msg").html("账号不能为空");
				}
				if(u_pwd == ""){
					ok = false;
					$("#pwd_msg").html("密码不能为空");
				}
				if(ok){
					$.ajax({
						url:baseUrl+"ethAccount/login",
						type:"post",
						data:{"username":username,"u_pwd":u_pwd},
						dataType:"json",
						success:function(result){
							var s = result.data;
							if(result.status == 0){
								addCookie("username",username,2);
								addCookie("u_pwd",u_pwd,2);
								addCookie("itcode",result.data,2);
								window.location.href = "/table/tableList.jsp";
							}else if(result.status==1){
								$("#username_msg").html(result.msg);
							}else if(result.status==2){
								$("#pwd_msg").html(result.msg);
							}
						},
						error:function(){
							alert("登录异常");
						}
					});
				}
			}
			</script>
</head>
<body>
					 <h1 align="center"class=" aui-text-info">登录</h1>
                    <form >
                        <input type="text" id="username" class="aui-input" placeholder="请输入账号"><span id="username_msg"></span>
                        <input type="text" id="u_pwd" class="aui-input" placeholder="请输入密码"><span id="pwd_msg"></span>
                        <div align="center" ><button type="button" id="login_btn" class="aui-text-info " >登录</button></div>
                    </form>
</body>
</html>