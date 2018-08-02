$(function(){
	$("button").click(function(){
		var commandCode = $(this).attr("id").substring($(this).attr("id").length - 1);
		ajax("../work/cleanMachine.go",{
			commandCode : commandCode
		},function(msg){
			$("h3").show().text(msg.result).css("color",msg.suc?"green":"red");
			setTimeout(function(){
				$("#showSuc").hide();
			}, 3000);
		});
	});
});



/**
 * @param url 
 * @param data
 * @param func
 * */
function ajax(url,data,func){
	$.ajax({
		url : url,
		data : data,
		type : "post",
		dataType: "json",
		success : func
	});
}