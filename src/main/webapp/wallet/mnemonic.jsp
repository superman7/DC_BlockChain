<%@page import="java.net.URLEncoder"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="weaver.conn.RecordSet"%>
<%@ page import="weaver.general.BaseBean"%>
<%@ page import="weaver.file.FileUpload"%>
<%@ page import="weaver.hrm.*" %>
<%@ page import="weaver.general.*" %>
<%@ page import="com.digitalchina.xa.it.util.Encrypt" %>
<%@ page import="com.digitalchina.xa.it.util.EncryptImpl" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="author" content="Weaver E-Mobile Dev Group" />
        <meta name="description" content="Weaver E-mobile" />
        <meta name="keywords" content="weaver,e-mobile" />
        <meta name="viewport" content="width=device-width,minimum-scale=1.0, maximum-scale=1.0" />
        <title>测试标签页面</title>
        <link rel="stylesheet" href="../css/trav.css"/>
        <link rel="stylesheet" href="../css/bootstrap.min.css" type="text/css" />
        <script src="../js/jquery-1.11.3.min.js" type="text/javascript"></script>
        <script src="../js/bootstrap.min.js" type="text/javascript"></script>
        <link rel="stylesheet" href="../css/weui.min.css" />
        <link rel="stylesheet" href="../css/jquery-weui.min.css" />
        <link rel="stylesheet" href="../css/icon.css" />
        <link rel="stylesheet" href="../css/task.css" />
        <script type='text/javascript' src='../js/jquery.textarea.autoheight.js'></script>
        <script type='text/javascript' src='../js/jquery.form.js'></script>
        <script type='text/javascript' src="../js/jquery-weui.js"></script>
        <script type='text/javascript' src='../js/fastclick.min.js'></script>
        <script type='text/javascript' src='../js/web3.min.js'></script>
        <script type='text/javascript' src='../js/bignumber.js'></script>
        <script type='text/javascript' src='../js/check_login.js'></script>
		<script type="text/javascript" src='../js/cookie_util.js'></script>
        <script type="text/javascript">
        /* 生成密语 */
        $(function() {
        	var itcode = getCookie("itcode");
            alert("请保存好您的密语与密语密码，我们不会为您保存，如果丢失没有办法为您恢复");
            $.ajax({
                type: "GET",
                url: "/ethAccount/refreshMnemonic",
                data: {},
                dataType: "json",
                success: function(data) {
                    if (data.success) {
                        $("#taMnemonic").val(data.mnemonic);
                    }
                }
            });
			/* 重选密语 */
            $("#btnRefresh").click(function() {
                $.ajax({
                    type: "GET",
                    url: "/ethAccount/refreshMnemonic",
                    data: {},
                    dataType: "json",
                    success: function(data) {
                        if (data.success) {
                            $("#taMnemonic").val(data.mnemonic);
                        }
                    }
                });
            });

            

            $("#btnOk").click(function() {
                var mnemonic = $("#taMnemonic").val();
                var mnePassword = $("#txtPassword").val();
                /* var param = JSON.stringify({
                        "mnemonic":mnemonic,
                        "mnePassword":mnePassword,
                        "itcode":itcode
                    }); */
               
                if(confirm("再次提醒您保存好密语与密语密码，丢失后无法恢复") == true){
                    $.ajax({
                        type: "GET",
                        url: "/ethAccount/newAddress",
                        data: {
                        	"mnemonic":mnemonic,
                            "mnePassword":mnePassword,
                            "itcode":itcode
                            },
                        dataType: "json",
                        success: function(data) {
                            if (data.success) {
                                var address = data.address;
                                var mnemonic = data.mnemonic;
                                var mnePassword = data.mnePassword;
                                window.location.href="/wallet/newAccount.jsp"
                                     +"?mnemonic=" + mnemonic +"&"+"address=" + address + "&mnePassword=" + mnePassword + "&itcode=" + itcode;
                            } else {
                                alert("您的账户数量与今日注册次数已累计达到10次，请明日再试！");
                                window.location.href = "/wallet/amBase.jsp";
                            }
                        }
                    });
                }
            });
        })
    </script>
</head>
    <body>
        <div class="container" style="">
            <div class="row">
                <div class="col-xs-12 col-md-12" align="center">
                    <h4 class="form-title" style="float: left; display: line-block;">创建新密语</h4>
                    <div class="form-group">
                        <textarea id="taMnemonic" class="form-control required" readonly="readonly" style="height: 70px;"></textarea>
                    </div><br>
                    <h4 style="float: left;">密语密码</h4>
                    <div class="form-group">
                        <input id="txtPassword" class="form-control required" placeholder="非必填"/>
                    </div>
                    <div class="form-group">
                        <button id="btnRefresh" type="button" class="btn btn-default btn-md">重选密语</button>
                        <button id="btnOk" type="button" class="btn btn-info btn-md">确定</button>
                    </div>
                </div>
            </div>
        </div>  
    </body>
</html>