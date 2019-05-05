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
        <title>建表赢奖励</title>
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
        var itcode;
        var baseUrl = '/';
        $(function() {
        	itcode = getCookie("itcode");
            apiready = function(){
                api.parseTapmode();
            }

            var tab = new auiTab({
                element:document.getElementById("footer")
            },function(ret){
                if(ret.index == 1) {
                    window.location.href="/table/createTable.jsp";                	
                }
                if(ret.index == 2) {
                   window.location.href="/table/tableList.jsp";
                }
                if(ret.index == 3) {
                    window.location.href="/table/tableIntroduce.jsp";
                }
            });
            window.addField = function() {
    	        var i = $("#form li:last").children().children().html();
            	var num= Number(i.replace(/[^0-9]/ig,""))+1;
            	//alert(parseInt(num));
            	$("#form").append("<li class='aui-list-item'><div class='aui-list-item-inner'><div class='aui-list-item-label'>字段"+num+"</div><div class='aui-list-item-input'><input type='text' placeholder='名' id='field"+num+"'></div><div class='aui-list-item-label'>类型</div><div class='aui-list-item-input'><select id='type"+num+"'><option value = 'int'>int</option><option value = 'varchar(255)'>varchar(255)</option><option value = 'varchar(20)'>varchar(20)</option><option value='double'>double</option></select></div></li>");
            }
            window.commit = function(){
	            var li = $("#form li:last").children().children().html();
            	var num = Number(li.replace(/[^0-9]/ig,""));
            	alert(num);
            	var jsonStr = "";
            	var Str; 
            	var StrType;
            	for(var i = 1;i<=num; i++){
            		Str = "#field";
            		Str += i;
            		StrType = "#type";
            		StrType += i;
            		//alert($(StrType).val());
            		jsonStr += '"'+Str+'"'+':'+'"'+$(Str).val()+'"'+','+'"'+ StrType+'"'+':'+'"'+$(StrType).val()+'"'+',';
            	}
            	var tableName = $("#tableName").val();
            	var jsonStr1 = '{'+jsonStr+'"tableName":"'+tableName+'","itcode":"'+itcode+'"}';
            	var jsonStr2 = jsonStr1.replace(/\s/g,'');
            	jsonStr2 = jsonStr1.replace(/#/g,'');
            	alert(jsonStr2);
            	 $.ajax({
            		type:"GET",
            		url:"/wallet/getCheckUp.jsp",
            		data:{"jsonStr":jsonStr2},
            		dataType:"text",
            		success:function(data){
            			 $.ajax({
	                            type: "GET",
	                            url: baseUrl + "table/createTable",
	                            data: {"param":data},
	                            dataType: "json",
	                            error:console.log(data),
	                            success: function(data) {
	                            	alert(data.msg);
	                            	alert(data.success);
	                                window.location.href="/table/tableList.jsp";

	                            }
            			 });
            		}
            	});  

            	
            }
        })
        
          
        </script>
    </head>
    <body>
    <div>
    <h1 align="center">建表</h1>
    <div class="aui-content aui-margin-b-15">
    <ul class="aui-list aui-form-list" id = "form">
    	
         <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    表名
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="表名" id="tableName">
                </div>
            </div>
        </li>
        <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    字段1
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="名" id=field1>
                </div>
                <div class="aui-list-item-label">
                    类型
                </div>
                <div class="aui-list-item-input">
                    <select id="type1">
                        <option value = "int">int</option>
                        <option value = "varchar(255)">varchar(255)</option>
                        <option value = "varchar(20)">varchar(20)</option>                        
                        <option value="double">double</option>
                    </select>
                </div>
            </div>
        </li>
        <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    字段2
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="名" id="field2">
                </div>
                <div class="aui-list-item-label">
                    类型
                </div>
                <div class="aui-list-item-input">
                    <select id="type2">
                        <option value = "int">int</option>
                        <option value = "varchar(255)">varchar(255)</option>
                        <option value = "varchar(20)">varchar(20)</option>                        
                        <option value="double">double</option>
                    </select>
                </div>
            </div>
        </li>
        
    </ul>
     <div class="aui-content aui-margin-b-15">
		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="addField()">添加字段</div>
		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="commit()">提交</div>
     </div>

</div>
</div>
       
        <footer class="aui-bar aui-bar-tab" id="footer">
            <div class="aui-bar-tab-item aui-active" tapmode>
                <i><img src="../img/shouye.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">建表</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/zuixinjiexiao.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">列表</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/wode.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">我的</div>
            </div>
        </footer>
    </body>
</html>