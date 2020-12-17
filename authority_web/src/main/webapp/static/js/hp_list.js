$(function() {
	layui.use('table', function() {
		
		//新增
		$('.bt_add').on('click', function() {
			var d = $(this).attr("data");
			layer.open({
				type : 2,
				title : '新增',
				shadeClose : true,
				area : [ d.split(",")[0], d.split(",")[1]],
				content : $(this).attr("data-url")
			});
		})
		
		//修改
		$('.bt_update').on('click', function() {
			var checkStatus = layui.table.checkStatus($('table').attr("id"));
			if (checkStatus.data.length<1) {
				layer.msg("至少选择一行数据", {icon: 2, time: 1500});
				return;
			}
			if (checkStatus.data.length>1) {
				layer.msg("至多只能选择一行数据", {icon: 2, time: 1500});
				return;
			}
			var id = checkStatus.data[0].id;
			
			var d = $(this).attr("data");
			layer.open({
				type : 2,
				title : '修改',
				shadeClose : true,
				area : [ d.split(",")[0], d.split(",")[1]],
				content : $(this).attr("data-url")+"?id="+id
			});
		})
		
		//删除
		$('.bt_delete').on('click', function() {
			console.log("ok");
			var checkStatus = layui.table.checkStatus($('table').attr("id"));
			if (checkStatus.data.length<1) {
				layer.msg("至少选择一行数据", {icon: 2, time: 1500});
				return;
			}
			
			$.ajax({
			    type: "POST",
			    url: $(this).attr("data-url"),
			    contentType : "application/json",
			    dataType: 'json',  
			    data: JSON.stringify(checkStatus.data),  
			    success: function(data){
			    	if (data.result==true) {
			    		layer.msg(data.msg, {icon: 1, time: 1500});
			    		layui.table.reload($('table.layui-hide').attr("id"));
			    	}else {
			    		layer.msg(data.msg, {icon: 2, time: 1500});
			    	}
			    },  
			    error: function(res){
			    	layer.msg("未知异常", {icon: 2});
			    }  
			});
		});
		
		//分配角色
		$('.bt_setRole').on('click', function() {
			console.log("ok");
			var checkStatus = layui.table.checkStatus($('table').attr("id"));
			if (checkStatus.data.length<1) {
				layer.msg("至少选择一行数据", {icon: 2, time: 1500});
				return;
			}
			if (checkStatus.data.length>1) {
				layer.msg("至多只能选择一行数据", {icon: 2, time: 1500});
				return;
			}
			var id = checkStatus.data[0].id;
			
			var d = $(this).attr("data");
			layer.open({
				type : 2,
				title : '设置角色',
				shadeClose : true,
				area : [ d.split(",")[0], d.split(",")[1]],
				content : $(this).attr("data-url")+"?id="+id
			});
		});
		
		//分配权限
		$('.bt_setMenu').on('click', function() {
			console.log("ok");
			var checkStatus = layui.table.checkStatus($('table').attr("id"));
			if (checkStatus.data.length<1) {
				layer.msg("至少选择一行数据", {icon: 2, time: 1500});
				return;
			}
			if (checkStatus.data.length>1) {
				layer.msg("至多只能选择一行数据", {icon: 2, time: 1500});
				return;
			}
			var id = checkStatus.data[0].id;
			
			var d = $(this).attr("data");
			layer.open({
				type : 2,
				title : '设置权限',
				shadeClose : true,
				area : [ d.split(",")[0], d.split(",")[1]],
				content : $(this).attr("data-url")+"?id="+id
			});
		});
		
	})
	
})

//回调方法
function closeLayer(msg) {
	layui.table.reload($('table.layui-hide').attr("id"));
	layer.msg(msg, {icon: 1, time: 1500});
	layer.closeAll('iframe');
}