//文件載入完成後執行
$(document).ready(function() {
    initPage();
});

/** 處理標籤、檔案格式、分眾的選取 切換css及set Value
 */
function selctedObject(id, element){
    var obj = $(element); 
    obj.toggleClass("active");
    var objsArr = [];
    if(obj.hasClass( "active" )){
        obj.html(obj.text()+'<span class="glyphicon glyphicon-remove" style="float:right"></span>');
    }else {
        obj.html(obj.text());
    }
    $("#"+id+" .active").each(function(index){
        objsArr.push($(this).text());
    });
    $("#"+id).val(objsArr);
    
    //重新載入odsGroup
    odsGroupQuery();
}

/** 清除全選標籤、檔案格式、分眾的選取 切換css及set Value
 */
function clearSelectedObject(id){
    $("#"+ id).val([]);
    $("#"+id+" .active").each(function(index){
        $(this).html($(this).text());
        $(this).removeClass("active");
    });

    //重新載入odsGroup
    odsGroupQuery();
}

/**
 * 清除查詢主題名稱
 */
function clearGroupName(){
    $("#groupName").val("");
    //重新載入odsGroup
    odsGroupQuery();
}

/**
 * open close 標籤div
 */
function showMore(id){
    var obj = $("#"+id); 
    if(obj.hasClass( "lessContent" )){
        obj.removeClass('lessContent');
    } else {
        obj.addClass('lessContent');
    }
}

function fieldCheck(){
    var isPass = true;
//    var ajaxdata = $("#ajaxdata").val();
//    if(ajaxdata==null ||ajaxdata==undefined ||ajaxdata==""){
//        isPass = false;
//        alert("請輸入查詢值。");
//    }
    return isPass;
}

/**
 * 針對關鍵字、分類標籤、檔案格式、分眾推廣群進行主題群組的搜尋
 */
function odsGroupQuery() {
    //資料
    for (var i = 0; i < $("#fileExts").val().length; i++) {
        if ($("#fileExts").val()[i] == "圖片") {
            $("#fileExts").val()[i] = "image";
        }
        if ($("#fileExts").val()[i] == "PDF檔") {
            $("#fileExts").val()[i] = "pdf";
        }
        if ($("#fileExts").val()[i] == "資料集") {
            $("#fileExts").val()[i] = "dataset";
        }
    }
    var formData = {
        groupName:$("#groupName").val(),
        orderByType:$("#orderByType").val(),
        selectedOdsIdentity:$("#identitys").val(),
        selectedOdsPackageTag:$("#tags").val(),
        selectedOdsResourceFileExt:$("#fileExts").val()
    };
    
    //RESTFul uri對應參數
    var param = {};
    
    //RESTful查詢
    var url = '/ods-main/ODS301E/query';
    
    if (fieldCheck()) {
        var promise = chtAjaxRest.find(url, formData, param);
        
        //success處理
        promise.done(function(data) {
            //主題群組載入
            genOdsGroups(data.odsGroups);
        });
        
        //error處理
        promise.fail(function(xhrInstance, status, xhrException) {
            alert("fail:"+status+" Message:"+xhrException);
        });
    }
}

/**
 * 頁面載入，取得全部主題群組、分類標籤、檔案格式、分眾推廣群
 */
function initPage() {
    //資料
    var formData = {
        orderByType:$("#orderByType").val()
    };
    
    //RESTFul uri對應參數
    var param = {};
    
    //RESTful查詢
    var url = '/ods-main/ODS301E/initPage';
    
    if (fieldCheck()) {
        var promise = chtAjaxRest.find(url, formData, param);
        
        //success處理
        promise.done(function(data) {
            var odsGroups = data.odsGroups;
            var odsIdentity = data.odsIdentity;
            var odsPackageTag = data.odsPackageTag;
            var odsResourceFileExt = data.odsResourceFileExt;
            $("#tags").val([]);
            $("#identitys").val([]);
            $("#fileExts").val([]);
            updatePage(odsGroups, odsIdentity, odsPackageTag, odsResourceFileExt);
        });
        
        //error處理
        promise.fail(function(xhrInstance, status, xhrException) {
            alert("fail:"+status+" Message:"+xhrException);
        });
    }
}

/**使用主題群組、分類標籤、檔案格式、分眾推廣群 填入畫面欄位
 * @param odsGroups
 * @param odsIdentity
 * @param odsPackageTag
 * @param odsResourceFileExt
 */
function updatePage(odsGroups, odsIdentity, odsPackageTag, odsResourceFileExt){
    //主題群組載入
    genOdsGroups(odsGroups);
    
    //分眾檔載入
    genHref("identitys", odsIdentity);
    
    //tags載入
    genHref("tags", odsPackageTag);
    
    //檔案格式載入
    genHref("fileExts", odsResourceFileExt);
}


/**
 * 做出標籤選項
 * <a href="#$1" class="list-group-item" onclick="selctedObject(\'$2\',this)">$3</a>
 * 
 * @param id
 * @param objArr
 */
function genHref(id, objArr){
    var href1 = '<a href="javascript:void(0);" class="list-group-item" onclick="selctedObject(\'';
    var href2 = '\',this)" ';
    var href2_2 = '>';
    var href3 = '</a>';
    $("#"+id).html("");
    var htmlstr = "";
    for ( var idx=0;idx<objArr.length;idx++) {
        if (objArr[idx] == "dataset" || objArr[idx] == "pdf" || objArr[idx] == "image") {
            var chtName = "";
            if (objArr[idx] == "dataset") {
                chtName = "資料集";
            } else if (objArr[idx] == "pdf") {
                chtName = "PDF檔";
            } else if (objArr[idx] == "image") {
                chtName = "圖片";
            }
            htmlstr = htmlstr + href1 + id + href2 + 'title="加入' + chtName + '條件" ' + href2_2 + chtName + href3;
        } else {
            htmlstr = htmlstr + href1 + id + href2 + 'title="加入' + objArr[idx] + '條件" ' + href2_2 + objArr[idx] + href3;
        }
    }
    $("#"+id).html(htmlstr);
}

/**做出主題群組
 * @param odsGroups
 */
function genOdsGroups(odsGroups) {
    var groupDiv1 = '<div class="col-6 col-sm-6 col-lg-4">\
        <div class="panel panel-default col-lg-height-3 col-sm-height-6" style="overflow:hidden;">\
        <div class="panel-body">';
    var groupDiv1_2 = '<div><h5>\
        <a href="/ods-main/ODS301E/rest/';
    var groupDiv1_3 = '<div style="float:left;margin-right:5px">\
        <img class="img-thumbnail" src="/ods-main/ODS308E/public/group/';
    // group id
    var groupDiv7 = '/image/';
    // $3
    var groupDiv2 = ' style="width:100%;"></div>';
    // $4
    var groupDiv3 = ' >';
    // $1
    var groupDiv4 = '</a></h5>\
        <p>';
    // $2
    var groupDiv5 = '</p>\
        <p align="right" style="position:absolute;bottom:20px;right:30px;">\
        <a class="btn btn-default" href="/ods-main/ODS301E/rest/';
    // $5
    var groupDiv6 = ' >詳細資訊</a></p>\
        </div>\
        </div>\
        </div>\
        </div>';
    // 主題群組載入
    var htmlstr = "";
    $("#odsGroups").html("");
    for ( var idx=0;idx<odsGroups.length;idx++) {
        htmlstr += groupDiv1
                + groupDiv1_2 + odsGroups[idx].id
                + '/" title="連結到 '+ odsGroups[idx].name +'" '
                + groupDiv3 
                + groupDiv1_3 + odsGroups[idx].id
                + groupDiv7 + odsGroups[idx].imageUrl + '"'
                + ' alt="連結到 '+ odsGroups[idx].name +'" title="連結到 '+ odsGroups[idx].name +'" '
                + groupDiv2 + odsGroups[idx].name
                + groupDiv4 + odsGroups[idx].description
                + groupDiv5 + odsGroups[idx].id 
                + '/" title="連結到 '+ odsGroups[idx].name +'" '
                + groupDiv6;
    }
    $("#odsGroups").html(htmlstr);
}
