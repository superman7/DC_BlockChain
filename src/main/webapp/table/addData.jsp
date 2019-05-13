<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="weaver.conn.RecordSet"%>
<%@ page import="weaver.general.BaseBean"%>
<%@ page import="weaver.file.FileUpload"%>
<%@ page import="weaver.hrm.*" %>
<%@ page import="weaver.general.*" %>
<% String tableName = request.getParameter("tableName"); %>
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
        var tableName="<%=tableName%>";
        var itcode = getCookie("itcode");
        var baseUrl = '/';
        var fields = "";
        var field = "";
        var fieldsHtml = "";
        var fieldName = "";
        var fieldType = "";
        $(function() {
        	$.ajax({
        		type:"GET",
        		url:"/table/getOne",
        		data:{"tableName":tableName,
        			"itcode":itcode	
        		},
        		dataType:"json",
        		success:function(data){
        			var list = data.list;
        			fields = list[0].fields;
        			field = fields.split(",");
        			for(var i = 0;i < field.length;i++){
        				fieldName = field[i].split(" ")[0];
        				fieldType = field[i].split(" ")[1];
        				//alert(fieldType);
        			fieldsHtml += "<li class='aui-list-item' ><div class='aui-list-item-inner'><div class='aui-list-item-label'>"+fieldName+"("+fieldType+")</div><div class='aui-list-item-input' ><input type='text' placeholder='添加数据' id='"+fieldName+"'></div></div></li>";
        			}
        			$("#form").html(fieldsHtml);
        		}
        	});
            window.commit = function(){
            	var fieldValues = "";
            	var fieldNames = "";
            	for(var i = 0;i < field.length;i++){
    				fieldName = field[i].split(" ")[0];
    				fieldType = field[i].split(" ")[1];
    				var fieldValue = $("#"+fieldName).val();
    				if(fieldValue == ""){
    					alert("输入值为空");
    					return;
    				}
    				if(fieldType == "int"){
    					var regex = /^[+|-]?\d*\.?\d*$/;
    					if(!regex.test(fieldValue)){
    						alert("输入值不合法");
    						return;
    					}
    					if(fieldValue>2147483647||fieldValue<-2147483648){
    						alert("输入值不合法");
    						return;
    					}
    					
    				}
    				if(fieldType == "double") {
    					var regex = /^[+|-]?\d*\.?\d*$/;
    					if(!regex.test(fieldValue)){
    						alert("输入值不合法");
    						return;
    					}
    				}
    				if(fieldType == "varchar(20)"){
    					if(fieldValue.length<=0||fieldValue.length>20){
    						alert("输入值不合法");
    						return;
    					}
    					fieldValue = "'"+fieldValue+"'";
    				}
    				if(fieldType == "varchar(255)"){
    					fieldValue = "'"+fieldValue+"'";    					
    				}
    				fieldNames += fieldName+",";
    				fieldValues +=fieldValue+",";
    			}
            	fieldNames = fieldNames.substr(0,fieldNames.length-1);
            	fieldValues = fieldValues.substr(0,fieldValues.length-1);
            	console.log('{"tableName":'+tableName+',"itcode":'+itcode+',"fieldNames":'+fieldNames+',"fieldValues":'+fieldValues+'}');
            	$.ajax({
            		type:"GET",
            		url:baseUrl+"table/addDataToTable",
            		data:{
            			"tableName":tableName,
            			"itcode":itcode,
            			"fieldNames":fieldNames,
            			"fieldValues":fieldValues
            		},
            		dataType:"json",
            		success:function(data){
            			if(data.success){
            				alert("添加成功");
            				window.location.href = "/table/tableList.jsp";
            			}
            		}
            	});
            }
        })
        
          
        </script>
    </head>
    <body>
    <div>
    <h1 align="center">添加数据</h1>
    <div class="aui-content aui-margin-b-15">
    <ul class="aui-list aui-form-list" id = "form">
        
        
    </ul>

	<div class="aui-content aui-margin-b-15">
		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="commit()">提交</div>
     </div>
</div>
</div>

    </body>
</html>