$(function(){
	
	$("#username","#password").change(function(){
		$("#errorcode").text("").hide();
	});
	
	$("#submit").click(function(){
		var user = $("#username").val();
		var pd = $.md5($("#password").val());
		$.ajax({
			url : "/login/queryUser",
			type:"post",
			data :{
				username : user,
				password : pd
			},
			dataType : "json",
			success : function(msg){
				if(msg.suc){
					window.location.href = msg.result;
				}else{
					$("#errorcode").text(msg.result).css("color","red").show();
				}
			}
		})
	});
});