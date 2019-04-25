<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="weaver.conn.RecordSet"%>
<%@ page import="weaver.general.BaseBean"%>
<%@ page import="weaver.file.FileUpload"%>
<%@ page import="weaver.hrm.*" %>
<%@ page import="weaver.general.*" %>
<% 	request.setCharacterEncoding("UTF-8");
	String itcode = request.getParameter("itcode");
	String id = request.getParameter("id");
			
%>
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
        <script type="text/javascript" src="../js/aui_script/aui-toast.js" ></script>
        <script type='text/javascript' src='../js/check_login.js'></script>
		<script type="text/javascript" src='../js/cookie_util.js'></script>
        <script type="text/javascript">
            var itcode = "<%=itcode%>";
            var dataJson = {};
            var baseUrl = '/';
            var id = <%=id%>;
            
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
		                               if (value.available == 3 ) {
	/* 	                                    var account = "<option value='" + value.account + "'></option>"; */
		                                    /* var innerHtml = "<option value='" + value.account + "'>" + value.alias + "</option>"; */
		                                    //alert(value.id);
		                                    $("#defaultAccount").val("默认账户:"+value.account);	
	 	                                	$("#balance").val("账户余额:" +value.balance  + " SMB");	                         
	 	                                	} else {
		                                    
		                                }
		                            });
		                        }
		                            else{
		                            	alert("异常，请稍后再试");
		                            }
		                    }
		                });
	                }
	            });
	           
	            $("#id").append(id);
	            $("#selectGroup").change(function() {
                    var selectedAlias = $("#selectGroup").children('option:selected').text();
                    var account = $(this).children('option:selected').attr("value");
                    var balance = dataJson[selectedAlias].balance;
                    $("#balance").val(balance + " SMB");
                });
	            
	          //发送请求，获取此次夺宝信息，查看该用户是否参与此次夺宝，若参与，展示夺宝号码
                $.ajax({
                    type: "GET",
                    url: baseUrl + "game/getOne",
                    data: {
                    	"itcode" : itcode,
                        "id" : id
                    },
                    dataType: "json",
                    success: function(data) {
                        if (data.success) {
                                infoData = data.infoData;
                                detailData = data.detailData;
                                boughtCount = detailData.length;
                                console.log(detailData.length);
                                var bkImgStr = infoData.backup3;
                                if(bkImgStr == ''){
                                    //已售罄，请等待开奖
                                    if(infoData.flag == 1) {
                                    	alert("等待开奖");
                                        var htmlStr = "<p><div class='aui-btn aui-btn-primary aui-btn-block aui-btn-outlined' onClick='viewResult()'>查看结果</div></p>";
                                        $("#divBottom").html(htmlStr);
                                        return;
                                    }
                                    //判断是否参与
                                    var innerHtml = "";
                                    if(boughtCount == 0){
                                    	//可以购买
                                    } else if (boughtCount > 0) {
                                        alert("已达购买上限");
                                        return;
                                    } 
                                    } 
                                    $("#divBottom").html(innerHtml);
                                }
                        }
                	});
	          window.viewResult = function(){
	        	  $.ajax({
	                    type: "GET",
	                    url: baseUrl + "game/getOne",
	                    data: {
	                    	"itcode" : itcode,
	                        "id" : id
	                    },
	                    dataType: "json",
	                    success: function(data) {
	                    	var detailData = data.detailData;
	                    	var number = data.infoData.winTicket
	                        var winnerArr = data.infoData.winner.split("&");
	                        var htmlStr1 = "";
	                        for(var index = 0; index < winnerArr.length; index++) {
	                            htmlStr1 += "<tr><td style='word-break: break-all;text-align: center;'>"+number+"</td><td style='word-break: break-all;text-align: center;'>"+winnerArr[index]+"</td></tr>";
	                        }
	                        var htmlStr2 = "";
	                        var rewordArr = [];
	                        if(detailData.length == 0) {
	                            htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>抱歉，您未参与！</td></tr>";
	                            $("#modalResult").html(htmlStr1);
	                            $("#modalMine").html(htmlStr2);
	                            $("#resultModal").modal('show');
	                            return;
	                        }

	                        for(var index = 0; index < detailData.length; index++) {
	                            
	                                if (number == detailData[index].ticket) {
	                                    htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;color:red;'>"+detailData[index].ticket+"</td></tr>";
	                                    rewordArr.push(detailData[index].winReword);
	                                } else {
	                                    htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailData[index].ticket+"</td></tr>";
	                                }
	                        }

	                        var htmlStr3 = "";
	                        if(rewordArr.length == 0) {
	                            htmlStr3 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>很抱歉，您未中奖</td></tr>";
	                        } else if(infoData.typeCode == 0) {
	                            var rewordStr = ""
	                            for (var i = 0; i < rewordArr.length; i++) {
	                                rewordStr += rewordArr[i] + "，";
	                            }
	                            htmlStr3 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>恭喜您获奖，红包码为：<strong style='color:red;'>" + rewordStr +"</strong>请在支付宝中搜索领取。</td></tr>";
	                        } else if (infoData.typeCode == 1) {
	                            htmlStr3 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>恭喜您获奖，<strong style='color:red;'>" + infoData.reward+" SMB</strong>将在稍后发放到您的账户中！</td></tr>";
	                        }

	                        $("#modalResult").html(htmlStr1);
	                        $("#modalMine").html(htmlStr2);
	                        $("#modalHongbao").html(htmlStr3);
	                        $("#lotteryTab").show();
	                        $("#resultModal").modal('show');
	          	}
	        	  });
	        	  }
	            
	          //点击查看夺宝码按钮
                window.viewTicket = function() {
                    showDefault("loading");
                    $.ajax({
                        type: "GET",
                        url: baseUrl + "game/getOne",
                        data: {
                        	"itcode" : itcode,
                            "id" : id
                        },
                        dataType: "json",
                        success: function(data) {
                            toast.hide();
                            if (data.success) {
                                var detailTemp = data.detailData;
                                var infoTemp = data.infoData;
                              //  alert(infoTemp.typeCode);
                                if (infoTemp.typeCode - 1 <= 0) {
                                	//console.log(infoTemp.backup3+"1")
                                    if (infoTemp.backup3 != '') {
                                        var htmlStr1 = "";
                                       // alert(detailTemp.length);
                                        for(var index = 0; index < detailTemp.length; index++) {
                                            var status = "";
                                            if(detailTemp[index].backup4 == 0){
                                                if(detailTemp[index].backup1 == ''){
                                                    status = '自身购买';
                                                }
                                            }
                                            
                                            var ticket = detailTemp[index].backup3 == 0 ? "生成中" : detailTemp[index].ticket;
                                            // htmlStr1 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailTemp[index].backup2+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;'>"+ticket+"</td>";
                                            
                                            if(detailTemp[index].backup4 == 6 || detailTemp[index].backup4 == 0){
                                                htmlStr1 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailTemp[index].hashcode+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;'>"+ticket+"</td><td id='tdOption' style='word-break: break-all;text-align: center;'>"+ status +"</td></tr>";
                                            }                                        
                                        }
                                        $("#modalTbody1").html(htmlStr1);
                                        $("#transactionModal1").modal('show');

                                    }else{
                                        var htmlStr = "";
                                        for(var index = 0; index < detailTemp.length; index++) {
                                            var ticket = detailTemp[index].backup3 == 0 ? "生成中" : detailTemp[index].ticket;
                                            //alert(ticket);
                                            htmlStr += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailTemp[index].hashcode+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;'>"+ticket+"</td></tr>";
                                        }
                                        $("#modalTbody").html(htmlStr);
                                        $("#transactionModal").modal('show');   
                                        //alert(htmlStr);
                                    }
                                }else{
                                    var htmlStr1 = "";
                                    for(var index = 0; index < detailTemp.length; index++) {
                                        var option = "";
                                        var ticket = detailTemp[index].backup3 == 0 ? "生成中" : detailTemp[index].ticket;
                                        htmlStr1 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailTemp[index].hashcode+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;'>"+ticket+"</td><td id='tdOption' style='word-break: break-all;text-align: center;'>"+ option +"</td></tr>";
                                    }
                                    $("#modalTbody1").html(htmlStr1);
                                    $("#transactionModal1").modal('show');
                                }
                            }
                        }
                    });
                }
                
         });
            
            
        	//点击双按钮
            function choose0(){
            	var choose = 0;
            	//alert("choose"+choose);
                var money = $("#money").val();
                //alert("money"+money);
                var balanceStr = $("#balance").val();
                //alert(balanceStr);
                var balance = balanceStr.replace(/[^\d.]/g,'');
                	//console.log(balance+"1");
                //alert(balance);
                if(Number(balance <= 0)){
                	alert("账户金额不足请及时充值");
                	return;
                }
                if(Number(money)>Number(balance)){
                	alert("请输入合法的金额");
            		return;    
            	}
                confirm
                if(confirm("您确认花费 "+ money +"SZB 购买吗？") == true){
                    showDefault("loading");
                confirm1(choose,money);
                }
        	}
          	//点击单按钮
            function choose1(){
            	var choose = 1;
            	//alert("choose"+choose);
                var money = $("#money").val();
                //alert("money"+money);
                var balanceStr = $("#balance").val();
                //alert(balanceStr);
                var balance = balanceStr.replace(/[^\d.]/g,'');
                //alert(balance);
                if(Number(balance <= 0)){
                	alert("账户金额不足请及时充值");
                	return;
                }
                if(Number(money)>Number(balance)){
                	alert("请输入合法的金额");
            		return;    
            	}
                confirm
                if(confirm("您确认花费 "+ money +"SZB 购买吗？") == true){
                    showDefault("loading");
                	confirm1(choose,money);
                }
            }
            
            

            var confirm1 = function(choose,money){
            	var saleFlag = true;
            	//alert(choose);
                if (boughtCount > 0) {
                    alert("已达购买上限");
                    return;
                }
                    $.ajax({
                        type: "GET",
                        url: "/wallet/getCheckUp.jsp",
                        data: {"jsonStr" : JSON.stringify({
                        	"choosed" : choose,
							"itcode" : itcode,
                            "money" : money,
                            "lotteryId" : infoData.id,
                            "option" : "0"
                        })},
                        dataType: "text",
                        success: function(data) {
	                        $.ajax({
	                            type: "GET",
	                            url: baseUrl + "game/insertGameDetails",
	                            data: {"param":data},
	                            dataType: "json",
	                            error:console.log(data),
	                            success: function(data) {
	                                if (data.success) {
	                                    toast.hide();
	                                    if(data.data == "balanceNotEnough") {
	                                        alert("您的余额不足!");
	                                        return;
	                                    }
	                                    if(data.data == "lotteryOver") {
	                                        alert("已售罄，请等待开奖!");
	                                        return;
	                                    }
	                                    alert("正在生成游戏码，请稍后查看");
	                                }
	                            }
	                        });
                        }
                    });
            }
            
            apiready = function(){
                api.parseTapmode();
            }
            var toast = new auiToast();
            function showDefault(type){
                switch (type) {
                    case "success":
                        toast.success({
                            title:"提交成功",
                            duration:2000
                        });
                        break;
                    case "fail":
                        toast.fail({
                            title:"提交失败",
                            duration:2000
                        });
                        break;
                    case "custom":
                        toast.custom({
                            title:"提交成功",
                            html:'<i class="aui-iconfont aui-icon-laud"></i>',
                            duration:2000
                        });
                        break;
                    case "loading":
                        toast.loading({
                            title:"加载中",
                            duration:2000
                        });
                        break;
                    default:
                        break;
                }
            }

            
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
                <input class="aui-list-item-input" id="defaultAccount" readonly="readonly">
            </div>
        </li>
        <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <input class="aui-list-item-title" id="balance" readonly="readonly">
            </div>
        </li>
    </ul>
   
 
    <ul class="aui-list aui-form-list">
    	<li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label" id="id">
                    期号:
                </div>
            </div>
         </li>
         <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    下注金额
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="下注金额" id="money">
                </div>
            </div>
        </li>
    </ul>
     <div class="aui-content aui-margin-b-15">
		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="viewTicket()">查看夺宝码</div>
		<div id="divBottom"></div>
        <p><div class="aui-btn aui-btn-primary" style="width:150px;height:50px; float:left" onClick="choose1()">单</div>
		<div class="aui-btn aui-btn-primary" style="width:150px;height:50px; float:right" onClick="choose0()">双</div>
     </div>

</div>
        <!-- 查看夺宝码 -->
        <div class="modal" id="transactionModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div id="divKeystore" class="modal-body">
                        <table class="table table-condensed" style="table-layout: fixed;">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">购买记录</caption>
                            <col style="width: 50%" />
                            <col style="width: 50%" />
                            <thead>
                                <tr>
                                  <th style="text-align: center;color: gray;font-size: 80%;">交易凭证</th>
                                  <th style="text-align: center;color: gray;font-size: 80%;">游戏码</th>
                                </tr>
                            </thead>
                            <tbody id="modalTbody">
                            </tbody>
                        </table>
                    </div>
                    <div class="modal-footer" style="text-align: center;">
                        <button type="button" class="btn btn-danger btn-sm closeModalWindow" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>
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