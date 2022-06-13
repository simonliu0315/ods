    var dimensionTypes = [{'dimensionType': 1, 'name' : '時間別'}];        
    var dimensionColumns = [
                        {'name':'年', 'parent' : null, 'dimensionType':'1'}
                        ,{'name':'年月', 'parent' : '年', 'dimensionType':'1'}
                        ,{'name':'日期', 'parent' :'年月', 'dimensionType':'1'}
                        ];
                            
    var currentDimensionStatus = [{'name':'年', 'depth':'0', 'dimensionType':'1'}];   
    
    var drillDownChain1 = [];
    var drillDownChain2 = [];
    var drillDownOptionsChain = [];
    var dataset1;
    var dataset2;
    var anonymousUser;

    //bootstro 
    function bootstroStart() {
        bootstro.start('.bootstro', {
            finishButton : '<button class="btn btn-mini btn-success bootstro-finish-btn"><i class="icon-ok" ></i>好的，我已經瞭解！</button>'
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
    
  $(function() {
      //訂閱、登入
      $("#doSub").click(function() {
          createSub();
      });
      var role = $("#role").val();
      var isSub = $("#isSub").val();
      var isAnonymousUser = $("#isAnonymousUser").val();
      
      if("true"==isSub && "RB"==role){
          if (parseUrl().context == 'ods-main') {
              init();
          }

          if ($('#packageCookie').val()=="false") {
              bootstroStart();
          }
          initRangeDatepicker("input[name='sDate']", "input[name='eDate']");
      }
      
      $("#queryBtn").click(function() {
          if (chkColumns()) {
              getPlotDataSet();
          }
      });

      $("#downloadBtn").click(function() {
          downloadDataset();
      });
      
      // plothover
        var previousPoint = null;
        var previousIdx = null;
        $("#placeholder").bind("plothover", function (event, pos, item) {
            if (item) {
                fun = "總數";

                var ycolumn = $("#ycolumn").val();
                var unitName = "(元)";// 單位
                var hoverFormat = numFormat;
                if(ycolumn.indexOf("占比", 0)>0){
                    unitName = "(%)";
                    hoverFormat = percFormat;
                }else if(ycolumn=="電子發票張數"||ycolumn=="電子發票中獎張數"||ycolumn=="營業稅發票申購張數"){
                    unitName = "(張數)";
                }
                var nodeName = item.series.label;
                var idx = item.seriesIndex;
                var dimensionName = dimensionColumns[drillDownOptionsChain.length-1].name;
                var isLastDimension = dimensionColumns.length==drillDownOptionsChain.length;
                var avgName = isLastDimension?"":"平均";
                if (previousPoint != item.dataIndex || previousIdx!=idx) {
                    previousPoint = item.dataIndex;
                    previousIdx = idx;
                    
                    $("#tooltip").remove();
                    $("#tooltipPie").remove();
                    
                    /*
                    //計算營業人家數
                    var data1 =[];
                    if(0==idx){
                        data1 = drillDownChain1[drillDownChain1.length - 1]['data'][previousPoint]['data'];
                    }else {
                        data1 = drillDownChain2[drillDownChain2.length - 1]['data'][previousPoint]['data'];
                    }
                    var banCnt = avgBanCount(item, data1, '營業人家數');
                    
                    var banHtml = "";
                    if(!isNaN(banCnt)){
                        banHtml = banHtml + "<div>"+avgName + nodeName + "家數 : " + numFormat(banCnt) +"</div>";
                    }
                    if(ycolumn.indexOf("占比", 0)>0){
                        var areaBanCnt = avgBanCount(item, data1, '地區營業人家數');
                        if(!isNaN(areaBanCnt)){
                            banHtml = banHtml + "<div>"+avgName+"地區營業人家數 : " + numFormat(areaBanCnt) +"</div>";
                        }
                    }
                    */
                    
                    var x = item.datapoint[0];
                    var y = item.datapoint[1];
                    if ( "電子發票金額"==$("#ycolumn").val() ) {
                        ycolumn = "電子發票" + $("#aggreFun option:selected").text() + "金額";
                    }
                    if ( "電子發票張數"==$("#ycolumn").val() ) {
                        ycolumn = "電子發票" + $("#aggreFun option:selected").text() + "張數";
                    }
                    if ( "電子發票客單價"==$("#ycolumn").val() ) {
                        ycolumn = "電子發票" + $("#aggreFun option:selected").text() + "客單價";
                    }
                    if ( ! (("營所稅營業淨利"==$("#ycolumn").val() || "營所稅營業收入淨額"==$("#ycolumn").val()) && item.series.label=="營業人") ) {
                        ycolumn = "平均" + ycolumn;
                    }
                    if ($("#chartType").val() == "columns") {
                        //橫條圖
                        var label = item.series.data[y][1];
                        var tipHtml = "<div>" + item.series.label + ":" + label + dimensionName + "</div><div>" + ycolumn + " : " + hoverFormat(x) + unitName + "</div>";
                        showTooltip(item.pageX, item.pageY, "<strong><div style='color:#0000FF'>" + tipHtml + "</div></strong>");
                    } else if ($("#chartType").val() != "pie") {
                        var label = item.series.data[x][0];
                        var tipHtml = "<div>" + item.series.label + ":" + label + dimensionName + "</div><div>" + ycolumn +  " : " + hoverFormat(y) + unitName +"</div>";
                        showTooltip(item.pageX, item.pageY - 100, "<strong><div style='color:#0000FF'>" + tipHtml + "</div></strong>");
                    };
                    
                    if ($("#chartType").val() == "pie") {
                        $("#tooltip").remove();
                        $("#tooltipPie").remove();
                        /*
                        var data1 =[];
                        if(0==idx){
                            data1 = drillDownChain1[drillDownChain1.length - 1]['data'][idx]['data'];
                        }
                        //計算營業人家數
                        var banCnt = avgBanCount(item, data1, '營業人家數');
                        var banHtml = "";
                        if(!isNaN(banCnt)){
                            banHtml = banHtml + "<div>"+avgName+ "營業人家數 : " + numFormat(banCnt) +"</div>";
                        }
                        
                        if(ycolumn.indexOf("占比", 0)>0){
                            var areaBanCnt = avgBanCount(item, data1, '地區營業人家數');
                            if(!isNaN(areaBanCnt)){
                                banHtml = banHtml + "<div>"+avgName+"地區營業人家數 : " + numFormat(areaBanCnt) +"</div>";
                            }
                        }
                        */
                        
                        var label = drillDownChain1[drillDownChain1.length - 1]['dimension'];
                        var tipHtml = label + " : " + item.series.label + dimensionName + " <br>" + ycolumn + " : " + hoverFormat(item.series.data[0][1]) + unitName;
                        showTooltipPie(tipHtml);
                    };
                };
            } else {
                $("#tooltip").remove();
                $("#tooltipPie").remove();
                previousPoint = null;
                previousIdx = null;
            };
        });
        
      $("#placeholder").bind("plotclick", function (event, pos, item) {
          if (item) {
              //console.log(item.seriesIndex);
              if ($("#chartType").val() == "pie") {
                  drillDown(item.seriesIndex);
              } else {
                  drillDown(item.dataIndex);
              }
          }
      });
      
      $("#doRating").click(function() {
          if (anonymousUser) {
              window.location.replace('/ods-main/login?redirectUrl=/ODS311E/'+$("#packageId").val()+'/'+$("#packageVer").val()+'/'+$("#packageType").val()+'/'+$("#packageCode").val()+'/');
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
      $("#chartType").change(function() { 
          if($("#chartType").val() == "basic-table"){
              $("#ycolumnDiv").hide();
              $("#aggreFunDiv").hide();
              $("#targetDiv").hide();
          }else{
              //只有電子發票金額、張數 有分 B2B,B2C 
              var ycolumn = $("#ycolumn").val();
              $("#aggreFunDiv").hide();
              if("電子發票金額"==ycolumn || "電子發票張數"==ycolumn || "電子發票金額占比"==ycolumn || "電子發票張數占比"==ycolumn || "電子發票客單價"==ycolumn){
                  $("#aggreFunDiv").show();
              }
              $("#ycolumnDiv").show();
              $("#targetDiv").show();
          }
      });
      $("#ycolumn").change(function() { 
          //只有電子發票金額、張數 有分 B2B,B2C 
          var ycolumn = $("#ycolumn").val();
          $("#aggreFunDiv").hide();     
          dimensionColumns = [
                                  {'name':'年', 'parent' : null, 'dimensionType':'1'}
                                  ,{'name':'年月', 'parent' : '年', 'dimensionType':'1'}
                                  ,{'name':'日期', 'parent' :'年月', 'dimensionType':'1'}
                                  ];
                                  
          if("電子發票金額"==ycolumn || "電子發票張數"==ycolumn || "電子發票金額占比"==ycolumn || "電子發票張數占比"==ycolumn || "電子發票客單價"==ycolumn){
              $("#aggreFunDiv").show();
          }
          if("營所稅營業淨利"==ycolumn || "營所稅營業收入淨額"==ycolumn ||"營所稅營業淨利占比"==ycolumn || "營所稅營業收入淨額占比"==ycolumn ){
              //營所稅 資料單位為年
              dimensionColumns = [
                                  {'name':'年', 'parent' : null, 'dimensionType':'1'}
                                  ];
          }
          if("營業稅發票申購張數"==ycolumn || "營業稅銷項總計金額"==ycolumn ||"營業稅發票申購張數占比"==ycolumn || "營業稅銷項總計金額占比"==ycolumn ){
              //營業稅 資料單位為年月(每期)
              dimensionColumns = [
                                  {'name':'年', 'parent' : null, 'dimensionType':'1'}
                                  ,{'name':'年月', 'parent' : '年', 'dimensionType':'1'}
                                  ];
          }
          
      });
      $("#hsnCd").change(function() { 
          findTownCd();
      });
      $("#selfCounty").change(function() { 
          findSelfArea();
      });
      $("#selfArea").change(function() { 
          findSelfIndustClass();
      });
  });
  
  //計算座標點營業人/地區營業人家數
  var avgBanCount = function(item, data1, columnName){
      var banCnt = 0;
      var avgCnt = 0;
      for(var i = 0; i < data1.length; i++){
          if(undefined!=data1[i][columnName]){
              banCnt = banCnt + data1[i][columnName];
              if(0<data1[i][columnName]){
                  avgCnt++;
              }
          }
      }
      if(0==banCnt||0==avgCnt){
          return 0;
      }
      return banCnt/avgCnt;
  };
  
  var chkColumns = function() {
      var msg = "";
      
      if ($("#chartType").val() == "") {
          msg = msg + "圖表類型 ";
      }
      if ($("#sDate").val() == "") {
          msg = msg + "發票日期(起) ";
      }
      if ($("#eDate").val() == "") {
          msg = msg + "發票日期(訖) ";
      }
      if ($("#ycolumn").val() == "") {
          msg = msg + "統計欄位 ";
      }
      if (msg != "") {
          msg = msg + "未選擇，請選擇後查詢，謝謝！";
          bootbox.alert(msg);          
          return false;
      } else {
          return true;
      }      
      
  };
  
  var processDraw = function() {
      var dimension = 1;//固定為1  發票日期
      var selectDimension = null;
      for(var i = 0; i < currentDimensionStatus.length; i++){
          if(dimension == currentDimensionStatus[i]['dimensionType']){
              selectDimension = currentDimensionStatus[i];
          }
      }

      var groupData1 = groupDataSet(dataset1, selectDimension['name']);
      var groupData2 = null;
      groupData1 = transform(groupData1);
      var aggreFun = null;
      var ycolumn = $("#ycolumn").val();

      // console.log(chartType);
      if(ycolumn.indexOf("占比", 0)>0){
        aggreFun = percentGroup;
      } else if(ycolumn == "電子發票客單價"){
        aggreFun = cptGroup;
      } else {
        aggreFun = sumGroup;
      }
      groupData1 = caculateGroup(groupData1, ycolumn, aggreFun);
      groupData1 = _.sortBy(groupData1, 'dimension');
      drillDownChain1= [];
      drillDownChain2 = [];
      drillDownOptionsChain = [];
      buildDrillDownChain(drillDownChain1, selectDimension['name'], selectDimension['name'], groupData1, aggreFun);
      if(dataset2!=null){
          groupData2 = groupDataSet(dataset2, selectDimension['name']);
          groupData2 = transform(groupData2);
          groupData2 = caculateGroup(groupData2, ycolumn, aggreFun);
          groupData2 = _.sortBy(groupData2, 'dimension');
          buildDrillDownChain(drillDownChain2, selectDimension['name'], selectDimension['name'], groupData2, aggreFun);
      }
      drawGraph(groupData1, groupData2);
      buildDrillDownOption(selectDimension['name']);
  };
  
  var getPlotDataSet = function() {
      var yctype = $("#ycolumn").val();
      if("電子發票中獎張數"==yctype || "電子發票中獎金額"==yctype){
          yctype = 1;
      }else if("營所稅營業淨利"==yctype || "營所稅營業收入淨額"==yctype){
          yctype = 5;
      }else if("營業稅發票申購張數"==yctype || "營業稅銷項總計金額"==yctype){
          yctype = 6;
      }else {
          //電子發票金額張數、客單價
          yctype = 7;
      }
      //資料
      var formData = {
          packageId : $("#packageId").val(),
          packageVer : $("#packageVer").val(),
          chartType : $("#chartType").val(),
          invoiceSDate : $("#sDate").val(),
          invoiceEDate : $("#eDate").val(),
          //所屬縣市鄉鎮行業
          selfCounty : $("#selfCounty").val(),
          selfArea : $("#selfArea").val(),
          selfIndustClass:$("#selfIndustClass").val(),
          ycolumn :yctype,
          aggreFun : $("#aggreFun").val(),
          //目標縣市鄉鎮行業
          hsnCd :$("#hsnCd").val(),
          townCd :$("#townCd").val(),
          industry : $("#industClass").val()
      };
      
      //RESTFul uri對應參數
      var param = {};
      
      //RESTful查詢
      if($("#chartType").val()=='basic-table'){
          //基本資料
          var url1 = '/ods-main/ODS315E/plot';
          var promise = chtAjaxRest.find(url1, formData, param);
          //success處理
          promise.done(function(data) {
              if(data && data.data && data.data.length > 0&& data.data[0].length > 0){
                  var basicInfo = data.data[0][0];
                  //營業人統編
                  $("#headBan").html(basicInfo.營業人統編);
                  //資本額
                  $("#capital").html(numberWithCommas(basicInfo.資本額));
                  
                  //總機構縣市、鄉鎮市區在init去查詢填入
                  //縣市
                  //$("#headBanHsnCd").html("FIX ME");
                  //鄉鎮市區
                  //$("#headBanTownCd").html("FIX ME");
                  //營業項目 (2碼)
                  //$("#headBanBscd2").html("FIX ME");
                   
                  //所屬縣市
                  var basicHsnCd=$("#selfCounty :selected ").text();
                  $("#basicHsnCd").html(basicHsnCd.replace('.','-'));
                  //所屬鄉鎮市區
                  var basicTownCd=$("#selfArea :selected ").text();
                  $("#basicTownCd").html(basicTownCd.replace('.','-'));
                  //所屬營業項目 (2碼)
                  var basicBscd2=$("#selfIndustClass :selected ").text();
                  $("#basicBscd2").html(basicBscd2.replace('.','-'));
                   
                  //所屬平均電子發票B2C金額
                  $("#avgB2CInvAmount").html(basicInfo.電子發票B2C金額 == undefined? "查無資料":numberWithCommas(basicInfo.電子發票B2C金額));
                  //所屬平均電子發票B2B金額
                  $("#avgB2BInvAmount").html(basicInfo.電子發票B2B金額 == undefined? "查無資料":numberWithCommas(basicInfo.電子發票B2B金額));
                  //所屬平均電子發票B2C張數
                  $("#avgB2CInvCount").html(basicInfo.電子發票B2C張數 == undefined? "查無資料":numberWithCommas(basicInfo.電子發票B2C張數));
                  //所屬平均電子發票B2B張數
                  $("#avgB2BInvCount").html(basicInfo.電子發票B2B張數 == undefined? "查無資料":numberWithCommas(basicInfo.電子發票B2B張數));
                  //所屬平均電子發票B2C客單價
                  $("#avgB2CCusAmount").html(basicInfo.電子發票B2C客單價 == undefined  ? "查無資料":numberWithCommas(basicInfo.電子發票B2C客單價));
                  //所屬平均電子發票B2B客單價
                  $("#avgB2BCusAmount").html(basicInfo.電子發票B2B客單價 == undefined  ? "查無資料":numberWithCommas(basicInfo.電子發票B2B客單價));
                  //營所稅營業淨利
                  $("#prcBusiNetPf").html(basicInfo.營所稅營業淨利 == undefined? "查無資料":numberWithCommas(basicInfo.營所稅營業淨利));
                  //營所稅營業收入淨額
                  $("#prcBusiRvnuNet").html(basicInfo.營所稅營業收入淨額 == undefined? "查無資料":numberWithCommas(basicInfo.營所稅營業收入淨額));
                  //營業稅發票申購張數
                  $("#bgmTotalCount").html(basicInfo.營業稅發票申購張數 == undefined? "查無資料":numberWithCommas(basicInfo.營業稅發票申購張數));
                  //營業稅銷項總計金額
                  $("#bgmSaleAmtTotal").html(basicInfo.營業稅銷項總計金額 == undefined? "查無資料":numberWithCommas(basicInfo.營業稅銷項總計金額));
                   
                  //所屬平均電子發票中獎張數
                  $("#awardCnt").html(basicInfo.電子發票中獎張數 == undefined? "查無資料":numberWithCommas(basicInfo.電子發票中獎張數));
                  //所屬平均電子發票中獎金額
                  $("#awardAmt").html(basicInfo.電子發票中獎金額 == undefined? "查無資料":numberWithCommas(basicInfo.電子發票中獎金額));
                  
                  $("#basicTable").show();
                  $("#drillChain").hide();
                  $("#industryTable").hide();
                  $("#placeholder").hide();
                  $("#flot-memo").hide();
                  $("#legend").hide();
                  
              } else {
                  bootbox.alert("查無資料！");
              }
          });
          //error處理
          promise.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:"+status+" Message:"+xhrException);
          });
      } else {
          var url1 = '/ods-main/ODS315E/plot';
          var ycolumn = $("#ycolumn").val();// 統計欄位
          
          var promise = chtAjaxRest.find(url1, formData, param);
          //success處理
          promise.done(function(data) {
              if(data && data.data && data.data.length > 0){
                  if(data.data.length==1){
                      dataset1 = data.data[0];
                      dataset2 = null;
                  }else if(data.data.length==2){
                      dataset1 = data.data[1];
                      dataset2 = data.data[0];
                  }
                  $("#basicTable").hide();
                  $("#drillChain").show();
                  $("#industryTable").show();
                  $("#placeholder").show();
                  $("#flot-memo").show();
                  $("#legend").show();
                  processDraw();
              } else {
                  bootbox.alert("查無資料！");
              }
          });
          //error處理
          promise.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:"+status+" Message:"+xhrException);
          });
      }
      
      
      
  };
  
  var chkDownloadColumns = function() {
      var msg = "";
      
      if ($("#chartType").val() == "") {
          msg = msg + "圖表類型 ";
      }
      if ($("#sDate").val() == "") {
          msg = msg + "發票日期(起) ";
      }
      if ($("#eDate").val() == "") {
          msg = msg + "發票日期(訖) ";
      }
      if ($("#ycolumn").val() == "") {
          msg = msg + "統計欄位 ";
      }
      if (msg != "") {
          msg = msg + "未選擇，請選擇後查詢，謝謝！";
          bootbox.alert(msg);          
          return false;
      } else {
          return true;
      }
  };
  
  var downloadDataset = function() {
      if (chkDownloadColumns()) {
          var startDate = $("#sDate").val().replace(/\//g, "");
          var endDate = $("#eDate").val().replace(/\//g, "");
          var yctype = $("#ycolumn").val();
          if("電子發票中獎張數"==yctype || "電子發票中獎金額"==yctype){
              yctype = 1;
          }else if("電子發票金額占比"==yctype || "電子發票張數占比"==yctype){
              yctype = 2;
          }else if("營所稅營業淨利占比"==yctype || "營所稅營業收入淨額占比"==yctype){
              yctype = 3;
          }else if("營業稅發票申購總計張數占比"==yctype || "營業稅銷項總計金額占比"==yctype){
              yctype = 4;
          }else if("營所稅營業淨利"==yctype || "營所稅營業收入淨額"==yctype){
              yctype = 5;
          }else if("營業稅發票申購張數"==yctype || "營業稅銷項總計金額"==yctype){
              yctype = 6;
          }else {
              //電子發票金額張數、客單價
              yctype = 7;
          }
          // 資料
          var formData = {
            packageId : $("#packageId").val(),
            packageVer : $("#packageVer").val(),
            startDate : startDate,
            endDate : endDate,
            chartType : $("#chartType").val(),
            ycolumn : yctype,
            aggreFun : $("#aggreFun").val(),
            hsnCd : $("#hsnCd").val(),
            townCd : $("#townCd").val(),
            industClass : $("#industClass").val(),
            selfCounty : $("#selfCounty").val(),
            selfArea : $("#selfArea").val(),
            selfIndustClass : $("#selfIndustClass").val(),
          };
          
          if("all"!=$("#selfIndustClass").val()){
              formData.selfIndustClass = $("#selfIndustClass").val();
          }
          
          var urlParam = "/";
          $.each(formData, function( index, value ) {
              urlParam = urlParam + value + "/";
          });
          window.open("/" + parseUrl().context + "/ODS315E/downloadDataset" + urlParam, "newwindow");
      }
  };
  
  var initDatepicker = function(element) {
      $(element).datepicker({
          dateFormat: "yy/mm/dd",
          changeYear: true,
          changeMonth: true,
          maxDate: '0',
          minDate: '-5y'
      });
  };
  var initRangeDatepicker = function(elementFrom, elementTo) {
      initDatepicker(elementFrom);
      initDatepicker(elementTo);
      $(elementFrom).datepicker("option", "onClose", 
          function(selectedDate) {
              $(elementTo).datepicker("option", "minDate", selectedDate);
          }
      );
      $(elementTo).datepicker("option", "onClose", 
          function(selectedDate) {
              $(elementFrom).datepicker("option", "maxDate", selectedDate);
          }
      );
  };
  
  var filterDate = function(data, sDate, eDate){
      var list = [];
      //console.log(sDate + " " + eDate);
       _.each( data, function( val, key ) {
           //console.log(val);
           _.each( val, function( val2, key2 ) {
               //console.log(key2);
               //console.log(val2);
               if (key2 == "發票日期") {
                   if (sDate != "" && eDate != "") {
                       if (val2 >= sDate && val2 <= eDate) {
                           list.push(val);
                       }
                   } else if (sDate != "") {
                       if (val2 >= sDate) {
                           list.push(val);
                       }
                   } else if (eDate != "") {
                       if (val2 <= eDate) {
                           list.push(val);
                       }
                   } else {
                       list.push(val);
                   }
               }
           });
                          
       });
       
       //console.log(list);       
       return list;
  };
     
  var parseUrl = function() {
      var parts = document.URL.split('/');
      var len = parts.length;
      return {
        context: parts.slice(0)[3],
        endpoint: parts.slice(0,[len-4]).join('/') 
      };
  };

  var numFormat = function(numb){
      var options = new JsNumberFormatter.formatNumberOptions();
      return JsNumberFormatter.formatNumber(numb, options, false);
  };

  var percFormat = function(numb){
      var options = new JsNumberFormatter.formatNumberOptions().specifyAll('#,##0', '00');
      return JsNumberFormatter.formatNumber(numb, options, false);
  };
  
  var goParent = function(chainIndex){
          var dimension = drillDownChain1[chainIndex]['dimension'];
          //console.log(drillDownChain1[chainIndex]['data']);
          var data1 = drillDownChain1[chainIndex]['data'];
          var data2 = null;
          drillDownChain1.splice(chainIndex + 1);
          drawDrillDownChain();
          drillDownOptionsChain.splice(chainIndex + 1);
          drawDrillDownOption(drillDownOptionsChain[drillDownOptionsChain.length - 1]);
          data1 = _.sortBy(data1, 'dimension');
          if(drillDownChain2.length!=0){
              data2 = drillDownChain2[chainIndex]['data'];
              drillDownChain2.splice(chainIndex + 1);
              data2 = _.sortBy(data2, 'dimension');
          }
          drawGraph(data1,data2);

  };
  
    var drillDown = function(dataIndex){
          var drillChainColumn = $("#drillChainOptions").val();
          
          if(drillChainColumn == null || drillChainColumn == ""){
            return;
          }
          
          var aggreFun = null;
          var ycolumn = $("#ycolumn").val();
          if(ycolumn.indexOf("占比", 0)>0){
            aggreFun = percentGroup;
          } else if(ycolumn == "電子發票客單價"){
            aggreFun = cptGroup;
          } else {
            aggreFun = sumGroup;
          }
          
          var nodeName = drillDownChain1[drillDownChain1.length - 1]['data'][dataIndex]['dimension'];
          var data1 = drillDownChain1[drillDownChain1.length - 1]['data'][dataIndex]['data'];
          var groupData1 = groupDataSet(data1, drillChainColumn);
          groupData1 = transform(groupData1);
          groupData1 = caculateGroup(groupData1, ycolumn, aggreFun);
          groupData1 = _.sortBy(groupData1, 'dimension');
          buildDrillDownChain(drillDownChain1, nodeName + '：' + drillChainColumn , drillChainColumn, groupData1, aggreFun);

          var data2 = null;
          var groupData2 = null;
          if(drillDownChain2.length != 0){
              data2 = drillDownChain2[drillDownChain2.length - 1]['data'][dataIndex]['data'];
              groupData2 = groupDataSet(data2, drillChainColumn);
              groupData2 = transform(groupData2);
              groupData2 = caculateGroup(groupData2, ycolumn, aggreFun);
              groupData2 = _.sortBy(groupData2, 'dimension');
              buildDrillDownChain(drillDownChain2, nodeName + '：' + drillChainColumn , drillChainColumn, groupData2, aggreFun);
          }
          drawGraph(groupData1, groupData2);
          buildDrillDownOption(drillChainColumn);
    };
     var buildDrillDownChain = function(drillDownChain, name, dimension, data, aggreFun){
        drillDownChain.push({'name' : name, 'dimension' : dimension,'data' : data, 'aggreFun': aggreFun});
        
        drawDrillDownChain();
      };
      
      var drawDrillDownChain = function(){
        var html = "";
        for(var i = 0; i < drillDownChain1.length; i++){
            html += "<a onclick='goParent(" + i + ")'>" + drillDownChain1[i]['name'] + "</a>";
            if( i != drillDownChain1.length - 1){
                html += "　＞　";
            }
        }
        $("#drillChain").html(html);
      };
      
      
        // drillDownOptionsChain = [{type1:{main: true,options:{'name':'鄉鎮市區', 'parent' : '縣市',
        // 'dimensionType':'1'}}}];
        var buildDrillDownOption = function(dimensionName){
        // drillDownOptionsChain
            var dimensionColumn = getDimensionColumn(dimensionName);
            var lastDrillDownOptions = null;
            var newDrillDownOptions = {};
            
            if(drillDownOptionsChain.length > 0){
                lastDrillDownOptions = drillDownOptionsChain[drillDownOptionsChain.length - 1];
            }
            
            for(var i = 0; i < dimensionTypes.length; i++){
                //console.log(dimensionTypes[i]);
                var dimensionType = dimensionTypes[i]['dimensionType'];
                if(dimensionColumn['dimensionType'] == dimensionType){
                    var options = getDimensionColumnChildren(dimensionName);
                    newDrillDownOptions['type' + dimensionType] = {'main' : true, 'options' : options};
                } else {
                    if(lastDrillDownOptions!= null){
                        newDrillDownOptions['type' + dimensionType] = {'main' : false, 'options' : lastDrillDownOptions['type' + dimensionType]['options']};
                    } else {
                        newDrillDownOptions['type' + dimensionType] = {'main' : false, 'options' : [getRootDimensionType(dimensionType)]};
                    }
                }
            }
            
            drillDownOptionsChain.push(newDrillDownOptions);
            drawDrillDownOption(newDrillDownOptions);
        };
        
        var drawDrillDownOption = function(newDrillDownOptions){
            var html = "";
            var isFirstMain = true;
            for(var i = 0; i < dimensionTypes.length; i++){
                var newDrillDownOption = newDrillDownOptions['type' + dimensionTypes[i]['dimensionType']];
                //console.log(newDrillDownOption);
                var options = newDrillDownOption['options'];
                var main = newDrillDownOption['main'];
                if(options != null){
                    for(var j = 0; j < options.length; j++){
                        if(main && isFirstMain){
                            html += "<option value='" +  options[j]['name'] + "' selected>" + options[j]['name'] + "</option>";
                        } else {
                            html += "<option value='" +  options[j]['name'] + "'>" + options[j]['name'] + "</option>";
                        }
                    }
                }
            }
            
            $("#drillChainOptions").html(html);
            
            
        };
      var getRootDimensionType = function(dimensionType){
          for(var i = 0; i < dimensionColumns.length; i++){
            if(dimensionColumns[i]['dimensionType'] == dimensionType && dimensionColumns[i]['parent'] == null){
                return dimensionColumns[i];
            }
          }
      };
      
      var getDimensionColumn = function(dimensionName){
          for(var i = 0; i < dimensionColumns.length; i++){
            if(dimensionColumns[i]['name'] == dimensionName){
                return dimensionColumns[i];
            }
          }
      };
      
      var getDimensionColumnChildren = function(dimensionName){
          var children = [];
          for(var i = 0; i < dimensionColumns.length; i++){
            if(dimensionColumns[i]['parent'] == dimensionName){
                children.push(dimensionColumns[i]);
            }
          }
          return children;
      };
      
      
      var groupDataSet = function(data, column){
          var result = _.groupBy(data, 
                function(item){ 
                      return item[column]; 
                }
          );
          
          _.each( result, function( val, key ) {
              result[key] = {'data': val};
          });
          
          return result;
      };
      
      var caculateGroup = function(data, aggreColumn, aggreFun){
        _.each( data, function( val, key ) {
              data[key]["yvalue"] = aggreFun(data[key], aggreColumn);
        });
        return data;
      };
      
      var sumGroup = function(item, valueColumn){
          var sum = _.reduce(item['data'], 
                      function(sum, item){ 
                            if(sum == null){
                                sum = 0;
                            }
                          return Number(sum) + Number(item[valueColumn]); 
                      }, 
                      0);
                      
          return sum;
          
      };
      
      //占比計算
      var percentGroup = function(item, valueColumn){
          var aggreFun = $("#aggreFun").val();
          var ycolumn = $("#ycolumn").val();
          var sum = 0;
          var areaSum = 0;
          if(ycolumn=="電子發票金額占比"){
            if(aggreFun=="all"){
              valueColumn = "電子發票B2C金額";
              sum = sum + sumGroup(item, valueColumn);
              areaSum = areaSum + sumGroup(item, "地區"+valueColumn);
              valueColumn = "電子發票B2B金額";
              sum = sum + sumGroup(item, valueColumn);
              areaSum = areaSum + sumGroup(item, "地區"+valueColumn);
            } else {
              valueColumn = "電子發票"+aggreFun+"金額";
              sum = sum + sumGroup(item, valueColumn);
              areaSum = areaSum + sumGroup(item, "地區"+valueColumn);
            }

          } else if(ycolumn=="電子發票張數占比"){
            if(aggreFun=="all"){
              valueColumn = "電子發票B2C張數";
              sum = sum + sumGroup(item, valueColumn);
              areaSum = areaSum + sumGroup(item, "地區"+valueColumn);
              valueColumn = "電子發票B2B張數";
              sum = sum + sumGroup(item, valueColumn);
              areaSum = areaSum + sumGroup(item, "地區"+valueColumn);
            } else {
              valueColumn = "電子發票"+aggreFun+"張數";
              sum = sum + sumGroup(item, valueColumn);
              areaSum = areaSum + sumGroup(item, "地區"+valueColumn);
            }
          } else {
            sum = sum + sumGroup(item, valueColumn);
            areaSum = areaSum + sumGroup(item, "地區"+valueColumn);
          }

          return (Math.round(sum/areaSum * 10000)/100);
      };
      
      //客單價計算
      var cptGroup = function(item, valueColumn){
        var sumAmt = sumGroup(item, "電子發票金額");
        var sumCnt = sumGroup(item, "電子發票張數");
        return ((sumAmt/sumCnt).toFixed(2));
      };
      
      var transform = function(data){
       var list = [];
        _.each( data, function( val, key ) {
              list.push({'dimension':key, data: val['data']});
        });
        
        return list;
      };
      
      var drawGraph = function(dataset1, dataset2){
        var list1 = [];
        var list2 = [];
        for(var i = 0; i< dataset1.length; i++){
            list1.push([dataset1[i]['dimension'],dataset1[i]['yvalue']]);
        }
        if(dataset2 != null){
            for(var i = 0; i< dataset2.length; i++){
                list2.push([dataset2[i]['dimension'],dataset2[i]['yvalue']]);
            }
        }
        
        var nodeName = drillDownChain1[drillDownChain1.length - 1]['dimension'];        
        var chartType = $("#chartType").val();
        var logAxis =  $("#logAxis").val();
        var showType = $("#showType").val();
        
        if (chartType != "columns") {
            $("#placeholder").css({width: '600px', height: '400px'});
        } else {
            var pHeight = 400;
            if (dataset1.length * 40 >= 400) {
                pHeight = dataset1.length * 40;
            }
            //console.log(pHeight);
            $("#placeholder").css({width: '600px', height: pHeight + 'px'});
        }
        // console.log(chartType);
        //判斷資料為單軸或雙軸
        var dataList = [];
        if(dataset2!=null){
            if("all"==showType||"area"==showType){
                dataList.push({ data: list1, label: "地區營業人", hoverable: true, clickable: true});
            }
            if("all"==showType||"self"==showType){
                dataList.push({ data: list2, label: "營業人", hoverable: true, clickable: true});
            }
        }else {
            dataList.push({ data: list1, label: "營業人", hoverable: true, clickable: true});
        }
        
        //判斷y軸顯示的單位
        var ycolumn = $("#ycolumn").val();
        var yaxisName = "(元)";// 單位
        if(ycolumn.indexOf("占比", 0)>0){
            yaxisName = "(%)";
        }else if(ycolumn=="電子發票張數"||ycolumn=="電子發票中獎張數"||ycolumn=="營業稅發票申購張數"){
            yaxisName = "(張數)";
        }
        
        //對數座標軸
        var amountTicks = [0,100,10000,1000000,100000000,10000000000,1000000000000,100000000000000];
        var countTicks = [0,10,100,1000,10000,100000,1000000,10000000];
        var ticks = "(元)"==yaxisName?amountTicks:countTicks;
        var logTickFormatter = function (v, axis) {
            if(v == 0){
               return '0'; 
            } else if(v == 10){
                return '拾';
            } else if(v == 100){
                return '佰';
            } else if(v == 1000){
                return '仟';
            } else if(v == 10000){
                return '萬';
            } else if(v > 10000 && v <= 10000000){
                return (v / 10000) + '萬';
            } else if(v == 100000000){
                return '億';
            } else if(v > 100000000 && v <= 100000000000){
                return (v / 100000000) + '億';
            } else if(v == 1000000000000){
                return '兆';
            } else if(v > 1000000000000 && v <= 1000000000000000){
                return (v / 1000000000000) + '兆';
            }
        };
        var transform = function (v) { 
            return v === 0 ? 0 : Math.log(v);
        };
        var inverseTransform = function (v) { 
            return Math.exp(v); 
        };
        var tmpTickformar = ycolumn.indexOf("占比", 0)>0?percFormat:"log"==logAxis?logTickFormatter:numFormat;
        var ods315TickFormat = function (v, axis) {
            return tmpTickformar(v, axis);
        };
        var ods315minTickSize = ycolumn.indexOf("占比", 0)>0?0.01:1;

        var options = {};
        options.grid = { 
                hoverable: true, 
                clickable: true,
                mouseActiveRadius: 30 // 指定滑鼠游標距離資料點半徑多遠時觸發事件
                }; 
        options.xaxis = {
                mode: "categories",
                tickLength: 0,
                axisLabel: "(" + nodeName + ")",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 18,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 10,
//                panRange: [0,null]   // or [number, number] (min, max) or false
                };
        options.yaxis = {
                axisLabel: yaxisName,
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 18,
                axisLabelFontFamily: 'Verdana, Arial',
                minTickSize: ods315minTickSize,
                tickFormatter: ods315TickFormat,
//                panRange: [0,null]   // or [number, number] (min, max) or false
                };
        if(ycolumn.indexOf("占比", 0)<0){//除佔比外不使用對數座標軸
            if("log"==logAxis){//根據選擇決定是否要show對數座標
                options.yaxis.ticks=ticks;
                options.yaxis.transform=transform;
                options.yaxis.inverseTransform=inverseTransform;
            }
        }
        
        options.legend = { show: true, noColumns: 2, container: $('#legendholder') };

//        options.zoom={
//            interactive: true
//        };
//        options.pan={
//            interactive: true
//        };
        if (chartType == "lines-and-points") {
            options.series = {
                    lines: { align:"center", show: true },
                    points: { align:"center", show: true }
            };
        } else if (chartType == "lines") {
            options.series = {
                    lines: { align:"center", show: true }
            };
        } else if (chartType == "points") {
            options.series = {
                    points: { align:"center", show: true }
            };
        } else if (chartType == "bars") {
            options.series = {
                    bars: { align:"center", show: true }
            };      
        } else if (chartType == "columns") {
            var listColumns1 = [];
            var listColumns2 = [];
            for(var i = 0; i< dataset1.length; i++){
                listColumns1.push([dataset1[i]['yvalue'],dataset1[i]['dimension']]);
            }
            if(dataset2 != null){
                for(var i = 0; i< dataset2.length; i++){
                    listColumns2.push([dataset2[i]['yvalue'],dataset2[i]['dimension']]);
                } 
            }
            dataList = [];
            if(dataset2!=null){
                if("all"==showType||"area"==showType){
                    dataList.push({ data: listColumns1, label: nodeName, hoverable: true, clickable: true});
                }
                if("all"==showType||"self"==showType){
                    dataList.push({ data: listColumns2, label: nodeName, hoverable: true, clickable: true});
                }
            }else {
                dataList.push({ data: listColumns1, label: nodeName, hoverable: true, clickable: true});
            }
            
            //橫條圖 x,y軸互換
            var tmp = options.xaxis;
            options.xaxis = options.yaxis;
            options.yaxis = tmp;
            
            options.series = {
                    bars: { align:"center", show: true }
            };
            options.bars = {
                    horizontal:true
            };
        } else if (chartType == "pie") {

            dataList = [];
            for(var i = 0; i< dataset1.length; i++){
                var tempData = {label:dataset1[i]['dimension'], data:dataset1[i]['yvalue']};
                dataList.push(tempData);
            }
            
            options.series = {
                    pie: {  show: true,
                        radius: 1,
                        label: {
                            show: true,
                            radius: 3/4,
                            formatter: function(label, series){
                                return '<div style="font-size:8pt;text-align:center;padding:2px;color:blue;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                            },
                            background: { opacity: 0.5 }
                        }
                },
            };
            options.legend = {
                    show: false
            };
            options.grid = {
                clickable: true,
                hoverable: true                        
            };
        }

        var plot = $.plot($("#placeholder"), dataList, options);
      };
      
      function xAxisLabelGenerator(x)
      {
          return xLabel[x];
      }

      function showTooltip(x, y, contents) {
          $('<div id="tooltip">' + contents + '</div>').css( {
              position: 'absolute',
              display: 'none',
              top: y + 1,
              left: x - 25,
              // border: '1px solid #fdd',
              padding: '2px',
              'background-color': '#F2D57A',
              opacity: 0.80
          }).appendTo("body").fadeIn(200);
      }
      function showTooltipPie(contents) {
        // console.log(contents);
        var html = "<div id=\"tooltipPie\"><span style=\"color:blue\">" + contents + "</span></div>";
        $("#flot-memo").html(html);
      }
      
      function showValue(plot, data, chartType) {
          $("[id=tooltip]").remove();
          var plotWidth = 593;
          var divPos = plot.offset();
          for (var i = 0; i < data.length; i++) {
              pos = plot.p2c({x: data[i][0], y: data[i][1]});
              console.log(data[i][0]);
              console.log(data[i][1]);
              console.log(pos.left);
              console.log(pos.top);
//              console.log((plotWidth/data.length)*i);
//              console.log(plotWidth/data.length/2);
//              console.log(plotWidth/10/data.length);
              //showTooltip(pos.left+divPos.left, pos.top+divPos.top, list[i][1]);
              if (chartType == "bars") {
                  showTooltip(divPos.left + plotWidth/data.length*i + plotWidth/data.length/2 - plotWidth/10/data.length, pos.top + divPos.top, data[i][1]);
              } else {
                  if (data.length == 1) {
                      showTooltip(divPos.left + plotWidth/2, pos.top + divPos.top, data[i][1]);
                  } else {
                      showTooltip(divPos.left + plotWidth/data.length*i, pos.top + divPos.top, data[i][1]);
                  }
              }
          }
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

          var promise = chtAjaxRest.get(url, formData, param);
          
          //success處理
          promise.done(function(data) {
          });
          
          //error處理
          promise.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:"+status+" Message:"+xhrException);
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
      
      function rmAttr(cVal){
          var $radios = $('input:radio[name=rating]');
          $radios.filter('[value="'+cVal+'"]').prop('checked', true);   
      }
      
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
      var selfCompany = [];
      function init() {
          var formData = {packageId:$("#packageId").val(), packageVer:$("#packageVer").val()};

          // RESTFul uri對應參數
          var param = {
              //ajaxdata : $("#ajaxdata").val()
          };

          // 查詢
          url = "/" + parseUrl().context + "/ODS315E/init";

          var promise = chtAjaxRest.find(url, formData, param);

          // success處理
          promise.done(function(data) {
              //alert(JSON.stringify(data));
              if (data.data == null) {
                  return ;
              }
              $("#tip").html(data.data.tip);
              var $radios = $('input:radio[name=rating]');
              if($radios.is(':checked') === false) {
                  $radios.filter('[value="'+data.data.odsUserPackageRate.rate+'"]').attr('checked', true);
              }
              
              anonymousUser = data.data.anonymousUser;
              --sls.global.blockUi.numBlockUi;
          });

          // error處理
          promise.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:" + status + " Message:" + xhrException);
          });
          
          // 取得地區選單
          var formDataSelect = {};
          // RESTFul uri對應參數
          var paramSelect = {
              //ajaxdata : $("#ajaxdata").val()
          };
          urlSelect = "/" + parseUrl().context + "/ODS315E/initFindCity";
          var promiseSelect = chtAjaxRest.find(urlSelect, formDataSelect, paramSelect);
          // success處理
          promiseSelect.done(function(data) {
              //alert(JSON.stringify(data));
              if (data.data == null) {
                  return ;
              }
              $("#hsnCd").find('option').remove();
              $("#hsnCd").append('<option value="all">全國</option>');
               // append new options
              $.each(data.data, function( index, value ) {
                  $("#hsnCd").append('<option value='+value.縣市代號+'>'+value.縣市代號+"-"+value.縣市名稱+'</option>');
              });
          });
          // error處理
          promiseSelect.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:" + status + " Message:" + xhrException);
          });
          
          // 取得登入者地區
          var formDataCity = {};
          // RESTFul uri對應參數
          var paramCity = {
              //ajaxdata : $("#ajaxdata").val()
          };
          urlCity = "/" + parseUrl().context + "/ODS315E/initFindSelect";
          var promiseCity = chtAjaxRest.find(urlCity, formDataCity, paramCity);
          // success處理
          promiseCity.done(function(data) {
              //alert(JSON.stringify(data));
              if (data.data == null || data.data == "") {
                  findTownCd();
                  return ;
              }
              //營業人所屬資訊 -縣市代號, 縣市名稱 , 鄉鎮代號 , 鄉鎮名稱, [營業項目代號 (2碼)], [營業項目名稱 (2碼)]
              selfCompany = parseCompanyData(data.data);

              //設定基本資料表
              if(data.data[0]["營業項目代號 (2碼)"] != null){
                  var industry = data.data[0]["營業項目代號 (2碼)"]+"-"+data.data[0]["營業項目名稱 (2碼)"];
                  $("#industryTable").find("tr:eq(0)").find("td:eq(1)").text(industry);
              }
              //設定所屬縣市鄉鎮行業
              if(data.data.length>1){//筆數大於1,表登入為總公司故有多筆
                  $("#corporationDiv").show();
                  $("#basicCorporationDiv1").show();
                  $("#basicCorporationDiv2").show();
              }else {
                  $("#corporationDiv").hide();
                  $("#basicCorporationDiv1").hide();
                  $("#basicCorporationDiv2").hide();
              }
              findSelfCounty(data.data[0].縣市代號);
              findSelfArea(data.data[0].鄉鎮代號);
              findSelfIndustClass(data.data[0]["營業項目代號 (2碼)"]);

              //縣市
              $("#headBanHsnCd").html(data.data[0].縣市代號 + "-" + data.data[0].縣市名稱);
              //鄉鎮市區
              $("#headBanTownCd").html(data.data[0].鄉鎮代號 + "-" + data.data[0].鄉鎮名稱);
              //營業項目 (2碼)
              $("#headBanBscd2").html(data.data[0]["營業項目代號 (2碼)"] + "-" + data.data[0]["營業項目名稱 (2碼)"]);
               
              
              //設定目標縣市鄉鎮行業
              selectOption("hsnCd",data.data[0].縣市代號,null);
              $("#hsnCd").val(data.data[0].縣市代號);
              
              findTownCd(data.data[0].鄉鎮代號);
              selectOption("industClass", data.data[0]["營業項目代號 (2碼)"],null);
              $("#industClass").val(data.data[0]["營業項目代號 (2碼)"]);
              
          });
          // error處理
          promiseCity.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:" + status + " Message:" + xhrException);
          });
      }

      //單純group
      var groupBanData = function(data, column){
          var result = _.groupBy(data, 
                function(item){ 
                      return item[column]; 
                }
          );
          return result;
      };
      //做出總分之機構下拉選單資料結構
      var parseCompanyData = function(data){
          var tmp = groupBanData(data, "縣市代號");
          _.each( tmp, function( val, key ) {
              var tmp2 = groupBanData(val, "鄉鎮代號");
              _.each( tmp2, function( val, key ) {
                  var tmp3 = groupBanData(val, "營業項目代號 (2碼)");
                  _.each( tmp3, function( val, key ) {
                      tmp3[key] = {name:val[0]["營業項目名稱 (2碼)"], data:val};
                  });
                  tmp2[key] = {name:val[0]["鄉鎮名稱"], data:tmp3};
              });
              tmp[key] = {name:val[0]["縣市名稱"], data:tmp2};
          });
          return tmp;
      };

      /**依據optValue 或 optName 選擇selectId element中的option, 以optValue為主,查不到再用optName去查
       * 使用方式:
       * selectOption('adtUnitNm', 'A05', null);
       * @param selectId select element
       * @param optValue selected value
       * @param optName selected name
       */
      function selectOption(selectId, optValue, optName){
          $("#"+selectId).children().each(function(){
              if(optValue!=null && optValue!=undefined && optName==null && optName==undefined){
                  if(""==optValue.toUpperCase()){
                      if(""==$(this).val()){
                          $(this).attr("selected","true"); //或是給selected也可
                          $(this).val($(this).val());
                          return 0;
                      }
                  }else{
                      if ($(this).val().toUpperCase().search(optValue.toUpperCase())!=-1){
                          //jQuery給法
                          $(this).attr("selected","true"); //或是給selected也可
                          $(this).val($(this).val());
                          return 0;
                      }
                  }
              }
              if(optValue!=null && optValue!=undefined && optName!=null && optName!=undefined){
                  if(""==optValue.toUpperCase()||""==optName.toUpperCase()){
                      if(""==$(this).val()){
                          $(this).attr("selected","true"); //或是給selected也可
                          $(this).val($(this).val());
                          return 0;
                      }
                  }else{
                      if (($(this).val().toUpperCase().search(optValue.toUpperCase())!=-1)
                              && ($(this).html().toUpperCase().search(optName.toUpperCase())!=-1)){
                          //jQuery給法
                          $(this).attr("selected","true"); //或是給selected也可
                          $(this).val($(this).val());
                          return 0;
                      }
                  }
              }
              if(optValue==null && optValue==undefined && optName!=null && optName!=undefined){
                  if(""==optName.toUpperCase()){
                      if(""==$(this).html()){
                          $(this).attr("selected","true"); //或是給selected也可
                          $(this).val($(this).val());
                          return 0;
                      }
                  }else{
                      if ($(this).html().toUpperCase().search(optName.toUpperCase())!=-1){
                          //jQuery給法
                          $(this).attr("selected","true"); //或是給selected也可
                          $(this).val($(this).val());
                          return 0;
                      }
                  }
              }
          });
      }
      
      function findSelfCounty(selfCounty) {
          // 取得所屬公司縣市
          $("#selfCounty").find('option').remove();
          $("#selfCounty").append('<option value="all">全國</option>');
           // append new options
          $.each(selfCompany, function( index, value ) {
              $("#selfCounty").append('<option value='+index+'>'+index+"-"+value.name+'</option>');
          });
          if(selfCounty!=null){
              selectOption('selfCounty', selfCounty, null);
              $("#selfCounty").val(selfCounty);
          }
      }
      
      function findSelfArea(selfArea) {
          // 取得所屬公司鄉鎮
          $("#selfArea").find('option').remove();
          $("#selfArea").append('<option value="all">全選</option>');
           // append new options
          var selfCounty = $("#selfCounty").val();
          $.each(selfCompany[selfCounty].data, function( index, value ) {
              $("#selfArea").append('<option value='+index+'>'+index+"-"+value.name+'</option>');
          });
          if(selfArea!=null){
              selectOption('selfArea', selfArea, null);
              $("#selfArea").val(selfArea);
          }
          findSelfIndustClass();
      }
      
      function findSelfIndustClass(selfIndust) {
          // 取得所屬營業代號
          $("#selfIndustClass").find('option').remove();
          $("#selfIndustClass").append('<option value="all">全選</option>');
          var selfCounty = $("#selfCounty").val();
          var selfArea = $("#selfArea").val();
          if('all'!=selfArea && 'all'!=selfCounty){
              $.each(selfCompany[selfCounty].data[selfArea].data, function( index, value ) {
                  $("#selfIndustClass").append('<option value='+index+'>'+index+"-"+value.name+'</option>');
              });
          }
          if(selfIndust!=null){
              selectOption('selfIndustClass', selfIndust, null);
              $("#selfIndustClass").val(selfIndust);
          }
          $("#industryTable").find("tr:eq(0)").find("td:eq(1)").text($("#selfIndustClass :selected ").text());
      }
      
      function findTownCd(town) {
          // 取得鄉鎮
          var formData = {hsnCd :$("#hsnCd").val()};
          // RESTFul uri對應參數
          var param = {
              //ajaxdata : $("#ajaxdata").val()
          };
          url = "/" + parseUrl().context + "/ODS315E/findTownCd";
          var promise = chtAjaxRest.find(url, formData, param);
          // success處理
          promise.done(function(data) {
              //alert(JSON.stringify(data));
              if (data.data == null) {
                  return ;
              }
              $("#townCd").find('option').remove();
              $("#townCd").append('<option value="all">全選</option>');
               // append new options
              $.each(data.data, function( index, value ) {
                  $("#townCd").append('<option value='+value.鄉鎮代號+'>'+value.鄉鎮代號+"-"+value.鄉鎮名稱+'</option>');
              });
              --sls.global.blockUi.numBlockUi;
              if(town!=null){
                  $("#townCd").val(town);
              }
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
      //幫數值補上千分位符號
      function numberWithCommas(x) {
          if(x== undefined || x==null){
            return x;
          }
          return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      }
      
