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
        <title>抽奖详情</title>
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
            var itcode;
            var id;
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
            	itcode = $("#itcode").text();
            	id = $("#lotteryId").text();
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
                	window.location.href = baseUrl + "lotteryIntroduce";
                });

                $("#inviteDescription").click(function() {
                    window.location.href = baseUrl + "lotteryInviteIntroduce";
                });

                var nowCount;
                //发送请求，获取此次夺宝信息，查看该用户是否参与此次夺宝，若参与，展示夺宝号码
                $.ajax({
                    type: "GET",
                    url: baseUrl + "lotteryInfo/getOne",
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
                            //选项抽奖
                            if (infoData.typeCode - 1 > 0) {
                                $("#fontReword1").text(infoData.description);
                                $("#strHaved1").text(infoData.nowSumAmount/10);
                                nowCount = infoData.nowSumAmount/10;
                                var bkImgStr = infoData.backup3;

                                document.getElementById("singleLottery").style.display="none";
                                document.getElementById("inviteLottery").style.display="none";
                                document.getElementById("optionLottery").style.display="";

                                $("#bkImg1").attr("src", "../img/"+bkImgStr);
                                changeProgress(infoData.nowSumAmount);
                                //已售罄，请等待开奖
                                if(infoData.flag == 1) {
                                    var htmlStr = "<a onClick='viewResult()'>查看结果</a>";
                                    $("#divBottom1").html(htmlStr);
                                    $("#divBottom2").hide();
                                    $("#divBottom3").hide();
                                    return;
                                }
                                
                                //判断是否参与
                                var innerHtml = "";
                                //选项抽奖
                                var divBottom1Html = "";
                                var divBottom2Html = "";
                                var divBottom3Html = "";
                                if(boughtCount == 0){
                                    divBottom1Html = "<font style='text-align: center;color: gray;'>点击下方参与有奖竞猜</font>";
                                    divBottom3Html = "<button type='button' class='btn btn-info btn-sm' onClick='buyClickOption(1)'>" + infoData.backup1 + "</button>&nbsp;&nbsp;&nbsp;&nbsp;"
                                    + "<button type='button' class='btn btn-info btn-sm' onClick='buyClickOption(2)'>" + infoData.backup2 + "</button>";

                                    $("#divBottom1").html("<font style='text-align: center;color: gray;'>点击下方参与有奖竞猜</font>");
                                    $("#divBottom2").hide();
                                    $("#divBottom3").html(divBottom3Html);

                                } else if (boughtCount >= infoData.limitEveryday) {
                                    divBottom2Html = "<font style='text-align: center;color: gray;'>已达购买上限&nbsp;<strong>" + boughtCount 
                                        + "</strong>&nbsp;次</font>";
                                    divBottom3Html = "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>";
                                    $("#divBottom1").hide();
                                    $("#divBottom2").html(divBottom2Html);
                                    $("#divBottom3").html(divBottom3Html);
                                } else if(boughtCount < infoData.limitEveryday) {
                                    divBottom1Html = "<font style='text-align: center;color: gray;'>已购买&nbsp;<strong>" + boughtCount 
                                        + "</strong>&nbsp;次</font>";
                                    divBottom2Html = "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>";
                                    divBottom3Html = "<button type='button' class='btn btn-info btn-sm' onClick='buyClickOption(1)'>" + infoData.backup1 + "</button>&nbsp;&nbsp;&nbsp;&nbsp;"
                                        + "<button type='button' class='btn btn-info btn-sm' onClick='buyClickOption(2)'>" + infoData.backup2 + "</button>";

                                    $("#divBottom1").html(divBottom1Html);
                                    $("#divBottom2").html(divBottom2Html);
                                    $("#divBottom3").html(divBottom3Html);
                                }

                                // $("#divBottom1").html(innerHtml);
                            //普通抽奖
                            }else{
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
                                       bkImgStr = "lottery_szb_2.jpg";
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
                                        innerHtml = "<button type='button' style='float: left; margin-left: 20%;' class='btn btn-info btn-sm' onClick='buyClick()'>立即抢购</button>";
                                        innerHtml += "<button type='button' style='float: right; margin-right: 20%;' class='btn btn-info btn-sm' onClick='viewTicket()'>查看夺宝码</button>";
                                    } else if (boughtCount >= infoData.limitEveryday) {
                                        innerHtml = "<font style='float: left; margin-left: 15px;'>已达购买上限&nbsp;<strong>" + boughtCount+ "</strong>&nbsp;次</font>" + "<button type='button' style='float: right; margin-right: 15px;' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>";
                                    } else if(boughtCount < infoData.limitEveryday) {
                                        innerHtml = "<font style='float: left; margin-left: 20px;'>已购买&nbsp;<strong>" + boughtCount 
                                        + "</strong>&nbsp;次</font>"
                                        + "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>"
                                        + "<button id='btnBuy' type='button' class='btn btn-info btn-sm' style='float: right; margin-right: 20px;' onClick='buyClick()'>继续购买</button>";
                                    }
                                    
                                    $("#divBottom").html(innerHtml);
                                //带邀请好友功能
                                }else{
                                    document.getElementById("singleLottery").style.display="none";
                                    document.getElementById("optionLottery").style.display="none";
                                    document.getElementById("inviteLottery").style.display="";
                                    $("#fontReword2").text(infoData.description);
                                    $("#strHaved2").text(infoData.nowSumAmount/10);
                                    nowCount = infoData.nowSumAmount/10;
                                    $("#bkImg2").attr("src", "../img/"+bkImgStr);
                                    changeProgress(infoData.nowSumAmount);
                                    //已售罄，请等待开奖
                                    if(infoData.flag == 1) {
                                        var htmlStr = "<a onClick='viewResult()'>查看结果</a>";
                                        $("#divBottom21").html(htmlStr);
                                        $("#divBottom22").hide();
                                        $("#divBottom23").hide();
                                        return;
                                    }
                                    
                                    //判断是否参与
                                    var innerHtml = "";
                                    //基础信息div(今日参与、已获得多少票。已收到/接受多少邀请)
                                    var divBottom21Html = "";
                                    //抢购+查看夺宝码
                                    var divBottom22Html = "";
                                    //
                                    var divBottom23Html = "";

                                    var inviteSpssHtml = "";

                                    if(todayBoughtCount == 0){
                                        inviteSpssHtml = "<font>&nbsp;已获得&nbsp;<strong>" + acceptInviteCount + "</strong>&nbsp;枚夺宝码奖励</font>";

                                        divBottom21Html = "<font>&nbsp;您共获得&nbsp;<strong>" + boughtCount + "</strong>&nbsp;枚夺宝码，今日未购买</font>";
                                        divBottom22Html = "<font>预计获奖率&nbsp;</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 10000) / 100.00 + "%</strong></font>，<font>收益</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 100 * rewardPrice) / 100.00 +"</strong></font>";

                                        divBottom23Html = "<button type='button' style='float: left; margin-left: 20%;' class='btn btn-info btn-sm' onClick='buyClickInvite()'>立即抢购</button>" + "<button type='button' style='float: right; margin-right: 20%;' class='btn btn-info btn-sm' onClick='viewTicket()'>查看夺宝码</button>";
                                        $("#divBottom21").html(divBottom21Html);
                                        $("#divBottom22").html(divBottom22Html);
                                        $("#divBottom23").html(divBottom23Html);
                                        $("#inviteSpss").html(inviteSpssHtml);
                                        $("#divBottom21").show();
                                        $("#divBottom22").show();
                                        $("#divBottom23").show();
                                    } else if (todayBoughtCount >= infoData.limitEveryday) {
                                        inviteSpssHtml = "<font>&nbsp;已获得&nbsp;<strong>" + acceptInviteCount + "</strong>&nbsp;枚夺宝码奖励</font>";

                                        divBottom21Html = "<font>&nbsp;您共获得&nbsp;<strong>" + boughtCount + "</strong>&nbsp;枚夺宝码，今日已购买</font>";
                                        divBottom22Html = "<font>预计获奖率&nbsp;</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 10000) / 100.00 + "%</strong></font>，<font>收益</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 100 * rewardPrice) / 100.00 +"</strong></font>";
                                        divBottom23Html = "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'>查看夺宝码</button>";
                                        $("#divBottom21").html(divBottom21Html);
                                        $("#divBottom22").html(divBottom22Html);
                                        $("#divBottom23").html(divBottom23Html);
                                        $("#inviteSpss").html(inviteSpssHtml);
                                        $("#divBottom21").show();
                                        $("#divBottom22").show();
                                        $("#divBottom23").show();
                                    } else if(todayBoughtCount < infoData.limitEveryday) {
                                        inviteSpssHtml = "<font>&nbsp;已获得&nbsp;<strong>" + acceptInviteCount + "</strong>&nbsp;枚夺宝码奖励</font>";

                                        divBottom21Html = "<font>&nbsp;您共获得&nbsp;<strong>" + boughtCount + "</strong>&nbsp;枚夺宝码，今日已购买&nbsp;<strong>" + todayBoughtCount + "</strong>&nbsp;次</font>";
                                        divBottom22Html = "<font>预计获奖率&nbsp;</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 10000) / 100.00 + "%</strong></font>，<font>收益</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 100 * rewardPrice) / 100.00 +"</strong></font>";
                                        divBottom23Html = "<button type='button' style='float: left; margin-left: 20%;' class='btn btn-info btn-sm' onClick='buyClickInvite()'>继续抢购</button>" + "<button type='button' style='float: right; margin-right: 20%;' class='btn btn-info btn-sm' onClick='viewTicket()'>查看夺宝码</button>";
                                        $("#divBottom21").html(divBottom21Html);
                                        $("#divBottom22").html(divBottom22Html);
                                        $("#divBottom23").html(divBottom23Html);
                                        $("#inviteSpss").html(inviteSpssHtml);
                                        $("#divBottom21").show();
                                        $("#divBottom22").show();
                                        $("#divBottom23").show();
                                    }
                                    $("#inviteUser").show();
                                    //if(itcode == "fannl" || itcode == "lizhe1" || itcode == "mojja" || itcode == "alexshen" || itcode == "liyuank" || itcode == "zhoujingb" || itcode == "zhangyangac" || itcode == "liuhze" || itcode == "wuzk" || itcode == "weiyg" || itcode == "xiapp" || itcode == "xueleic" || itcode == "liuxyai" || itcode == "yaoqiangc" || itcode == "fengshuo1" || itcode == "qiutong1" || itcode == "yangkaid" || itcode == "chenningc") {
                                    //     if (infoData.backup3 != '') {
                                    //         $("#inviteUser").show();
                                    //     }
                                    // }

                                }
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
                window.inviteUser = function() {
                    var dialog = new auiDialog();
                    dialog.prompt({
                        title:"邀请用户得抽奖码",
                        text:"请输入希望邀请的itcode",
                        type:'text',
                        buttons:['取消','邀请']
                    },function(ret){
                        if(ret.buttonIndex == 2){
                            if (ret.text == '') {
                                alert("请输入希望邀请的itcode!");
                                return;
                            }
                            if(confirm("您确认花费 "+ infoData.unitPrice +"SZB 购买吗？") == true){
                                showDefault("loading");
                                $.ajax({
                                    type: "GET",
                                    url: baseUrl + "inviteLotteryDetails",
                                    data: {
										"itcode" : itcode,
                                        "unitPrice" : infoData.unitPrice,
                                        "lotteryId" : infoData.id,
                                        "inviteItcode" : ret.text,
                                        "option" : "5"
                                    },
                                    dataType: "json",
                                    success: function(data) {
                                        if (data.success) {
                                            toast.hide();
                                            if(data.data == "InviteItcodeIsIllegaly") {
                                                alert("邀请用户itcode非法，请重试!");
                                                return;
                                            }
                                            if(data.data == "feifa") {
                                                alert("不允许邀请自身!");
                                                return;
                                            }
                                            if(data.data == "balanceNotEnough") {
                                                alert("您的余额不足!");
                                                return;
                                            }
                                            if(data.data == "lotteryOver") {
                                                alert("已售罄，请等待开奖!");
                                                return;
                                            }
                                            if(data.data == "UserHasBeenInvited") {
                                                alert("该用户已参与本次抽奖，邀请失败！");
                                                return;
                                            }
                                            if(data.data == "InviteCountMoreThanLimit") {
                                                alert("您邀请用户的次数已达上限20次，邀请失败！");
                                                return;
                                            }
                                            if(data.data == "ThisItcodeHasBeenInvited") {
                                                alert("您已邀请该用户，请勿重复邀请！");
                                                return;
                                            }
                                            alert("邀请成功，稍后可在我的邀请中查看！");
                                            boughtCount += 1;
                                            $("#strHaved2").text(nowCount);
                                        }
                                    }
                                })
                            }
                        }
                    })
                }
                window.queryInvited = function() {
                    showDefault("loading");
                    $.ajax({
                        type: "GET",
                        url: baseUrl + "lotteryInfo/getInvite",
                        data: {
							"itcode" : itcode,
                            "id" : infoData.id
                        },
                        dataType: "json",
                        success: function(data) {
                            toast.hide();
                            if (data.success) {
                                var detailTemp = data.detailData;
                                var infoTemp = data.infoData;
                                var htmlStr1 = "";
                                for(var index = 0; index < detailTemp.length; index++) {
                                    var status = "";
                                    if (detailTemp[index].backup4 == 5) {
                                        status = "<td id='tdStatus' style='word-break: break-all;text-align: center;'><button type='button' class='btn btn-success btn-sm' onClick='acceptInvite(" + detailTemp[index].id + ")'> 接受邀请 </button></td>";
                                    }else if(detailTemp[index].backup4 == 6 || detailTemp[index].backup4 == 0){
                                        status = "<td id='tdStatus' style='word-break: break-all;text-align: center;'>已接受</td>";
                                    }else if(detailTemp[index].backup4 == 7 || detailTemp[index].backup4 == 8){
                                        status = "<td id='tdStatus' style='word-break: break-all;text-align: center;color:gray;'>已失效</td>";
                                    }
                                    
                                    htmlStr1 += "<tr><td id='tdInviteItcode' style='word-break: break-all;text-align: center;'>"+detailTemp[index].backup2+"</td><td id='tdInviteTime' style='word-break: break-all;text-align: center;'>" + detailTemp[index].hashcode + "</td>"+ status +"</tr>";
                                }
                                $("#modalTbody2").html(htmlStr1);
                                $("#transactionModal2").modal('show');
                            }
                        }
                    });
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
                            url: baseUrl + "selectLotteryInfo",
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
                    if(confirm("您确认花费 "+ infoData.unitPrice +"SZB 购买吗？") == true){
                        showDefault("loading");
                        $.ajax({
                            type: "GET",
                            url: baseUrl + "insertLotteryDetails",
                            data: {
								"itcode" : itcode,
                                "unitPrice" : infoData.unitPrice,
                                "lotteryId" : infoData.id,
                                "option" : "0"
                            },
                            dataType: "json",
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
                                    alert("正在生成夺宝码，请稍后查看");
                                    boughtCount += 1;
                                    selfBoughtCount += 1;
                                    var htmlStr = "";
                                    if (selfBoughtCount >= infoData.limitEveryday) {
                                        htmlStr = "<font style='float: left; margin-left: 15px;'>已达购买上限&nbsp;<strong>" + selfBoughtCount 
                                            + "</strong>&nbsp;次</font>"
                                            + "<button type='button' style='float: right; margin-right: 15px;' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>";
                                    } else {
                                        htmlStr = "<font style='float: left; margin-left: 20px;'>已购买&nbsp;<strong>" + selfBoughtCount 
                                            + "</strong>&nbsp;次</font>"
                                            + "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>"
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
                }
                //接受邀请
                window.acceptInvite = function(option) {
                    $(".closeModalWindow").click();
                    // $("#transactionModal2").hide();
                    if(confirm("您确认花费 10SZB 接受邀请吗，接受邀请后将再为您生成一枚夺宝码？") == true){
                        showDefault("loading");
                        $.ajax({
                            type: "GET",
                            url: baseUrl + "acceptInvite",
                            data: {
                            	"id" : option,
                                "unitPrice" : infoData.unitPrice
                            },
                            dataType: "json",
                            success: function(data) {
                                if(data.success) {
                                    toast.hide();
                                    if(data.data == "balanceNotEnough") {
                                        alert("您的余额不足!");
                                        return;
                                    }
                                    if(data.data == "acceptInviteLimit") {
                                        alert("接受邀请达到上限。");
                                        return;
                                    }
                                    if(data.data == "lotteryOver") {
                                        alert("已售罄，请等待开奖!");
                                        return;
                                    }
                                    alert("接受邀请成功，请稍后查收奖励！");
                                    acceptInviteCount += 1;
                                    boughtCount += 1;
                                    nowCount += 1;
                                    inviteSpssHtml = "<font>&nbsp;已获得&nbsp;<strong>" + acceptInviteCount + "</strong>&nbsp;枚夺宝码奖励</font>";
                                    if (todayBoughtCount == 0) {
                                        divBottom21Html = "<font>&nbsp;您共获得&nbsp;<strong>" + boughtCount + "</strong>&nbsp;枚夺宝码，今日未购买</font>";
                                    } else {
                                        divBottom21Html = "<font>&nbsp;您共获得&nbsp;<strong>" + boughtCount + "</strong>&nbsp;枚夺宝码，今日已购买</font>";
                                    }
                                    divBottom22Html = "<font>预计获奖率&nbsp;</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 10000) / 100.00 + "%</strong></font>，<font>收益</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 100 * rewardPrice) / 100.00 +"</strong></font>";
                                    $("#divBottom21").html(divBottom21Html);
                                    $("#inviteSpss").html(inviteSpssHtml);
                                    $("#divBottom22").html(divBottom22Html);

                                    $("#strHaved2").text(nowCount);
                                }
                            }
                        })
                    }
                }
                //购买选项类抽奖奖票
                window.buyClickOption = function(option) {
                    var saleFlag = true;
                    
                    $.ajax({
                        type: "GET",
                        url: baseUrl + "selectLotteryInfo",
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

                    if (!saleFlag) {
                        return;
                    }
                    if (selfBoughtCount >= infoData.limitEveryday) {
                        alert("已达购买上限");
                        return;
                    }
                    if(confirm("您确认花费 "+ infoData.unitPrice +"SZB 购买吗？") == true){
                        showDefault("loading");
                        $.ajax({
                            type: "GET",
                            url: baseUrl + "insertLotteryDetails",
                            data: {
                            	"itcode" : itcode,
                                "unitPrice" : infoData.unitPrice,
                                "lotteryId" : infoData.id,
                                "option" : option
                            },
                            dataType: "json",
                            success: function(data) {
                                toast.hide();
                                if (data.success) {
                                    if(data.data == "balanceNotEnough") {
                                        alert("您的余额不足!");
                                        return;
                                    }
                                    if(data.data == "lotteryOver") {
                                        alert("已售罄，请等待开奖!");
                                        return;
                                    }
                                    alert("正在生成夺宝码，请稍后查看");
                                    boughtCount += 1;

                                    //选项抽奖
                                    var divBottom1Html = "";
                                    var divBottom2Html = "";
                                    var divBottom3Html = "";

                                    if (boughtCount >= infoData.limitEveryday) {
                                        divBottom2Html = "<font style='text-align: center;color: gray;'>已达购买上限&nbsp;<strong>" + boughtCount 
                                            + "</strong>&nbsp;次</font>";
                                        divBottom3Html = "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>";
                                        $("#divBottom1").hide();
                                        $("#divBottom2").show();
                                        $("#divBottom3").show();

                                        $("#divBottom2").html(divBottom1Html);
                                        $("#divBottom2").html(divBottom2Html);
                                        $("#divBottom3").html(divBottom3Html);
                                    } else {
                                        divBottom1Html = "<font style='text-align: center;color: gray;'>已购买&nbsp;<strong>" + boughtCount 
                                            + "</strong>&nbsp;次</font>";
                                        divBottom2Html = "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'> 查看夺宝码 </button>";
                                        divBottom3Html = "<button type='button' class='btn btn-info btn-sm' onClick='buyClickOption(1)'>" + infoData.backup1 + "</button>&nbsp;&nbsp;&nbsp;&nbsp;"
                                            + "<button type='button' class='btn btn-info btn-sm' onClick='buyClickOption(2)'>" + infoData.backup2 + "</button>";
                                        $("#divBottom1").html(divBottom1Html);
                                        $("#divBottom2").html(divBottom2Html);
                                        $("#divBottom3").html(divBottom3Html);

                                        $("#divBottom1").show();
                                        $("#divBottom2").show();
                                        $("#divBottom3").show();
                                    }

                                    $("#strHaved1").text(nowCount + 1);
                                }
                            }
                        });
                    }
                }
                //邀请好友类购买
                window.buyClickInvite = function(option) {
                    var saleFlag = true;
                    
                    $.ajax({
                        type: "GET",
                        url: baseUrl + "selectLotteryInfo",
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

                    if (!saleFlag) {
                        return;
                    }
                    if (todayBoughtCount >= infoData.limitEveryday) {
                        alert("今日已达购买上限");
                        return;
                    }
                    if(confirm("您确认花费 "+ infoData.unitPrice +"SZB 购买吗？") == true){
                        showDefault("loading");
                        $.ajax({
                            type: "GET",
                            url: baseUrl + "insertLotteryDetails",
                            data: {
                            	"itcode" : itcode,
                                "unitPrice" : infoData.unitPrice,
                                "lotteryId" : infoData.id,
                                "option" : "0"
                            },
                            dataType: "json",
                            success: function(data) {
                                toast.hide();
                                if (data.success) {
                                    if(data.data == "balanceNotEnough") {
                                        alert("您的余额不足!");
                                        return;
                                    }
                                    if(data.data == "lotteryOver") {
                                        alert("已售罄，请等待开奖!");
                                        return;
                                    }
                                    alert("正在生成夺宝码，请稍后查看");
                                    todayBoughtCount ++;
                                    boughtCount ++;
                                    nowCount ++;
                                    //选项抽奖
                                    var divBottom21Html = "";
                                    var divBottom22Html = "";
                                    var divBottom23Html = "";

                                    if (todayBoughtCount >= infoData.limitEveryday) {
                                        inviteSpssHtml = "<font>&nbsp;已获得&nbsp;<strong>" + acceptInviteCount + "</strong>&nbsp;枚夺宝码奖励</font>";

                                        divBottom21Html = "<font>&nbsp;共获得&nbsp;<strong>" + boughtCount + "</strong>&nbsp;枚夺宝码，今日已购买</font>";
                                        divBottom22Html = "<font>预计获奖率&nbsp;</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 10000) / 100.00 + "%</strong></font>，<font>收益</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 100 * rewardPrice) / 100.00 +"</strong></font>";
                                        divBottom23Html = "<button type='button' class='btn btn-info btn-sm' onClick='viewTicket()'>查看夺宝码</button>";
                                        $("#divBottom21").html(divBottom21Html);
                                        $("#divBottom22").html(divBottom22Html);
                                        $("#divBottom23").html(divBottom23Html);
                                        $("#inviteSpss").html(inviteSpssHtml);
                                        $("#divBottom21").show();
                                        $("#divBottom22").show();
                                        $("#divBottom23").show();
                                    } else if(todayBoughtCount < infoData.limitEveryday) {
                                        inviteSpssHtml = "<font>&nbsp;已获得&nbsp;<strong>" + acceptInviteCount + "</strong>&nbsp;枚夺宝码奖励</font>";

                                        divBottom21Html = "<font>&nbsp;共获得&nbsp;<strong>" + boughtCount + "</strong>&nbsp;枚夺宝码，今日已购买";
                                        divBottom22Html = "<font>预计获奖率&nbsp;</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 10000) / 100.00 + "%</strong></font>，<font>收益</font><font style='color:red;'><strong>" + Math.round(boughtCount / nowCount * 100 * rewardPrice) / 100.00 +"</strong></font>";
                                        divBottom23Html = "<button type='button' style='float: left; margin-left: 20%;' class='btn btn-info btn-sm' onClick='buyClickInvite()'>继续抢购</button>" + "<button type='button' style='float: right; margin-right: 20%;' class='btn btn-info btn-sm' onClick='viewTicket()'>查看夺宝码</button>";
                                        $("#divBottom21").html(divBottom21Html);
                                        $("#divBottom22").html(divBottom22Html);
                                        $("#divBottom23").html(divBottom23Html);
                                        $("#inviteSpss").html(inviteSpssHtml);
                                        $("#divBottom21").show();
                                        $("#divBottom22").show();
                                        $("#divBottom23").show();
                                    }

                                    $("#strHaved2").text(nowCount);
                                }
                            }
                        });
                    }
                }

                //点解查看夺宝码按钮
                window.viewTicket = function() {
                    showDefault("loading");
                    $.ajax({
                        type: "GET",
                        url: baseUrl + "lotteryInfo/getOne",
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
                                            if (detailTemp[index].backup4 == 5) {
                                                status = '邀请' + detailTemp[index].backup1 + '，暂未接受';
                                            }else if(detailTemp[index].backup4 == 6){
                                                status = '邀请' + detailTemp[index].backup1 + '成功奖励';
                                            }else if(detailTemp[index].backup4 == 7){
                                                status = '邀请失败,' + detailTemp[index].backup1 + '未接受';
                                            }else if(detailTemp[index].backup4 == 8){
                                                status = '邀请失败,' + detailTemp[index].backup1 + '未接受，10SZB已退还';
                                            }else if(detailTemp[index].backup4 == 0){
                                                if(detailTemp[index].backup1 == ''){
                                                    status = '自身购买';
                                                }else if(detailTemp[index].backup1 == 'admin'){
                                                    status = '接受' + detailTemp[index].backup2 + '邀请奖励';
                                                }else {
                                                    status = '邀请' + detailTemp[index].backup1 + '成功奖励';
                                                }
                                            }
                                            
                                            var ticket = detailTemp[index].backup3 == 0 ? "生成中" : detailTemp[index].ticket;
                                            // htmlStr1 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailTemp[index].backup2+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;'>"+ticket+"</td>";
                                            if(detailTemp[index].backup4 == 6 || detailTemp[index].backup4 == 0){
                                                htmlStr1 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailTemp[index].hashcode+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;'>"+ticket+"</td><td id='tdOption' style='word-break: break-all;text-align: center;'>"+ status +"</td></tr>";
                                            }else if(detailTemp[index].backup4 == 5){
                                                htmlStr1 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;color:gray;'>"+detailTemp[index].hashcode+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;color:gray;'>"+ticket+"</td><td id='tdOption' style='word-break: break-all;text-align: center;color:gray;'>"+ status +"</td></tr>";
                                            }else if(detailTemp[index].backup4 == 8 || detailTemp[index].backup4 == 7){
                                                htmlStr1 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;color:gray;'>"+detailTemp[index].hashcode+"</td><td id='tdTicket' style='word-break: break-all;text-align: center;color:gray;text-decoration:line-through;'>"+ticket+"</td><td id='tdOption' style='word-break: break-all;text-align: center;color:gray;'>"+ status +"</td></tr>";
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
                                        if (detailTemp[index].backup4 == 1) {
                                            option = infoTemp.backup1;
                                        }else if(detailTemp[index].backup4 == 2){
                                            option = infoTemp.backup2;
                                        }
                                        
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
    <body style="font-family:微软雅黑;" >
		 <font id="itcode" hidden="hidden">${itcode}</font>
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
                            <button type='button' class='btn btn-link gameDescription'>夺宝玩法简介</button>
                        </div>
                        </div>
                    </div>
                </div>
                <!-- typecode为0/1的显示方式end -->

                <!-- typecode为2的显示方式 -->
                <div id='optionLottery' class="col-xs-12 col-md-12" align="center" style="margin-top: 5%;display: none;">
                    <div style="border: 1px solid #EDEDED;">
                        <img id="bkImg1" src=""  class="img-responsive center-block" style="padding-top: 1px;padding-bottom: 5px;">
                        <div style="border: 1px solid #EDEDED;border-top: none;">
                            <font id="fontReword1" style="text-align: center;"></font>
                            <div class="progress"></div>
                            <font style="text-align: center;color: gray;">当前累计参与&nbsp;<strong id="strHaved1"></strong>&nbsp;人次</font>
                        </div>
                        <div id="divBottom1" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div id="divBottom2" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div id="divBottom3" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                            <button type='button' class='btn btn-link gameDescription'>夺宝玩法简介</button>
                        </div>
                    </div>
                </div>
                <!-- typecode为2的显示方式end -->
                <!-- 带邀请好友功能的显示方式 -->
                <div id='inviteLottery' class="col-xs-12 col-md-12" align="center" style="margin-top: 5%;display: none;">
                    <div style="border: 1px solid #EDEDED;">
                        <img id="bkImg2" src=""  class="img-responsive center-block" style="padding-top: 1px;padding-bottom: 5px;">
                        <div style="border: 1px solid #EDEDED;border-top: none;">
                            <font id="fontReword2" style="text-align: center;"></font><br>
                            <!-- <div class="progress"></div> -->
                            <font style="text-align: center;color: gray;">当前累计参与&nbsp;<strong id="strHaved2"></strong>&nbsp;人次</font>
                        </div>
                        <div id="divBottom21" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div id="divBottom22" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div id="divBottom23" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                            <button type='button' class='btn btn-link gameDescription'>夺宝玩法简介</button>
                        </div>
                    </div>
                </div>
                <!-- 带邀请好友功能的显示方式end -->

                <div id="fannlManager" class="col-xs-12 col-md-12" style='margin-top: 1%;' hidden="hidden" align="center">
                    <div style="border: 1px solid #EDEDED;">
                        <div style='margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;'>
                            <button type='button' class='btn btn-default btn-sm' onclick="fannl()">管理</button>
                        </div>
                    </div>
                </div>
                <div id="inviteUser" class="col-xs-12 col-md-12" style='margin-top: 1%;' hidden="hidden" align="center">
                    <div style="border: 1px solid #EDEDED;">
                        <div id="inviteSpss" style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                        </div>
                        <div style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                            <button type='button' style='float: left; margin-left: 20%;' class='btn btn-warning btn-sm' onclick="inviteUser()">邀请用户</button>
                            <button type='button' style='float: right; margin-right: 20%;' class='btn btn-warning btn-sm' onclick="queryInvited()">查看邀请</button>
                        </div>
                        <div style="margin-top: 5px; margin-bottom: 5px;height: 30px;vertical-align: middle;">
                            <button id='inviteDescription' type='button' class='btn btn-link'>邀请玩法简介</button>
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
                                  <th style="text-align: center;color: gray;font-size: 80%;">夺宝码</th>
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
                                  <th style="text-align: center;color: gray;font-size: 80%;">夺宝码</th>
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
        <!-- 邀请类-查看邀请我的用户 -->
        <div class="modal" id="transactionModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div id="divKeystore" class="modal-body">
                        <table class="table table-condensed" style="table-layout: fixed;">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">邀请记录</caption>
                            <col style="width: 35%" />
                            <col style="width: 30%" />
                            <col style="width: 35%" />
                            <thead>
                                <tr>
                                  <th style="text-align: center;color: gray;font-size: 80%;">邀请人</th>
                                  <th style="text-align: center;color: gray;font-size: 80%;">邀请时间</th>
                                  <th style="text-align: center;color: gray;font-size: 80%;">是否接受</th>
                                </tr>
                            </thead>
                            <tbody id="modalTbody2">
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