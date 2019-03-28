$(function(){
	//登录成功之后首先获取cookie，进行判断
	//获取用户登录后保存的cookie值
	var itcode = getCookie("itcode");
	//判断cookie是否为空
	//为空则跳转到登录页面
	if(itcode == "" ||itcode == null){
		window.location.href = "login.jsp";
	}
});