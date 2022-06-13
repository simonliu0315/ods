//文件載入完成後執行
$(document).ready(function() {
    // 事件綁定
    bindEvent();
    init();    
});
function showAlert(level, code, message) {
    if(level == 'danger') {
        $('#alertArea').html('<div class="alert alert-block alert-danger fade in">' +
        '<button title="關閉" type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
        '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p>請確認後再操作一次</p>' +'</div>');
    } else if(level == 'success') {
        $('#alertArea').html('<div class="alert alert-block alert-success fade in">' +
                '<button title="關閉" type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
                '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p></p>' +'</div>');
    } else if(level == 'info') {
        $('#alertArea').html('<div class="alert alert-block alert-info fade in">' +
                '<button title="關閉" type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
                '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p></p>' +'</div>');
    } else if(level == 'warning') {
        $('#alertArea').html('<div class="alert alert-block alert-warning fade in">' +
                '<button title="關閉" type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>' +
                '<h4><strong><em>'+code+'</em></strong><strong>'+message+'</strong></h4>' +' <p>請確認後再操作一次</p>' +'</div>');
    }
}
function bindEvent() {
    $("#btnUpdate").bind("click", ods305e_update);
    $("#btnRefresh").bind("click", ods305e_refresh);
};
function init() {
    var formData = {
        packageId : $("#packageId").val(),
        packageVer : $("#packageVer").val()
    };
    var param = {};
    var url = 'findCheckBox';
    url = "/ods-main/ODS305E/rest/" + url;
    var promise = chtAjaxRest.find(url, formData, param);

    // success處理
    promise.done(function(data) {        
        $('input[type=checkbox]').each(function() {
            $(this).prop('checked', false);
        });        
        // console.log(JSON.stringify(data));
        for (var i = 0; i < data.data.length; i++) {
            $('input[type=checkbox]').each(
                    function(key, value) {                        
                        if ($(this).val().toUpperCase() == (data.data[i].packageId + ' '
                                + data.data[i].resourceId + ' ' + data.data[i].criteriaId)) {                            
                            $(this).prop('checked', true);
                            //$(this).attr('checked', true);
                        }
                        if ($(this).val() == 'V' && data.data[i].resourceId == null
                                && data.data[i].criteriaId == null) {                            
                            $(this).removeAttr('checked');
                            $(this).prop('checked', true);
                        }
                    });
        }
        //showAlert('success', '', '讀取成功');
    });
    // error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        alert("fail:" + status + " Message:" + xhrException);
    });
};
function ods305e_refresh() {    
    init();
};
function ods305e_update() {
    if (isValidateError()) {
        //alert('選取[更新即通知]不可選取其他相關資料');
        showAlert('danger', '', '選取[更新即通知]不可選取其他相關資料');
        return;
    } else if ($('input[type=checkbox]:checked').length == 0) {
        //alert('請選擇勾選一筆資料');
        showAlert('danger', '', '請選擇勾選一筆資料');
        return;
    }
    // alert(JSON.stringify(form2object('ods305eForm')));
    // 資料
    var formData = form2object('ods305eForm');

    // RESTFul uri對應參數
    var param = {
        ajaxdata : $("#ajaxdata").val()
    };

    // RESTful查詢
    var url = 'rest/:ajaxdata';

    // 這邊加上這個url處理是為了使用絕對路徑 /xyz/XXXX/rest/:ajaxdata
    // 一般狀況不用特別加上此段轉換，可以直接使用rest/:ajaxdata
    url = "/ods-main/ODS305E/" + url;

    // if (fieldCheck()) {
    var promise = chtAjaxRest.find(url, formData, param);

    // success處理
    promise.done(function(data) {
        //alert(JSON.stringify(data));
        showAlert(data.alerts[0].type, '', data.alerts[0].message);        
    });

    // error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        alert("fail:" + status + " Message:" + xhrException);
    });
    // }
};
function isValidateError() {
    var isError = false;
    $('input[type=checkbox]:checked').each(function() {
        if ($(this).val() == 'V') {
            if ($('input[type=checkbox]:checked').length == 1) {
                isError = false;
                return;
            } else {
                isError = true;
                return;
            }
        }
    });
    return isError;
};
