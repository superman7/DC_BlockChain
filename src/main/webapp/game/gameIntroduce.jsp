<!DOCTYPE html>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="author" content="Weaver E-Mobile Dev Group" />
        <meta name="description" content="Weaver E-mobile" />
        <meta name="keywords" content="weaver,e-mobile" />
        <meta name="viewport" content="width=device-width,minimum-scale=1.0, maximum-scale=1.0" />
        <title>抽奖介绍页面</title>
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
    </head>
    <body style="font-family:微软雅黑; margin-top:5%;">
        <div class="container" style="">
            <div class="row">
                <div class="col-xs-12 col-md-12" align="center">
                    <h3 class="display-3" style="color:#FF0000">神州币单双游戏玩法简介</h3><br>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12 col-md-12">
                    <p class="lead" style="font-weight: bold;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;神州币单双游戏依托于神州区块链平台</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;每期游戏价值<mark>10 SZB</mark>，当触发开奖条件（人数满足）后，系统会根据既定开奖算法计算出一个幸运夺宝码，取幸运码的最后一位单双，尾数为单压单数人中奖，即可获得等额SZB。</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;夺宝码基于用户itcode+区块链交易hash值计算产生，完全随机，保证公平。</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;开奖算法结合多重参数及区块链区块计算，透明计算，任何用户均可验证，确保公正（具体可参见算法说明部分）。</p>
                </div>
            </div>
            
            <div class="row">
                <div class="col-xs-12 col-md-12" align="center">
                    <br><p class="lead" style="font-weight: bold;">开奖算法说明：</p>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12 col-md-12">
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;1.按开奖条件可将抽奖分为<mark>a.根据参与人数开奖;</mark><mark>b.根据设定时间开奖.</mark>两种。</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;2.当开奖条件触发时，系统将取出上期抽奖的<mark>获奖人</mark>及<mark>夺宝码</mark>，以及当前时间区块链上最后一个有效区块的<mark>Nonce值</mark>、<mark>timestamp值</mark>以及<mark>整个区块链的总难度值</mark>，将这五个参数使用MerkleTrees算法计算出一个64位的16进制Hash值。</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;3.将当期抽奖的全部夺宝码取出并排序，按上一步计算出的Hash值对夺宝码总数取模，确定唯一中奖码。</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;4.将本期开奖使用的区块Hash值、中奖者及中奖码记录至区块链，用以他人验证。</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;5.具体算法可参阅<a href="https://github.com/superman7/AccountManagement">Github开源仓库</a>。</p>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12 col-md-12" align="center">
                    <br><p class="lead" style="font-weight: bold;">Good Luck！</p><br>
                </div>
            </div>
        </div>  
    </body>
</html>