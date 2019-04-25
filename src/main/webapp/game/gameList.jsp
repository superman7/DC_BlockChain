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
        <title>游戏首页</title>
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
        <script type='text/javascript' src='../js/check_login.js'></script>
        <script type="text/javascript" src='../js/cookie_util.js'></script>
        <script type="text/javascript">
            var itcode;
            var baseUrl = '/';
            var smbData;
            var newOpen;

            $(function() {
            	itcode = getCookie("itcode");
                $("#shouye").show();
                apiready = function(){
                    api.parseTapmode();
                }

                var tab = new auiTab({
                    element:document.getElementById("footer")
                },function(ret){
                    if(ret.index == 1) {
                       $("#wode").hide();
                        $("#jinxingzhong").hide();
                        $("#shouye").show(); 
                    }
                    if(ret.index == 2) {
                        $("#wode").hide();
                        $("#shouye").hide();
                        $("#jinxingzhong").show();
                    }
                    if(ret.index == 3) {
                        $("#shouye").hide();
                        $("#jinxingzhong").hide();
                        $("#wode").show();
                    }
                });

               
            })
            
            $(function() {
                itcode = getCookie("itcode");
                $.ajax({
                    type: "GET",
                    url: "/game/gameInfo/getData",
                    data: {"itcode" : itcode},
                    dataType: "json",
                    success: function(data) {
                    	//alert(data.success);
                        if (data.success) {
                        	var htmlStr1 = "";                            
                            smbData = data.smbData;
                            var d = new Date(smbData.lotteryTime);
                            var times=(d.getMonth() + 1 < 10 ? "0" + (d.getMonth() + 1) : d.getMonth() + 1) 
                            			+ '-' 
                           				+ (d.getDate() < 10 ? ("0" + d.getDate()) :d.getDate())
                           				+ ' ' 
                            			+ (d.getHours() < 10 ? ("0" + d.getHours()) :d.getHours())
                            			+ ':' + (d.getMinutes() < 10 ? ("0" + d.getMinutes()) :d.getMinutes())                            
                            htmlStr1 += "<tr id='tr' bgcolor='#e51c23'><td id='lotteryTime' style='word-break: break-all;text-align: center;'>"+times
                            +"</td><td id='lotteryId' style='word-break: break-all;text-align: center;'>"+smbData.id
                            +"</td><td id='winTicket' style='word-break: break-all;text-align: center;'>"+"正在进行中"
                            +"</td><td id='lotteryPerson' style='word-break: break-all;text-align: center;'>"+smbData.nowSumPerson
                            +"</td><td id='winTicket' style='word-break: break-all;text-align: center;'>"+ smbData.unitPrice*smbData.nowSumPerson +"</td></tr>";
                        
                            //alert(smbData);
                            //当前未开奖信息
                            newOpen = data.newOpen;
                            //当前已开奖信息
                            
                          //  alert(smbData.lotteryTime);
                            for(var index = 0; index < newOpen.length; index++) {
                            	d = new Date(newOpen[index].lotteryTime);
                                times=(d.getMonth() + 1 < 10 ? "0" + (d.getMonth() + 1) : d.getMonth() + 1) 
                                + '-' 
                                + (d.getDate() < 10 ? ("0" + d.getDate()) :d.getDate())
                                + ' ' 
                                + (d.getHours() < 10 ? ("0" + d.getHours()) :d.getHours())
                                + ':' + (d.getMinutes() < 10 ? ("0" + d.getMinutes()) :d.getMinutes())
                                htmlStr1 += "<tr bgcolor='#03a9f4'><td id='lotteryTime' style='word-break: break-all;text-overflow:ellipsis;word-break:keep-all; white-space:nowrap;'>"+times
                                +"</td><td id='lotteryId' style='word-break: break-all;text-align: center;'>"+newOpen[index].id
                                +"</td><td id='winTicket' style='word-break: break-all;text-align: center;'>"+newOpen[index].winTicket
                                +"</td><td id='lotteryPerson' style='word-break: break-all;text-align: center;'>"+newOpen[index].nowSumPerson
                                +"</td><td id='winTicket' style='word-break: break-all;text-align: center;'>"+ newOpen[index].winSumAmount +"</td></tr>";
                            }
                            //alert(htmlStr1);
                            $("#modalTbody").html(htmlStr1);
                            $("#tr").click(function(){
                            	//正在进行中的表格添加点击事件
                            	window.location.href = baseUrl+"game/gameBuy.jsp?itcode="+itcode+"&id="+smbData.id;
                            })
                    	}
                	}
                });
                
                //发送请求，获取夺宝信息，查看该用户是否参与此次夺宝，若参与，展示夺宝号码
                $.ajax({
                    type: "GET",
                    url: "/game/gameInfo/getData",
                    data: {"itcode" : itcode},
                    dataType: "json",
                    success: function(data) {
                    	//alert(data.success);
                        if (data.success) {
                            smbData = data.smbData;
                            newOpen = data.newOpen;
                            var innerHtml = "";
                            var innerHtml2 = "";
                            innerHtml += "<div class='aui-card-list' onClick='clickToDetail("+smbData.id+")'><div class='aui-card-list-header' style='font-size:140%;'>【第"+smbData.id+"期】"+smbData.description+"</div><div class='aui-card-list-content' align='center'><img src='../img/game1.jpg' class='img-responsive center-block' style='height: 130px;width: 93%;'></div><div class='aui-card-list-footer' align='center'><p><div><button type='button' class='btn btn-info btn-sm'>点击进入</button></div></p></div></div>"
                            $("#divLotteryList").prepend(innerHtml);
                        }
                    }
                });
                
              //进入单次抽奖详情页面
                window.clickToDetail = function(id) {
                    /* window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryBuyPage.jsp?itcode="+itcode+"&id="+id; */
                    window.location.href = "/game/gameBuy.jsp?itcode="+itcode+"&id="+id;
                }
              	
                $("#introduce").click(function() {
                    window.location.href = baseUrl + "game/gameIntroduce.jsp";
                });
                $("#myRecores").click(function() {
                    // window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryIntroduce.jsp";
                    alert("敬请期待！");
                });
                $("#myAchieve").click(function() {
                    // window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryIntroduce.jsp";
                    alert("敬请期待！");
                });
                $("#contactUs").click(function() {
                    // window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryIntroduce.jsp";
                    alert("如有任何疑问，请与fannl@digitalchina.com联系！");
                });
                
            })
            
            </script>
<body>
	<div id="jinxingzhong" hidden="hidden">
            <section class="aui-refresh-content">
                <div class="aui-content">
                    <div id="divLotteryList">
                        <div style="height: 50px;"> 
                        </div>
                    </div>
                </div>
            </section>
        </div>
        <div id="zuixinjiexiao" hidden="hidden">
            <section class="aui-refresh-content">
                <div class="aui-content">
                    <div id="divNewOpenList">
                        <div style="height: 50px;">
                        </div>
                    </div>
                </div>
            </section>
        </div>
        <div id="wode" hidden="hidden">
            <section class="aui-content">
                <ul class="aui-list aui-list-in aui-margin-b-15">
                    <li id="introduce" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/wanfajianjie.png" class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">玩法简介</div>
                        </div>
                    </li>
                    <li id="myRecores" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/duobaojilu.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">夺宝记录</div>
                        </div>
                    </li>
                    <li id="myAchieve" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/wodechengjiu.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">我的成就</div>
                        </div>
                    </li>
                    <li id="contactUs" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/lianxiwomen.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">联系我们</div>
                        </div>
                    </li>
                </ul>
            </section>
        </div>
    <div id="shouye" hidden="hidden">
    <div>
    
    <i><img src="../img/fapai.jpg"  class="img-responsive center-block" style="height: 180px;width: 100%;"></i>
    </div>
    <div>
    <table class="table table-condensed" style="table-layout: auto; margin:auto;">
        <caption id="tabCaption" style="word-break: break-all;text-align:center;">往期记录</caption>
        <col style="width: 20%" />
        <thead>
            <tr>
                <th style="color: gray;font-size: 80%; text-align=center">开奖时间</th>
                <th style="color: gray;font-size: 80%;text-align=center">期号</th>
                <th style="color: gray;font-size: 80%;text-align=center">中奖号码</th>
                <th style="color: gray;font-size: 80%;text-align=center">中奖人数</th>
                <th style="color: gray;font-size: 80%;text-align=center">返奖金额</th>
            </tr>
        </thead>
        <tbody id="modalTbody">
        </tbody>
    </table>
    </div>
    </div>
    <footer class="aui-bar aui-bar-tab" id="footer">
            <div class="aui-bar-tab-item aui-active" tapmode>
                <i><img src="../img/shouye.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">首页</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/zuixinjiexiao.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">进行中</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/wode.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">我的</div>
            </div>
        </footer>
</body>
</html>