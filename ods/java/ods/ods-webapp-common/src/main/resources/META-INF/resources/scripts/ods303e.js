$(document).ready(function() {
    if (parseUrl().context == 'ods') {
        $('#doRating').attr('disabled', true);
        $('#doOrder').attr('disabled', true);
        $('#doPrint').attr('disabled', true);
        $('#doUserInfo').attr('disabled', true);        
    }
    
    if ($('#packageCookie').val()=="false") {
        bootstroStart();
    }
    
    $("#showPkgInfoApiDetail").click(function() {
        showPkgInfoApiDetail(); 
    });

});

function bootstroStart() {
    bootstro.start('.bootstro', {
        finishButton : '<button class="btn btn-mini btn-success bootstro-finish-btn"><i class="icon-ok" ></i>好的，我已經瞭解！</button>'
    });
}

function parseUrl() {
    var parts = document.URL.split('/');
    var len = parts.length;
    return {
      context: parts.slice(0)[3],
      endpoint: parts.slice(0,[len-4]).join('/') 
    };
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
    console.log("url="+url);
    
    var promise = chtAjaxRest.get(url, formData, param);
    
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

function showPkgInfoApiDetail() {
    window.open("/ods-main/ODS313E/detail/packageInfo/"+$("#packageId").val()+"/"+$("#packageVer").val(), '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');
}
