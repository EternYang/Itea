var clickItem;

$(function() {
	
	searchInven();
	
	$("#savechange").click(function(){
		console.log(clickItem);
		if($("#beiliao").val() == clickItem.attr("itemPrepared")){
			return;
		}
		$.ajax({
			url : "../work/changePrepared",
			data : {
				itemName : clickItem.attr("itemName"),
				itemPrepared : $("#beiliao").val()
			},
			dataType : "json",
			success : function(msg) {
				if(msg.suc){
					$("#showSuc").show().text("修改成功").css("color","green");
					$("#beiliao").val(msg.result[0].ItemPrepared);
					searchInven();
				}else{
					$("#showSuc").show().text("修改失败").css("color","red");
				}
				setTimeout(function(){
					$("#showSuc").hide();
					$(clickItem.find("h4")).text("备料:" + $("#beiliao").val() + "ml");
					$("#myModal").modal("hide");
				}, 3000);
				
			}
		});
	});
	
	//发出清洗某个机器的指令
	$("#cleanMachine").click(function(){
		$.ajax({
			url : "../work/cleanMachine",
			type : "post",
			data : {
				itemName : clickItem.attr("itemName"),
				commandCode : "3"
			},
			success : function(msg){
				$("#showSuc").show().text(msg.result).css("color",msg.suc?"green":"red");
				setTimeout(function(){
					$("#showSuc").hide();
				}, 3000);
			}
		});
	});
	
	$('#myModal').on('shown.bs.modal', function (e) {
        // 关键代码，如没将modal设置为 block，则$modala_dialog.height() 为零
        $(this).css('display', 'block');
        var modalHeight=$(window).height() / 2 - $('#myModal .modal-dialog').height() / 2;
        $(this).find('.modal-dialog').css({
            'margin-top': modalHeight
        });
    });
	
});


function searchInven(){
	// 查询所有库存
	$.ajax({
		url : "../work/queryAllInventory",
		dataType : "json",
		success : function(result) {
			if (result.suc) {
				var msg = result.result;
				var row = 0;
				$("#dom").html("");
				// 遍历所有库存
				for (var i = 1; i <= msg.length; i++) {
					// 满4个，换行
					if (i % 4 == 1) {
						row++;
						$("#row").clone(true).attr("id", "row" + row).appendTo(
								"#dom");
					}
					var num = i % 4 == 0 ? 3 : i % 4 - 1;
					var $div = $($("#row" + row).find(".col-md-3")[num]);
					$div.attr("id", "id" + i);
					$("#modal_h3").clone(true).attr("id", msg[i - 1].ItemDesc)
							.text(msg[i - 1].ItemDesc).appendTo($div);
					$("#modal_div").clone(true).attr("id","eDom" + i).height("100%").appendTo($div);
					initEcharts(msg[i - 1].percentage,"eDom" + i,msg[i - 1]);
					$("#modal_h4").clone(true).attr("id", msg[i - 1].ItemStore)
					.text("备料:" + msg[i - 1].ItemPrepared + "ml").appendTo($div);
					var c = msg[i - 1];
					$div.attr("itemStore",c.ItemStore).attr("itemName",c.ItemName).attr("itemDesc",c.ItemDesc).attr("itemPrepared",c.ItemPrepared).click(function(){
						$("#myModal").modal("show");
						$("#myModalLabel").text($(this).attr("itemDesc"));
						$("#yuliang").text($(this).attr("itemStore"));
						$("#beiliao").val($(this).attr("itemPrepared"));
						clickItem = $(this);
					});
				}
			}
		}
	});
}



/**
 * 展示echarts
 * @param e value
 * @param sel selector of element
 * @param data
 * */
function initEcharts(e,sel,data){
	var co = "#99FF66";
	if(e <= 50 && e > 20){
		co = "#FFFF66";
	}
	if(e <= 20){
		co = "#FF0000";
	}
	var Chart4 = echarts.init(document.getElementById(sel));
	option = {
        	title:{
			        show:true,
			        text:e + "%\n" + data.ItemStore + "ml",
			        x:'center',
		        	y:'center',
			        textStyle: {
	                    fontSize: '15',
	                    color:"black",
	                    fontWeight: 'normal'
	                }
		       },
		    tooltip: {
		        trigger: 'item',
		        formatter: "{d}%\n" + data + "ml",
		        show:false
		    },
		    legend: {
		        orient: 'vertical',
		        x: 'left',
		        show:false
		    },
		    series: 
		        {
		            name:'',
		            type:'pie',
		            radius: ['65%', '85%'],
		            avoidLabelOverlap: true,
		            hoverAnimation:false,
		            label: {
		                normal: {
		                    show: false,
		                    position: 'center'
		                },
		                emphasis: {
		                    show: false
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: false
		                }
		            },
		            data:[
		                {value:e, name:data.ItemDesc,id:data.ItemName},
		                {value:100-e, id:data.ItemName}
		            ]
		        },
		        color:[co,"#C0C0C0"]
		    
		};
        Chart4.setOption(option);
        
       /* Chart4.on("click",function(params){
        	console.log(params);
        	$("#myModal").modal("show");
        	$("#myModalLabel").text(params.data.id);
        });*/
}