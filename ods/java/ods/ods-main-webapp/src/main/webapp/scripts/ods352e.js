//文件載入完成後執行
$(document).ready(function() {
    // 事件綁定
    bindEvent();
    init();    
    
   $('#deleteConfirmDialog').dialog({
        modal:true,
        title:'刪除確認',
        close: function() {
        },
        buttons: {
          "否":function() {
            $("#deleteConfirmDialog").dialog("close");
          },
          "是":function() {
              
              ods352e_delete_confirm();
            $("#deleteConfirmDialog").dialog("close");
          }
        },
        autoOpen:false
    });
});
function showAlert(level, code, message) {
    if(level == 'danger') {
        $('#alertArea').html('<div class="alert alert-block alert-danger fade in">' +
        '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
        '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p>請確認後再操作一次</p>' +'</div>');
    } else if(level == 'success') {
        $('#alertArea').html('<div class="alert alert-block alert-success fade in">' +
                '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
                '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p></p>' +'</div>');
    } else if(level == 'info') {
        $('#alertArea').html('<div class="alert alert-block alert-info fade in">' +
                '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
                '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p></p>' +'</div>');
    } else if(level == 'warning') {
        $('#alertArea').html('<div class="alert alert-block alert-warning fade in">' +
                '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
                '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p>請確認後再操作一次</p>' +'</div>');
    }

}
function bindEvent() {
    $("#btnDelete").bind("click", ods352e_delete);
};
function init() {
    
    $("#list2").jqGrid({
        url:'rest/find_user_follow_pkg',
        datatype: "json",
        height: 250,
        width: 970,
        colNames:['編號','主題名稱', '更新日期', 'package_id'],
        colModel:[
                {name:'id',index:'id', width:55},
                {name:'name',index:'name', width:250},
                {name:'updated',index:'updated', width:250},
                {name:'pkgId',index:'pkgId', width:250, hidden:true }
        ],
        sortname:'id',
        sortorder:'asc',
        viewrecords:true,
        //rowNum:10,
        //rowList:[10,20,30],
        rowNum:10000000,
        rowList:[10000000],
        jsonReader:{
                repeatitems : false
        },
        pager:"#pagerDiv",
        multiselect: true,
        caption: "訂閱清單"
    }).navGrid('#pagerDiv',{edit:true,add:true,del:false});
    
    

};


function ods352e_delete() {
    var isDelete = true;
    var ids = $("#list2").jqGrid('getGridParam','selarrrow');

    if (ids.length > 0) {

        var selPkgIdList = "";

        for (var i=0; i < ids.length; i++) {
          var id = ids[i];
          var row = $("#list2").jqGrid('getRowData', id);
          selPkgIdList = selPkgIdList + row.pkgId + ',';
        }

        //alert(selNum);
    }
    else
    {
        showAlert('warning', '', '未選取任何訂閱主題，無法取消');
        isDelete = false;
    }
    
    
    if(isDelete)
    {
        $("#deleteConfirmDialog").dialog("open");
    }
};

function ods352e_delete_confirm() {
    var ids = $("#list2").jqGrid('getGridParam','selarrrow');
    
    var selPkgIdList = "";

    for (var i=0; i < ids.length; i++) {
      var id = ids[i];
      var row = $("#list2").jqGrid('getRowData', id);
      selPkgIdList = selPkgIdList + row.pkgId + ',';
    }
    
    formData = {
            selPkgIdList:selPkgIdList
        };
        
        //RESTFul uri對應參數
        var param = {};
        
        //RESTful查詢
        var url = '/ods-main/ODS352E/rest/delete';
        
        
        var promise = chtAjaxRest.find(url, formData, param);
        
        // success處理
        promise.done(function(data) {
            //alert(JSON.stringify(data));
            $('#list2').trigger( 'reloadGrid' );
            showAlert(data.alerts[0].type, '', data.alerts[0].message);        
        });

        // error處理
        promise.fail(function(xhrInstance, status, xhrException) {
            alert("fail:" + status + " Message:" + xhrException);
        });
        
}
