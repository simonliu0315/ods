function rmAttr(cVal){
    var $radios = $('input:radio[name=rating]');
    $radios.filter('[value="'+cVal+'"]').prop('checked', true);   
}
function getMeta() {
    var html = '';
    $("head meta").each(function () {
        html += $(this).clone().wrap('<div>').parent().html();
    });
    return html;
}

var meta = '';
var anonymousUser;
$(document).ready(function() {
    meta = getMeta();    
    if (parseUrl().context == 'ods-main') {
        findRating();
        isAnonymousUser();
        //$.unblockUI();
    }    
    
    $('#doRating').qtip({ // Grab some elements to apply the tooltip to
        content: {
            text: '我要評分，此功能需進行登入，若尚未登入將導向電子發票整合服務平台。由整合服務平台登入後，再進行評分！'
        }
    });
    
    $('#doOrder').qtip({ // Grab some elements to apply the tooltip to
        content: {
            text: '主題訂閱，此功能需進行登入，若尚未登入將導向電子發票整合服務平台。由整合服務平台登入後，再進行訂閱！'
        }
    });
    //$("#ratingUpdate").bind("click", createRating);  
    $("#doRating").click(function() {
        //alert("isAnonymousUser:"+anonymousUser);
        if (anonymousUser) {
            window.location.replace('/ods-main/login?redirectUrl=/ODS303E/'+$("#packageId").val()+'/'+$("#packageVer").val()+'/');
            //移除匿名評分避免功能惡用
            // bootbox.dialog({
            //     message : $("#dialog").html(),
            //     title : "請評分(匿名評分)",
            //     buttons : {
            //         success : {
            //             label : "給分!",
            //             className : "btn-danger' title='給分'",
            //             callback : function() {
            //                 createRating($("input[name=rating]:checked").val());
            //                 return;
            //             }
            //         }
            //     }
            // });            
        } else {
            bootbox.dialog({
                message : $("#dialog").html(),
                title : "請評分...",
                buttons : {
                    success : {
                        label : "給分!",
                        className : "btn-danger' title='給分'",
                        callback : function() {
                            createRating($("input[name=rating]:checked").val());
                            return;
                        }
                    }
                }
            });
        }
    });
    $("#doAnalysizing").click(function() {
        bootbox.dialog({
            message : $("#dialogAnalysize").html(),
            title : "請選擇TWBX檔",
            buttons : {
                success : {
                    label : "關閉",
                    className : "btn-danger' title='關閉'",
                    callback : function() {
                        return;
                    }
                }
            }
        });
    });
    $("#doPrint").click(function() {
       doPrint(); 
    });
    $("#doPackDatasets").click(function() {
        doPackDatasets(); 
     });
    
    $("#doUserInfo").click(function() {
        window.open("/" + parseUrl().context+ "/ODS309E/", "newwindow");
    });

});

function createRating(starVal) {  
    
    // 判斷是否未選擇評價星數
    if(starVal == undefined){
        bootbox.alert("請選擇評分");
        return;
    }
    
    var formData = {rate: starVal};

    // RESTFul uri對應參數
    var param = {
        //ajaxdata : $("#ajaxdata").val()
    };

    // RESTful查詢
    var url = 'rest/' + $("#packageId").val() + '/' + $("#packageVer").val();
   
    url = "/" + parseUrl().context+ "/ODS307E/" + url;
    var promise = chtAjaxRest.find(url, formData, param);

    // success處理
    promise.done(function(data) {
        //alert(JSON.stringify(data));
        showAlert(data.alerts[0].type, '', data.alerts[0].message);
        bootbox.alert(data.alerts[0].message);
        location.reload();       
    });

    // error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        bootbox.alert("fail:" + status + " Message:" + xhrException);
    });
}
function findRating() {
    var formData = {};

    // RESTFul uri對應參數
    var param = {
        //ajaxdata : $("#ajaxdata").val()
    };

    // RESTful查詢
    var url = 'rest/find/' + $("#packageId").val() + '/' + $("#packageVer").val();
   
    url = "/" + parseUrl().context + "/ODS307E/" + url;

    var promise = chtAjaxRest.find(url, formData, param);

    // success處理
    promise.done(function(data) {
        //alert(JSON.stringify(data));
        if (data.data == null) {
            return ;
        }
        var $radios = $('input:radio[name=rating]');
        if($radios.is(':checked') === false) {
            $radios.filter('[value="'+data.data.rate+'"]').attr('checked', true);
        }
    });

    // error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        bootbox.alert("fail:" + status + " Message:" + xhrException);
    });
}
function doPrint() {
    var url = '/ods-main/ODS303E/create_download_record/'+$("#packageId").val()+'/'+$("#packageVer").val();
    var promise = chtAjaxRest.find(url, {}, {});

    //success處理
    promise.done(function(data) {
    });
    //error處理
    promise.fail(function(xhrInstance, status, xhrException) {
        alert("fail:"+status+" Message:"+xhrException);
    });

    
    var formData = "?";
    formData += "packageId="+$("#packageId").val();
    formData += "&packageVer="+$("#packageVer").val();
    window.open("/" + parseUrl().context + "/ODS304E/downloadPdf" + formData, "newwindow");
//    uiCommonOpenWindowWithPostObj(
//            '/ods-main/ODS304E/downloadPdf',
//            '',
//            "height=500, width=800, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, " +
//            "resizable=yes,location=no, status=no",
//            formData);       
}

function doPackDatasets() {
    window.open("/" + parseUrl().context + "/ODS308E/download/" + $("#packageId").val() + "/" + $("#packageVer").val() + "/zip/?fileType=zip", "newwindow");
}

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
function parseUrl() {
    var parts = document.URL.split('/');
    var len = parts.length;
    return {
      context: parts.slice(0)[3],
      endpoint: parts.slice(0,[len-4]).join('/') 
    };
}
function uiCommonOpenWindowWithPostObj(url, name, specs, paramObject, forceUniqueWindowName) {
    var paramNames = [];
    var paramValues = [];
    if (paramObject) {
        for (var paramName in paramObject) {
            paramNames.push(paramName); 
            paramValues.push(paramObject[paramName]);
        }
    }
    return uiCommonOpenWindowWithPostParams(url, name, specs, paramNames, paramValues, forceUniqueWindowName);
}
var gUniqueWindowNo = 0;
function uiCommonOpenWindowWithPostParams(url, name, specs, paramNames, paramValues, forceUniqueWindowName) {
    if (name) {
        if (forceUniqueWindowName) {
            name = name + "_" + gUniqueWindowNo;
            gUniqueWindowNo++;
        }
    } else {
        name = "_blank";
    }
    var newWindow = window.open('about:blank', name, specs); 
    //var newWindow = window.open('about:blank', name, specs);
    if (!newWindow) return false;    
    var formHtml = "<!DOCTYPE html><html lang=\"zh-Hant-TW\" xmlns:th=\"http://www.thymeleaf.org\"><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8/'></meta>"+meta+"</head><body><form name='uiCommonPostParamForm' method='post' action='" + url + "' accept-charset='UTF-8'>";
    if (paramNames && paramValues && (paramNames.length == paramValues.length)) {        
        for (var i=0; i < paramNames.length; i++) formHtml += "<input type='hidden' name='" + paramNames[i] + "' value='" + paramValues[i] + "'/>";
        formHtml += "</form><!--script type='text/javascript'>document.forms['uiCommonPostParamForm'].submit();</script--></body></html>";
    }
    newWindow.document.write(formHtml);
    return newWindow;
}

function isAnonymousUser() {
    var formData = {};

    // RESTFul uri對應參數
    var param = {
        //ajaxdata : $("#ajaxdata").val()
    };

    // RESTful查詢
    var url = 'rest/chkAnonymousUser';
   
    url = "/" + parseUrl().context + "/ODS307E/" + url;

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
