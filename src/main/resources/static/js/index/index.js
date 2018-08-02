$(function() {

	$("a").click(function(e) {
		//移除以前的active属性
		for(var i = 0 ; i < $($("#navs").find("li")).length ; i++){
			var li = $("#navs").find("li");
			if($(li).hasClass("active")){
				$(li).removeClass("active");
			}
		}
		//获取被点击a连接的id
		var id = $(this).attr("id");
		//添加被选择的active属性
		$(this).parent().addClass("active");
		if (id == "showWorking") {
			$("#external-frame").attr("src", "../itea/work");
		}
		if (id == "showInventory") {
			$("#external-frame").attr("src", "../itea/inventory");
		}
		if (id == "showSetting") {
			$("#external-frame").attr("src", "../itea/setting");
		}
	});

})


/**
 * 工具方法，排空
 */
function isEmpty(obj) {
	if (obj == undefined || obj == null || obj.length == 0) {
		return true;
	}
	return false;
}

/**
 * 工具方法，设置iframe宽高值
 * */
function setIframeHeight() {
	$("#external-frame").height(document.documentElement.clientHeight)
		.width(document.documentElement.clientWidth - $(".leftTree").width() - 40);
};
