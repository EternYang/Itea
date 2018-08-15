$(function() {

	$(window).unload(function() {
		ws.close();
	});
	createWebSocket(wsurl);
	
	eventBind();

	// 发送请求，提醒服务器开始循环
	$.ajax({
		url : "../work/queryBegin.go"
	});

});


/**
 * 事件绑定
 */
function eventBind() {
	$("#calling2Missed").click(
			function() {
				var orderId = isEmpty($("#callingSelect").attr("orderId")) ? ""
						: $("#callingSelect").attr("orderId");
				var nextOrderId = isEmpty($("#nextSelect option:selected")
						.val()) ? "" : $("#nextSelect option:selected").val();
				$.ajax({
					url : "../work/changeErrorOrMiss.go",
					data : {
						orderId : orderId,
						status : "-1",
						nextId : nextOrderId,
						nextStatus : "3"
					},
					dataType : "json",
					success : function(msg) {
						parent.location.reload();
					}
				})
				// queryOrders();
			});

	$("#next2calling").click(
			function() {
				var orderId = isEmpty($("#callingSelect").attr("orderId")) ? ""
						: $("#callingSelect").attr("orderId");
				var nextOrderId = isEmpty($("#nextSelect option:selected")
						.val()) ? "" : $("#nextSelect option:selected").val();
				$.ajax({
					url : "../work/changeStatus.go",
					data : {
						orderId : orderId,
						nextId : nextOrderId
					},
					dataType : "json",
					success : function(msg) {
						parent.location.reload();
					}
				})
			});

	$("#missedover").click(function() {
		var orderId = $("#missSelect option:selected").val();
		$.ajax({
			url : "../work/changeErrorOrMiss.go",
			data : {
				orderId : orderId,
				status : "4",
				nextId : "",
				nextStatus : ""
			},
			dataType : "json",
			success : function(msg) {
				parent.location.reload();
			}
		})
	});

	$("#restart").click(
			function() {
				var orderId = $("#errorSelect option:selected").val();
				$.ajax({
					url : "../work/changeErrorOrMiss.go",
					data : {
						orderId : orderId,
						status : "0"
					},
					dataType : "json",
					success : function(msg) {
						parent.location.reload();
					}
				})
			});
}


/**
 * 定义websocket
 * 
 */
var ws = null;
// 避免重复连接
var lockReconnect = false;
var wsurl = "ws://192.168.1.145:8080/Itea/socketServer";
createWebSocket(wsurl);
function createWebSocket(url){
	if ("WebSocket" in window) {
		ws = new WebSocket(wsurl);
	} else if ("MozWebSocket" in window) {
		ws = new MozWebSocket(wsurl);
	} else {
		alert("your brower isn't support websocket,please use FireFox,Chrome,or IE10+");
		return;
	}
	console.log("reqeusting connect...");
	connect();
}
function connect() {
	
	ws.onmessage = function(msg) {
		if(msg.data == "from server"){
			console.log(msg.data);
			return;
		}
		console.log(JSON.parse(msg.data));
		displayOrders(JSON.parse(msg.data).orders);
		displayInvent(JSON.parse(msg.data).inventory);
		displayStatus(JSON.parse(msg.data).status);
	}
	ws.onopen = function() {
		console.log("服务器已连接");
	}
	ws.onclose = function() {
		console.log("服务器连接已关闭");
		createWebSocket(wsurl);
	}
	ws.onerror = function(msg) {
		console.log(msg.data);
		reconnect(wsurl);
	}
}

/**
 * 展示后台接收到的order数据
 */
function displayOrders(msg) {
	if (!msg) {
		return;
	}
	selectedNext = $("#nextSelect option:selected").val();
	selectedMissed = $("#missSelect option:selected").val();
	console.log(msg);
	// $("option").remove();
	if (!isEmpty(msg.calling)) {
		var calling = msg.calling[0];
		$("#callingSelect").text(calling.orderNO.substring(calling.orderNO.length-4)).attr("orderId",calling.orderId);
		/*$("#modal_option").clone(true).removeAttr("id").text(
				calling.orderNO.substring(next.orderNO.length - 4)).attr(
				"selected", "selected").val(calling.orderId).appendTo(
				"#callingSelect");*/
	}
	if (!isEmpty(msg.next)) {
		if (msg.next.length > $("#nextSelect").find("option").length) {
			// 可以提示有新完成的订单 }
		}
		$("#nextSelect").html("");
		$("#modal_option").clone(true).removeAttr("id").appendTo("#nextSelect");
		for (var i = 0; i < msg.next.length; i++) {
			var next = msg.next[i];
			$("#modal_option").clone(true).removeAttr("id").text(
					next.orderNO.substring(next.orderNO.length - 4)).attr(
					"selected",
					selectedNext == next.orderId ? "selected" : null).val(
					next.orderId).appendTo("#nextSelect");
		}
		$("#nextSelect").multiselect("rebuild");
	}
	if (!isEmpty(msg.missed)) {
		$("#missSelect").html("");
		$("#modal_option").clone(true).removeAttr("id").appendTo("#missSelect");
		for (var i = 0; i < msg.missed.length; i++) {
			var missed = msg.missed[i];
			$("#modal_option").clone(true).removeAttr("id").text(
					missed.orderNO.substring(missed.orderNO.length - 4)).attr(
					"selected",
					selectedMissed == missed.orderId ? "selected" : null).val(
					missed.orderId).appendTo("#missSelect");
		}
		$("#missSelect").multiselect("rebuild");
	}
	if (!isEmpty(msg.error)) {
		showOrHideError(true);
		var error = msg.error[0];
		$("#errorSelect").html("");
		$("#modal_option").clone(true).removeAttr("id").appendTo("#errorSelect");
		$("#modal_option").clone(true).removeAttr("id").text(
				error.orderNO.substring(error.orderNO.length - 4)).attr(
				"selected", "selected").val(error.orderId).appendTo(
				"#errorSelect");
	} else {
		showOrHideError(false);
	}
	$("#missSelect").multiselect();
	$("#nextSelect").multiselect();
	//$("#callingSelect").multiselect();
	$("#errorSelect").multiselect();
}

/**
 * 展示后台接收到的最小库存数据
 */
function displayInvent(msg) {
	console.log(msg);
	if (!msg) {
		return;
	}
	if (msg.length > 0) {

		console.log(msg[0]);
		if (msg[0].percentage > 20 && msg[0].percentage <= 50) {
			$("#showInvent").text("余量警告：有材料余量低于50%，请知悉！").css("color",
					"#FF9900");
			return;
		}
		if (msg[0].percentage <= 20) {
		}
		$("#showInvent").text("余量充足").css("color", "green");

		var obj = msg[0];
		$("#showInvent").text(
				"余量警告：材料" + obj.ItemDesc + "等余量低于25%，请及时添加！(1/" + msg.length
						+ ")").css("color", "red");
		return;
	}
	$("#showInvent").text("余量充足").css("color", "green");
}

/**
 * 展示后台接收到的工作状态数据
 */
function displayStatus(msg) {
	console.log(msg);
	if (!msg) {
		return;
	}
	var i = 0;
	if (msg.length > 5) {
		i = msg.length - 5;
	}
	for (i; i < msg.length; i++) { // 检查当前展示工作状态是否已满足5条
		if ($("#workstatus").find("li").length >= 5) {
			// 若满足，移除当前第一条消息
			$($("#workstatus").find("li:first")).remove();
		}
		var workingTime = msg[i].WorkingTime.substring(0, 8); // 判定消息是否是error信息
		if (msg[i].isError) { // 是，去后台根据errorcode查询错误信息，并展示
			var $li = $("#modal_li").clone(true).attr("id", ""); // 展示错误信息
			$($li.find("a")).text(msg[i].ErrorDesc).css("color", "red"); // 错误时间
			$($li.find("span")).text(workingTime.substring(0, 8)); // 红色字体并追加显示
			$li.css("color", "red").appendTo("#workstatus");
		} else {
			var $li = $("#modal_li").clone(true).attr("id", "");
			$($li.find("a")).text(msg[i].WorkingDesc);
			$($li.find("span")).text(msg[i].WorkingTime.substring(0, 8)).css(
					"color", "black");
			$li.appendTo("#workstatus");
		}
	}
}

function reconnect(url) {
	if (lockReconnect)
		return;
	lockReconnect = true;
	// 没连接上会一直重连，设置延迟避免请求过多
	setTimeout(function() {
		console.info("尝试重连..." + formatDate(new Date().getTime()));
		createWebSocket(url);
		lockReconnect = false;
	}, 5000);
}

/**
 * 心跳检测,每5s心跳一次 防止socket自动关闭
 */
/*var heartCheck = {
	timeout : 5000,
	timeoutObj : null,
	serverTimeoutObj : null,
	reset : function() {
		clearTimeout(this.timeoutObj);
		clearTimeout(this.serverTimeoutObj);
		return this;
	},
	start : function() {
		var self = this;
		this.timeoutObj = setTimeout(
				function() {
					// 这里发送一个心跳，后端收到后，返回一个心跳消息，
					// onmessage拿到返回的心跳就说明连接正常
					ws.send("HeartBeat"
							+ formatDate(new Date().getTime()));
					console.info("客户端发送心跳："
							+ formatDate(new Date().getTime()));
					self.serverTimeoutObj = setTimeout(function() {
						// 如果超过一定时间还没重置，说明后端主动断开了

						ws.close();
						// 如果onclose会执行reconnect，我们执行ws.close()就行了.
						// 如果直接执行reconnect 会触发onclose导致重连两次
					}, self.timeout)
				}, this.timeout)
	}
}


*/

// js中格式化日期
/*function formatDate(time){
    var date = new Date(time);

    var year = date.getFullYear(),
        month = date.getMonth()+1,//月份是从0开始的
        day = date.getDate(),
        hour = date.getHours(),
        min = date.getMinutes(),
        sec = date.getSeconds();
    var newTime = year + '-' +
                (month < 10? '0' + month : month) + '-' +
                (day < 10? '0' + day : day) + ' ' +
                (hour < 10? '0' + hour : hour) + ':' +
                (min < 10? '0' + min : min) + ':' +
                (sec < 10? '0' + sec : sec);

    return newTime;         
}*/

/**
 * 工具方法，排空判断
 */
function isEmpty(obj) {
	if (obj == undefined || obj == null || obj.length == 0) {
		return true;
	}
	return false;
}

/**
 * 工具方法，用于展示错误信息时，切换另外三个的class属性
 * 
 * @param boolean
 *            true,显示error，切换另外三个class为col-sd-3
 *            false，隐藏error，切换另外三个class为col-sd-4
 */
function showOrHideError(bo) {
	var divs = $("#showCallNumber").find("div");
	if (bo) {
		for (var i = 0; i < divs.length; i++) {
			$(divs[i]).attr("class", "col-xs-3 col-sm-3 col-md-3 col-lg-3");
		}
		$("#showError").show();
		var bgm = $('#sound')[0];
		bgm.play();
	} else {
		$("#showError").hide();
		for (var i = 0; i < divs.length; i++) {
			$(divs[i]).attr("class", "col-xs-4 col-sm-4 col-md-4 col-lg-4");
		}
	}
}

/**
 * String util
 */
String.prototype.startWith = function(str) {
	if (str == null || str == "" || this.length == 0
			|| str.length > this.length)
		return false;
	if (this.substr(0, str.length) == str)
		return true;
	else
		return false;
	return true;
}