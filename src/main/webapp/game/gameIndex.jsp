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
        <title>夺宝首页</title>
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
        <link rel="stylesheet" href="../css/bootstrap-select.min.css" />
        <script type='text/javascript' src='../js/jquery.textarea.autoheight.js'></script>
        <script type='text/javascript' src='../js/jquery.form.js'></script>
        <script type='text/javascript' src="../js/jquery-weui.js"></script>
        <script type='text/javascript' src='../js/fastclick.min.js'></script>
        <script type='text/javascript' src='../js/web3.min.js'></script>
        <script type='text/javascript' src='../js/bignumber.js'></script>
        <script type='text/javascript' src='../js/bootstrapValidator.min.js'></script>
        <script type='text/javascript' src='../js/bootstrap-select.min.js'></script>
        <script type="text/javascript" src="../js/aui_script/api.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-tab.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-pull-refresh.js"></script>
        <script type='text/javascript' src='../js/check_login.js'></script>
		<script type="text/javascript" src='../js/cookie_util.js'></script>
        <script type="text/javascript">
            var itcode = getCookie("itcode");
            var dataJson = {};
            var baseUrl = '/';
            $(function(){
	            $.ajax({
	                type: "GET", 
	                url: "/wallet/getCheckUp.jsp",
	                data: {"jsonStr" : JSON.stringify({
	                	"itcode" : itcode
	                })},
	                dataType: "text",
	                success: function(data) {
		                $.ajax({
		                    type: "GET",
		                    url: "/ethAccount/accountList",
		                    data: {"param" : data},
		                    dataType: "json",
		                    success: function(data) {
		                        if (data.success) {
		                            $(data.accountList).each(function(index, value) {
		                                dataJson[value.alias] = {
		                                    "account" : value.account,
		                                    "balance" : value.balance,
		                                    "available" : value.available,
		                                    "alias" : value.alias
		                                }
		                               if (value.available != 0) {
	/* 	                                    var account = "<option value='" + value.account + "'></option>"; */
		                                    var innerHtml = "<option value='" + value.account + "'>" + value.alias + "</option>";
		                                    $("#selectGroup").append(innerHtml);
		                                    
		                                }
		                            });
		                                $('.selectpicker').selectpicker('refresh');
	 	                                $("#balance").val("账户余额:" + dataJson[$("#selectGroup").children('option:selected').text()].balance  + " SMB");	                         } else {
		                            alert("异常，请稍后再试");
		                        }
		                    }
		                });
	                }
	            });
	            $.ajax({
                    type: "GET",
                    url: "/wallet/getCheckUp.jsp",
                    data: {"jsonStr" : JSON.stringify({
                        "itcode" : itcode,
                        "id" : id
                    })},
                    dataType: "text",
                    success: function(data) {
			            $.ajax({
		                    type: "GET",
		                    url: "/game/getOne",
		                    data: {"param" : data},
		                    dataType: "json",
		                    success: function(data) {
		                        if (data.success) {
		                        	
		                        }
		                    }
			            });
                    }
	            $("#selectGroup").change(function() {
                    var selectedAlias = $("#selectGroup").children('option:selected').text();
                    var account = $(this).children('option:selected').attr("value");
                    var balance = dataJson[selectedAlias].balance;
                    $("#balance").val(balance + " SMB");
                });
	            $("#btnConfirm").click(function() {
	            	
                    var money = $("#money").val();
                    var balance = $("balance").val();
                    if(Number(balance <= 0)){
                    	alert("账户金额不足请及时充值");
                    	return;
                    }
                    if(Number(money)>Number(balance))){
                    	alert("请输入合法的金额");
                		return;    
	            	}
	            $(function(){
		            $.ajax({
		                type: "GET", 
		                url: "/wallet/getCheckUp.jsp",
		                data: {"jsonStr" : JSON.stringify({
		                	"itcode" : itcode,
		                	"ganme_no":id,
		                	"account": account,
		                	"money" : money,
		                	
		                	
		                })},
		                dataType: "text",
		                success: function(data) {
			                $.ajax({
			                    type: "GET",
			                    url: "/ethAccount/accountList",
			                    data: {"param" : data},
			                    dataType: "json",
			                    success: function(data) {
	            }
            });
            
        </script>
    </head>
    <body>
    <h1 align="center">猜单双游戏</h1>
    <div class="aui-content aui-margin-b-15">
    <ul class="aui-list aui-list-in">
        <li class="aui-list-header">
            账户信息
        </li>
        <li class="aui-list-item">
            <div class="aui-list-item-inner">
            	<select id="selectGroup" class="selectpicker">
                    <!-- <option value='1'>账户1</option> -->
                </select>
            </div>
        </li>
        <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <input class="aui-list-item-title" id="balance" readonly="readonly">
            </div>
        </li>
    </ul>
</div>
    <div class="aui-content aui-margin-b-15">
    <ul class="aui-list aui-select-list">
        
        <li class="aui-list-item" >
            <div class="aui-list-item-label" align="center">
                <label ><input class="aui-radio" type="radio" name="radio2" id="single"> 单数</label><br>
            </div>
        </li>
        <li class="aui-list-item" >
            <div class="aui-list-item-label" align="center">
                <label><input class="aui-radio" type="radio" name="radio2" id="double"> 双数</label>
            </div>
        </li>
       	</ul>
       	</div>
    <div class="aui-content aui-margin-b-15">
    <ul class="aui-list aui-form-list">
         <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    下注金额
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="下注金额" id="coin">
                </div>
            </div>
        </li>
    </ul>
    <p><div class="aui-btn aui-btn-block aui-btn-outlined" id="btnConfirm">提交</div></p>
</div>
    	<font id="itcode" hidden="hidden">${itcode}</font>
        
        <footer class="aui-bar aui-bar-tab" id="footer">
            <div class="aui-bar-tab-item aui-active" tapmode>
                <i><img src="../img/shouye.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">进行中</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/zuixinjiexiao.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">最新揭晓</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/wode.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">我的</div>
            </div>
        </footer>
        <div class="modal" id="resultModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-body">
                        <table class="table table-condensed" style="table-layout: fixed;">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">开奖结果</caption>
                            <col style="width: 50%" />
                            <col style="width: 50%" />
                            <thead>
                                <tr>
                                  <th style="text-align: center;color: gray;font-size: 80%;">获奖码</th>
                                  <th style="text-align: center;color: gray;font-size: 80%;">获奖人Itcode</th>
                                </tr>
                            </thead>
                            <tbody id="modalResult">
                            </tbody>
                        </table>
                        <table class="table table-condensed" style="table-layout: fixed;">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">我的夺宝码</caption>
                            <tbody id="modalMine">
                            </tbody>
                        </table>
                        <table id="lotteryTab" class="table table-condensed" style="table-layout: fixed;" hidden="hidden">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;color: red;">中奖提示</caption>
                            <tbody id="modalHongbao">
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>