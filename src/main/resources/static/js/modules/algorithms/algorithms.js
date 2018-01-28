$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'algorithm/list',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'id', index: 'id', width: 30, key: true },
			{ label: '算法', name: 'algorithmName', index: 'algorithm_name', width: 50 },
			{ label: '数据源', name: 'dataSourceEntity.id', index: 'data_source_entity_id', width: 30 },
			{ label: '算法状态', name: 'algorithmStatus', index: 'algorithm_status', width: 60 },
			{ label: '运行信息', name: 'information', index: 'information', width: 120 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [5,10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		algorithms: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.algorithms = {};
		},
		update: function (event) {
			var algorithmsId = getSelectedRow();
			if(algorithmsId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(algorithmsId)
		},
		saveOrUpdate: function (event) {
			var url = vm.algorithms.id == null ? "algorithm/save" : "algorithm/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.algorithms),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var algorithmsIds = getSelectedRows();
			if(algorithmsIds == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "algorithm/delete",
                    contentType: "application/json",
				    data: JSON.stringify(algorithmsIds),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(algorithmsId){
			$.get(baseURL + "algorithm/info/"+algorithmsId, function(r){
                vm.algorithms = r.algorithms;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});