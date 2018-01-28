$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'datasource/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', width: 20, key: true },
            { label: '文件名', name: 'name', width: 60 },
            { label: '文件类型', name: 'dataSourceType', width: 60 },
			{ label: '创建时间', name: 'createDate', width: 40 }
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

    new AjaxUpload('#upload', {
        action: baseURL + 'datasource/upload?token=' + token,
        name: 'file',
        autoSubmit:true,
        responseType:"json",
        onSubmit:function(file, extension){
            // if(vm.config.type == null){
            //     alert("云存储配置未配置");
            //     return false;
            // }
            if (!(extension && /^(txt|csv|dat)$/.test(extension.toLowerCase()))){
                alert('只支持txt、csv、dat格式的文件！');
                return false;
            }
        },
        onComplete : function(file, r){
            if(r.code == 0){
                // alert(r.name+"上传成功!");
                vm.config.filePath=r.name;
            }else{
                alert(r.msg);
            }
        }
    });

});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
        config: {}
	},
    created: function(){
        this.getConfig();
        // console.info("no need to getConfig()")
    },
	methods: {
		query: function () {
			vm.reload();
		},
		getConfig: function () {
            $.getJSON(baseURL + "datasource/config", function(r){
				vm.config = r.config;
            });
        },
		addConfig: function(){
			vm.showList = false;
			vm.title = "数据源配置";
			vm.config.type=1;

		},
		saveOrUpdate: function () {
			var url = baseURL + "datasource/saveConfig";
			$.ajax({
				type: "POST",
			    url: url,
                contentType: "application/json",
			    data: JSON.stringify(vm.config),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
        del: function () {
            var ossIds = getSelectedRows();
            if(ossIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "datasource/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ossIds),
                    success: function(r){
                        if(r.code === 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
		reload: function () {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});