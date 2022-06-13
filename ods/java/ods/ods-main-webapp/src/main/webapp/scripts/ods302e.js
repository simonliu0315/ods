//文件載入完成後執行
$(document).ready(function() {
    initPage();
    
    $("#showPkgApiDetail").click(function() {
        showPkgApiDetail(); 
    });
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
    
    //重新載入odsPackage
    odsPackageQuery();
}

/** 清除全選標籤、檔案格式、分眾的選取 切換css及set Value
 */
function clearSelectedObject(id){
    $("#"+ id).val([]);
    $("#"+id+" .active").each(function(index){
        $(this).html($(this).text());
        $(this).removeClass("active");
    });

    //重新載入odsPackage
    odsPackageQuery();
}

/**
 * 清除查詢主題名稱
 */
function clearPackageName(){
    $("#packageName").val("");
    //重新載入odsPackage
    odsPackageQuery();
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
function odsPackageQuery() {
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
        packageName:$("#packageName").val(),
        orderByType:$("#orderByType").val(),
        selectedOdsPackageTag:$("#tags").val(),
        selectedOdsResourceFileExt:$("#fileExts").val()
    };
    
    //RESTFul uri對應參數
    var param = {};
    
    //RESTful查詢
    var url = '/ods-main/ODS302E/query';
    
    if (fieldCheck()) {
        var promise = chtAjaxRest.find(url, formData, param);
        
        //success處理
        promise.done(function(data) {
            //主題群組載入
            genOdsPackages(data.odsPackages);
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
    var url = '/ods-main/ODS302E/initPage';
    
    if (fieldCheck()) {
        var promise = chtAjaxRest.find(url, formData, param);
        
        //success處理
        promise.done(function(data) {
            var odsPackages = data.odsPackages;
            var odsPackageTag = data.odsPackageTag;
            var odsResourceFileExt = data.odsResourceFileExt;
            $("#tags").val([]);
            $("#fileExts").val([]);
            updatePage(odsPackages, odsPackageTag, odsResourceFileExt);
        });
        
        //error處理
        promise.fail(function(xhrInstance, status, xhrException) {
            alert("fail:"+status+" Message:"+xhrException);
        });
    }
}

/**使用主題群組、分類標籤、檔案格式、分眾推廣群 填入畫面欄位
 * @param odsPackages
 * @param odsPackageTag
 * @param odsResourceFileExt
 */
function updatePage(odsPackages, odsPackageTag, odsResourceFileExt){
    //主題群組載入
    genOdsPackages(odsPackages);
    
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
 * @param odsPackages
 */
function genOdsPackages(odsPackages) {
    var packageDiv1 = '<div class="col-6 col-sm-6 col-lg-4" >\
    <div class="panel panel-default col-lg-height-3 col-sm-height-6" style="overflow:hidden;">\
      <div class="panel-body">';
    var packageDiv1_2 = '<h5>\
          <a href="/ods-main/ODS303E/';
    var packageDiv1_3 = '<div style="">\
              <img class="img-thumbnail" src="/ods-main/ODS308E/public/package/';
    //${odsPackage.id}
    var packageDiv7 = '/image/';
          //${odsPackage.imageUrl}
    var packageDiv2 = ' style="width:100%;">\
        </div>';
          //+${odsPackage.id}+'/' + ${odsPackage.latestVer}
    var packageDiv3 = ' >';
          //${odsPackage.name}
    var packageDiv4 = '</a>\
        </h5><p>';
          //${odsPackage.description}
    var packageDiv5 = '</p>\
        <p align="right" style="position:absolute;bottom:20px;right:30px;">\
          <a class="btn btn-default" href="';
          //+${odsPackage.id}+ '/' + ${odsPackage.latestVer}
    var packageDiv6 = '>詳細資訊</a>\
            </p>\
          </div>\
        </div>\
      </div>';
    
    
    // 主題載入
    var htmlstr = "";
    $("#odsPackages").html("");
    for ( var idx=0;idx<odsPackages.length;idx++) {
        if (odsPackages[idx].code == null || odsPackages[idx].code =="") {
            packageDiv1_2 = '<h5>\
                <a href="/ods-main/ODS303E/' + odsPackages[idx].id + '/' + odsPackages[idx].latestVer + '/' + odsPackages[idx].breadLink;
            packageDiv5_1 = '/ods-main/ODS303E/' + odsPackages[idx].id + '/' + odsPackages[idx].latestVer + '/' + odsPackages[idx].breadLink;
        } else {
            packageDiv1_2 = '<h5>\
                <a href="/ods-main/ODS311E/' + odsPackages[idx].id + '/' + odsPackages[idx].latestVer + '/' + odsPackages[idx].type + '/' + odsPackages[idx].code;
            packageDiv5_1 = '/ods-main/ODS311E/' + odsPackages[idx].id + '/' + odsPackages[idx].latestVer + '/' + odsPackages[idx].type + '/' + odsPackages[idx].code;
        }
        htmlstr = htmlstr 
                + packageDiv1
                + packageDiv1_2 
                + '/" title="連結到 '+ odsPackages[idx].name +'" '
                + packageDiv3 
                + packageDiv1_3 + odsPackages[idx].id
                + packageDiv7 + odsPackages[idx].imageUrl + '"' 
                + ' alt="連結到 '+ odsPackages[idx].name +'" title="連結到 '+ odsPackages[idx].name +'" '
                + packageDiv2 + odsPackages[idx].name
                + packageDiv4 + odsPackages[idx].description
                + packageDiv5 + packageDiv5_1 
                + '/" title="連結到 '+ odsPackages[idx].name +'" ' + packageDiv6;
    }
    $("#odsPackages").html(htmlstr);
}


function showPkgApiDetail() {
    window.open("/ods-main/ODS312E/detail/packages", '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');
}
