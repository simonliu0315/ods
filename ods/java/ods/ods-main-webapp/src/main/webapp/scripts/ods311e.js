//文件載入完成後執行
var anonymousUser;
$(document).ready(function() {    
    isAnonymousUser();
    
    $("#doSub").click(function() {
        //alert("isAnonymousUser:"+anonymousUser);
        if (anonymousUser) {
            window.location.replace('/ods-main/login?redirectUrl=/ODS311E/'+$("#packageId").val()+'/'+$("#packageVer").val()+'/'+$("#packageType").val()+'/'+$("#packageCode").val()+'/');
        } else {
            createSub();
        }
    });
    
    //initPage();
});

function initPage() {
    //資料
    var formData = {
        packageId : $("#packageId").val(),
        packageVer : $("#packageVer").val()
    };
    
    //RESTFul uri對應參數
    var param = {};
    
    //RESTful查詢
    var url = '/ods-main/ODS311E/initPage';

    var promise = chtAjaxRest.find(url, formData, param);
    
    //success處理
    promise.done(function(data) {
        var odsPackageVersion = data.odsPackageVersion;
        $("#packageName").html(odsPackageVersion.name);
        $("#packageDescription").html(odsPackageVersion.description);
    });
    
    //error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        alert("fail:"+status+" Message:"+xhrException);
    });
}

function createSub() {
    // RESTFul uri對應參數
    var formData = {};
    var param = {
        //ajaxdata : $("#ajaxdata").val()
    };

    // RESTful查詢
    var url = 'createSub/' + $("#packageId").val();
   
    url = "/" + parseUrl().context+ "/ODS311E/" + url;
    var promise = chtAjaxRest.find(url, formData, param);

    // success處理
    promise.done(function(data) {
        bootbox.alert(data.alerts[0].message);
        location.reload();       
    });

    // error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        bootbox.alert("fail:" + status + " Message:" + xhrException);
    });
}

function isAnonymousUser() {
    var formData = {};

    // RESTFul uri對應參數
    var param = {
        //ajaxdata : $("#ajaxdata").val()
    };

    // RESTful查詢
    var url = 'chkAnonymousUser';
   
    url = "/" + parseUrl().context + "/ODS311E/" + url;

    var promise = chtAjaxRest.find(url, formData, param);

    // success處理
    promise.done(function(data) {
        //alert(JSON.stringify(data));
        if (data.data == null) {
            return ;
        }
        anonymousUser = data.data;
        --sls.global.blockUi.numBlockUi;
        //alert(anonymousUser);
    });

    // error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        bootbox.alert("fail:" + status + " Message:" + xhrException);
    });
}

function shareSocial(type){
    //資料
    /*var formData = {
        packageId:$("#packageId").val(),
        packageVer:$("#packageVer").val()
    };*/
    var formData = {};
    
    //RESTFul uri對應參數
    var param = {};

    //var url = '/ods-main/ODS303E/create_share_record';
    var url = '/ods-main/ODS303E/create_share_record/'+$("#packageId").val()+'/'+$("#packageVer").val()+'/'+type;

    var promise = chtAjaxRest.find(url, formData, param);
    
    //success處理
    promise.done(function(data) {
    });
    
    //error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        alert("fail:"+status+" Message:"+xhrException);
    });
    
    var url = ''; 
    if (type == 'facebook') {
        url = 'https://www.facebook.com/sharer.php?u='+document.URL;
    } else if (type == 'google') {
        url = 'https://plus.google.com/share?url='+document.URL;
    } else if (type == 'twitter') {
        url = 'https://twitter.com/share?url='+document.URL;
    }
    window.open(url);
}

