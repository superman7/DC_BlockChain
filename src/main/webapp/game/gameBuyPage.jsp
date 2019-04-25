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
        <title>押注详情</title>
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
        <script type="text/javascript">
            
            var infoData;
            var detailData;
            var boughtCount;
            var rewardPrice = 5000;
            var baseUrl = '/';

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

            $(function() {
            	var itcode = "<%=itcode%>";
            	var id = <%=id%>;
            	//alert(itcode+id);
                var nowDate = new Date();
                var nowDateStr = nowDate.getFullYear();
                if (nowDate.getMonth() + 1 < 10) {
                    nowDateStr += ('-0' + (nowDate.getMonth() + 1));
                }else{
                    nowDateStr += ('-' + (nowDate.getMonth() + 1));
                }
                if(nowDate.getDate() < 10){
                    nowDateStr += ('-0' + nowDate.getDate());
                }else{
                    nowDateStr += ('-' + nowDate.getDate());
                }
                function changeProgress(valueNow) {
                    var valueMax = $("#divProgress").attr("aria-valuemax");
                    $("#divProgress").attr("aria-valuenow",valueNow);
                    $("#divProgress").attr("style","width: "+ valueNow/valueMax*100 +"%;");
                }

                $(".gameDescription").click(function() {
                	window.location.href = baseUrl + "gameIntroduce";
                });

                var nowCount;
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
                                
                                selfBoughtCount = 0;
                                todayBoughtCount = 0;
                                acceptInviteCount = 0;
                                beInvitedCount = 0;
                                console.log(detailData.length);
                                for (var i = 0; i < detailData.length; i++) {
                                    if(detailData[i].backup4 - 4 > 0){
                                        boughtCount --;
                                        beInvitedCount ++;
                                    }else{
                                        if(detailData[i].backup1 == ''){
                                            selfBoughtCount ++;
                                            if (detailData[i].hashcode.substring(0,10) == nowDateStr) {
                                                todayBoughtCount ++;
                                            }
                                        }else{
                                            if(detailData[i].backup4 == 0){
                                                acceptInviteCount ++;
                                            }
                                        }
                                    }
                                }
                                $("#fontReword").text(infoData.description);
                                $("#strHaved").text(infoData.nowSumAmount);
                                $("#strSum").text(infoData.winSumAmount);
                                $("#strRest").text(infoData.winSumAmount - infoData.nowSumAmount);
                                $("#divProgress").attr("aria-valuemax",infoData.winSumAmount);
                                var bkImgStr = infoData.backup3;
                                //不带邀请好友功能
                                if(bkImgStr == ''){
                                    if (infoData.typeCode == 0) {
                                       bkImgStr = "lottery_rmb_2.jpg";
                                    } else if (infoData.typeCode == 1) {
                                       bkImgStr = "game1.jpg";
                                    }
                                    $("#bkImg").attr("src", "../img/"+bkImgStr);
                                    changeProgress(infoData.nowSumAmount);

                                    //已售罄，请等待开奖
                                    if(infoData.flag == 1) {
                                        var htmlStr = "<a onClick='viewResult()'>查看结果</a>";
                                        $("#divBottom").html(htmlStr);
                                        return;
                                    }
                                    //判断是否参与
                                    var innerHtml = "";
                                    if(boughtCount == 0){
                                    	//alert(boughtCount);
                                        innerHtml = "<div class='aui-list-item-inner' id = 'gameChoose'><div class='aui-list-item-input'><label><input class='aui-radio' type='radio' name='choose' value='1' checked> 单&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label><label><input class='aui-radio' type='radio' name='choose' value='0'> 双</label></div></div>"
										+"<button type='button' style='float: left; margin-left: 20%;' class='btn btn-info btn-sm' onClick='buyClick()'>立即抢购</button>";
                                        innerHtml += "<button type='button' style='float: right; margin-right: 20%;' class='btn btn-info btn-sm' onClick='viewTicket()'>查看游戏码</button>";
                                    } else if (boughtCount >= infoData.limitEveryday) {
                                        innerHtml = "<font style='float: left; margin-left: 15px;'>已达购买上限&nbsp;<strong>" + boughtCount+ "</strong>&nbsp;次</font>" + "<button type='button' style='float: right; margin-right: 15px;' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看游戏码 </button>";
                                    } else if(boughtCount < infoData.limitEveryday) {
                                        innerHtml = "<font style='float: left; margin-left: 20px;'>已购买&nbsp;<strong>" + boughtCount 
                                        + "</strong>&nbsp;次</font>"
                                        + "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看游戏码 </button>"
                                        + "<button id='btnBuy' type='button' class='btn btn-info btn-sm' style='float: right; margin-right: 20px;' onClick='buyClick()'>继续购买</button>";
                                    } 
                                    $("#divBottom").html(innerHtml);
                                }
                        }
                    }
                });

                if(itcode == "fannl" || itcode == "mojja") {
                    $("#fannlManager").show();
                }

                window.fannl = function() {
                    var dialog = new auiDialog();
                    dialog.prompt({
                        title:"reward修改",
                        text:infoData.reward,
                        type:'number',
                        buttons:['取消','修改','开奖']
                    },function(ret){
                        if(ret.buttonIndex == 2){
                            showDefault("loading");
                            $.ajax({
                                type: "GET",
                                url: baseUrl + "lotteryInfo/updateReward",
                                data: {
                                	"id" : infoData.id,
                                    "reward" : ret.text
                                },
                                dataType: "json",
                                success: function(data) {
                                    toast.hide();
                                    if (data.success) {
                                        alert("修改成功！");
                                    }
                                }
                            })
                        }else if(ret.buttonIndex == 3){
                            showDefault("loading");
                            $.ajax({
                                type: "GET",
                                url: baseUrl + "lotteryInfo/runOptionLottery",
                                data: {
                                	"lotteryId" : infoData.id,
                                    "option" : ret.text
                                },
                                dataType: "json",
                                success: function(data) {
                                    toast.hide();
                                    if (data.success) {
                                        alert("开奖成功！");
                                    }
                                }
                            })
                        }
                    })
                }
                //点击购买按钮
                window.buyClick = function() {
                    var saleFlag = true;
                    if($("#divProgress").attr("aria-valuenow") == $("#divProgress").attr("aria-valuemax")) {
                        alert("已售罄，请等待开奖!");
                        return;
                    } else {
                        $.ajax({
                            type: "GET",
                            url: baseUrl + "game/selectGameInfo",
                            data: {
                            	"lotteryId" : infoData.id
                            },
                            dataType: "json",
                            success: function(data) {
                                if(data.data == "LotteryOver") {
                                    alert("已售罄，请等待开奖!");
                                    saleFlag = false;
                                }
                            }
                        })
                    }
                    if (!saleFlag) {
                        return;
                    }
                    if (selfBoughtCount >= infoData.limitEveryday) {
                        alert("已达购买上限");
                        return;
                    }
 					var choosed = $("input[name='choose']:checked").val();
 					//alert(choosed+"hahaha");
                    confirm
                    if(confirm("您确认花费 "+ infoData.unitPrice +"SZB 购买吗？") == true){
                        showDefault("loading");
                        $.ajax({
                            type: "GET",
                            url: "/wallet/getCheckUp.jsp",
                            data: {"jsonStr" : JSON.stringify({
                            	"choosed" : choosed,
								"itcode" : itcode,
                                "unitPrice" : infoData.unitPrice,
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
		                                    boughtCount += 1;
		                                    selfBoughtCount += 1;
		                                    var htmlStr = "";
		                                    if (selfBoughtCount >= infoData.limitEveryday) {
		                                        htmlStr = "<font style='float: left; margin-left: 15px;'>已达购买上限&nbsp;<strong>" + selfBoughtCount 
		                                            + "</strong>&nbsp;次</font>"
		                                            + "<button type='button' style='float: right; margin-right: 15px;' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看游戏码 </button>";
		                                    } else {
		                                        htmlStr = "<font style='float: left; margin-left: 20px;'>已购买&nbsp;<strong>" + selfBoughtCount 
		                                            + "</strong>&nbsp;次</font>"
		                                            + "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看游戏码 </button>"
		                                            + "<button id='btnBuy' type='button' class='btn btn-info btn-sm' style='float: right; margin-right: 20px;' onClick='buyClick()'>继续购买</button>";
		                                    } 
		                                    $("#divBottom").html(htmlStr);
		
		                                    var nowAmount = parseInt($("#divProgress").attr("aria-valuenow"))+10;
		                                    changeProgress(nowAmount);
		                                    $("#strHaved").text(nowAmount);
		                                    $("#strRest").text(infoData.winSumAmount - nowAmount);
		                                }
		                            }
		                        });
                            }
                        });
                    }
                }
                
                //点解查看夺宝码按钮
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
                                if (infoTemp.typeCode - 1 <= 0) {
                                    if (infoTemp.backup3 != '') {
                                        var htmlStr1 = "";
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
                                            htmlStr += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailTemp[index].hashcode+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;'>"+ticket+"</td></tr>";
                                        }
                                        $("#modalTbody").html(htmlStr);
                                        $("#transactionModal").modal('show');   
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
                
                //查看结果
                window.viewResult =function() {
                    var numberArr = infoData.winTicket.split("&");
                    var winnerArr = infoData.winner.split("&");
                    var htmlStr1 = "";
                    for(var index = 0; index < numberArr.length; index++) {
                        htmlStr1 += "<tr><td style='word-break: break-all;text-align: center;'>"+numberArr[index]+"</td><td style='word-break: break-all;text-align: center;'>"+winnerArr[index]+"</td></tr>";
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
                        for(var j = 0; j < numberArr.length; j++) {
                            if (numberArr[j] == detailData[index].ticket) {
                                htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;color:red;'>"+detailData[index].ticket+"</td></tr>";
                                rewordArr.push(detailData[index].winReword);
                            } else {
                                htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailData[index].ticket+"</td></tr>";
                            }
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
        </script>
    </head>
    <body style="font-family:微软雅黑;">
		 <font id="itcode">${itcode}</font>
		 <font id="lotteryId" hidden="hidden">${id}</font>
         <div class="container" style="">
            <div class="row">
                <!-- typecode为0/1的显示方式 -->
                <div id='singleLottery' class="col-xs-12 col-md-12" align="center" style="margin-top: 5%;">
                    <div style="border: 1px solid #EDEDED;">
                        <img id="bkImg" src=""  class="img-responsive center-block" style="padding-top: 1px;padding-bottom: 5px;">
                        <div style="border: 1px solid #EDEDED;border-top: none;">
                            <font id="fontReword" style="text-align: center;"></font>
                            <div class="progress" style="margin-top: 8px;">
                                <div id="divProgress" class="progress-bar progress-bar-success" role="progressbar"
                                    aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 100%;">
                                </div>
                            </div>
                            <font style="text-align: center;color: gray;">已累计 <strong id="strHaved"></strong>&nbsp;&nbsp;&nbsp;总需金额 <strong id="strSum"></strong>&nbsp;&nbsp;&nbsp;剩余 <strong id="strRest"></strong></font>
                        </div>
                        <div id="divBottom" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                            <button type='button' class='btn btn-link gameDescription'>单双游戏玩法简介</button>
                        </div>
                        </div>
                    </div>
                </div>

                <div id="fannlManager" class="col-xs-12 col-md-12" style='margin-top: 1%;' hidden="hidden" align="center">
                    <div style="border: 1px solid #EDEDED;">
                        <div style='margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;'>
                            <button type='button' class='btn btn-default btn-sm' onclick="fannl()">管理</button>
                        </div>
                    </div>
                </div>
                <div id="divWait" class="col-xs-12 col-md-12" align="center" style="position: fixed;left: 50%;top: 50%;transform: translateX(-50%)translateY(-50%);-webkit-transform:translateX(-50%) translateY(-50%);">
                    <img id="waitGif" src="../img/walletWait.gif" hidden="hidden">
                </div>
            </div>
        </div>
        <!-- 单项类-查看用户购买的夺宝码 -->
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
                                  <th style="text-align: center;color: gray;font-size: 80%;">购买时间</th>
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
        <!-- 选项/邀请类-查看用户购买的夺宝码 -->
        <div class="modal" id="transactionModal1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div id="divKeystore" class="modal-body">
                        <table class="table table-condensed" style="table-layout: fixed;">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">购买记录</caption>
                            <col style="width: 30%" />
                            <col style="width: 40%" />
                            <col style="width: 30%" />
                            <thead>
                                <tr>
                                  <th style="text-align: center;color: gray;font-size: 80%;">购买时间</th>
                                  <th style="text-align: center;color: gray;font-size: 80%;">游戏码</th>
                                  <th style="text-align: center;color: gray;font-size: 80%;">状态</th>
                                </tr>
                            </thead>
                            <tbody id="modalTbody1">
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
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">我的游戏码</caption>
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