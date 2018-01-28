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

    $("#jqGrid1").jqGrid({
        colModel: [
            { label: 'ID', name: 'id', width: 100, key:true},
            { label: '列名', name: 'colName', width: 400, editable:true, edittype:"text"},
            { label: '列类型', name: 'colType', width: 400,editable:true, edittype:'select',
                formatter:'select', editoptions:{value:"VARCHAR:字符串;DOUBLE:小数;INT:整数"}},
        ],
        viewrecords: true, // show the current page, data rang and total records on the toolbar
        // width: 500,
        autowidth:true,
        rownumbers: true,
        rownumWidth: 25,
        height: 200,
        rowNum: 4,
        datatype: 'local',
        pager: "#jqGridPager1",
        caption: "数据列类型",
        ondblClickRow: function(id){
            if(id ){
                var rowData = $("#jqGrid1").jqGrid("getRowData", id);
                // $('#jqGridId').jqGrid('restoreRow',lastsel);
                $('#jqGrid1').jqGrid('editRow',id,{
                    keys : true,        //这里按[enter]保存
                    // url: s2web.appURL + "jq/save.action",
                    mtype : "POST",
                    restoreAfterError: true,
                    // extraparam: {
                    //     "ware.id": rowData.id,
                    //     "ware.warename": $("#"+id+"_name").val(),
                    //     "ware.createDate": $("#"+id+"_date").val(),
                    //     "ware.number": $("#"+id+"_amount").val(),
                    //     "ware.valid": $("#"+id+"_type").val()
                    // },
                    oneditfunc: function(rowid){
                        console.log(rowid);
                    },
                    successfunc: function(response){
                        alert("save success");
                        return true;
                    },
                    errorfunc: function(rowid, res){
                        console.log(rowid);
                        console.log(res);
                    }
                });
            }
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
		showList: 0,
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
			vm.showList = 1;
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
        getFileStructure: function () {
            var url = baseURL + "datasource/getFileStructure";
            $.ajax({
                type: "POST",
                url: url,
                contentType: "application/json",
                data: JSON.stringify(vm.config),
                success: function(r){
                    if(r.code === 0){
                        vm.showList = 2;
                        var gridArrayData = [];
                        // show loading message
                        $("#jqGrid1")[0].grid.beginReq();
                        for (var i = 0; i < r.columnTypes.length; i++) {
                            var item = r.columnTypes[i];
                            gridArrayData.push({
                                colName: item.colName,
                                colType: item.colType,
                                id: item.id
                            });
                        }
                        // set the new data
                        $("#jqGrid1").jqGrid('setGridParam', { data: gridArrayData});
                        // hide the show message
                        $("#jqGrid1")[0].grid.endReq();
                        // refresh the grid
                        $("#jqGrid1").trigger('reloadGrid');
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        getShowListOne: function () {
            vm.showList = 1;
        },

        saveDataSource: function () {
            var url = baseURL + "datasource/getFileStructure";
            $.ajax({
                type: "POST",
                url: url,
                contentType: "application/json",
                data: JSON.stringify(vm.config),
                success: function(r){
                    if(r.code === 0){
                        // alert('操作成功', function(){
                        // vm.reload();
                        // });

                        vm.showList = 0;
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
			vm.showList = 0;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});