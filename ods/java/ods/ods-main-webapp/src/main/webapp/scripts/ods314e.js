    var dimensionTypes = [{'dimensionType': 1, 'name' : '地區別'},
                          {'dimensionType': 2, 'name' : '時間別'},
                          {'dimensionType': 3, 'name' : '行業別'},
                          {'dimensionType': 4, 'name' : '載具別'}];       
    var dimensionColumns = [{
                            'name':'縣市', 'parent' : null, 'dimensionType':'1'}
                                , {'name':'鄉鎮市區', 'parent' : '縣市', 'dimensionType':'1'}
                            , {'name':'發票年', 'parent' : null, 'dimensionType':'2'}
                                , {'name':'發票年月', 'parent' : '發票年', 'dimensionType':'2'}
                            , {'name':'行業別', 'parent' : null, 'dimensionType':'3'}
                            , {'name':'載具類別名稱', 'parent' : null, 'dimensionType':'4'}
                                , {'name':'載具名稱', 'parent' : '載具類別名稱', 'dimensionType':'4'}];
                            
    var currentDimensionStatus = [{'name':'縣市', 'depth':'0', 'dimensionType':'1'},
                                   {'name':'發票年', 'depth':'0', 'dimensionType':'2'},
                                   {'name':'行業別', 'depth':'0', 'dimensionType':'3'},
                                   {'name':'載具類別名稱', 'depth':'0', 'dimensionType':'4'}];

    var drillDownChains = [];
    var drillDownOptionsChains = [];
    var datasets = [];
    var anonymousUser;
    var datehange = true;

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
      
      if("true"==isSub && "RS"==role){
          if (parseUrl().context == 'ods-main') {
              init();
          }

          if ($('#packageCookie').val()=="false") {
              bootstroStart();
          }
          $('#yearDonateCntGoal').autoNumeric('init', {mDec:0});
          initRangeDatepicker("input[name='sDate']", "input[name='eDate']");
      }
      
      
      $("#queryBtn").click(function() {
          if (chkColumns()) {
              if(datehange){
                  getPlotDataSet();
                  datehange = false;
              }else{
                  //日期沒變則不用重抓資料
                  processDraw();                  
              }
          }
      });

      $("#setGoalBtn").click(function() {
          var msg = "";
          if ($("#yearDonateCntGoal").val() == "") {
              msg = msg + "年度捐贈目標 ";
          }
          if (msg != "") {
              msg = msg + "未輸入，請輸入後執行，謝謝！";
              bootbox.alert(msg);  
          } else {
              //資料
              var formData = {
                  packageId : $("#packageId").val(),
                  packageVer : $("#packageVer").val(),
                  yearDonateCntGoal : $("#yearDonateCntGoal").autoNumeric('get')
              };
              
              //RESTFul uri對應參數
              var param = {};
              
              //RESTful查詢
              var url = '/ods-main/ODS314E/setDonateInfo';

              var promise = chtAjaxRest.find(url, formData, param);
              
              //success處理
              promise.done(function(data) {

                  //alert(JSON.stringify(data));
                  if (data.data == null) {
                      return ;
                  }
                  $("#yearDonateCntGoal").autoNumeric('set', data.data.yearDonateCntGoal);
                  //$("#yearDonateCntGoal").html(data.data.yearDonateCntGoal);
                  //--sls.global.blockUi.numBlockUi;

                  bootbox.alert("設定完成！");
                  var thisYearDonateCnt = $("#thisYearDonateCnt").html().replace(",","");
                  while(thisYearDonateCnt.indexOf(",")>0){
                      thisYearDonateCnt = thisYearDonateCnt.replace(",","");
                  }
                  drawTemp(thisYearDonateCnt*1, data.data.yearDonateCntGoal*1);
              });
              
              //error處理
              promise.fail(function(xhrInstance, status, xhrException) {
                  bootbox.alert("fail:"+status+" Message:"+xhrException);
              });
          }
          
      });
      
      $("#downloadBtn").click(function() {
          downloadDataset();
      });
      
      // plothover
        var previousPoint = null;
        $("#placeholder").bind("plothover", function (event, pos, item) {
            if (item) {

                var avgName = "所有受捐贈機關或團體"==item.series.label?"平均":"";
                if (previousPoint != item.dataIndex) {
                    previousPoint = item.dataIndex;
                    
                    $("#tooltip").remove();
                    $("#tooltipPie").remove();
                     
                    var x = item.datapoint[0];
                    var y = item.datapoint[1];                

                    if ($("#chartType").val() == "columns") {    
                        var label = item.series.data[y][1];
                        var tipHtml = "";
                        tipHtml = "<div>" + item.series.label + ":" + label + "</div><div>" + avgName + $("#ycolumn").val() + " : " + numFormat(x) + "</div>";
                        showTooltip(item.pageX, item.pageY,
                          "<strong><div style='color:#0000FF'>" + tipHtml + "</div></strong>");
                    } else if ($("#chartType").val() != "pie") {
                        var label = item.series.data[x][0];
                        var tipHtml = "";
                        tipHtml = "<div>" + item.series.label + ":" + label + "</div><div>" + avgName + $("#ycolumn").val() +  " : " + numFormat(y) + "</div>";
                        showTooltip(item.pageX, item.pageY - 50,
                          "<strong><div style='color:#0000FF'>" + tipHtml + "</div></strong>");
                    };
                };
                if ($("#chartType").val() == "pie") {
                    $("#tooltip").remove();
                    $("#tooltipPie").remove();
                    //alert("exeute");
                    // console.log(item);
                    var drillDownChain = drillDownChains[0];
                    var label = drillDownChain[drillDownChain.length - 1]['dimension'];
                    
                    showTooltipPie(
                      label + " : " + item.series.label + " <br>" + avgName + $("#ycolumn").val() + " : " + numFormat(item.series.data[0][1]));
                };
            } else {
                $("#tooltip").remove();
                $("#tooltipPie").remove();
                previousPoint = null;
            };
        });
        $("#placeholderYearPer").bind("plothover", function (event, pos, item) {
            if (item) {
                var fun = "佔比";
                
                if (previousPoint != item.dataIndex) {
                    previousPoint = item.dataIndex;
                    
                    $("#tooltip").remove();
                    $("#tooltipPie").remove();
                     
                    var x = item.datapoint[0];
                    var y = item.datapoint[1];                

                    var label = item.series.data[x][0];
                    var tipHtml = "";
                    tipHtml = "<div>" + "發票年月"+ ":" + label + "</div><div>" + $("#ycolumn").val() + "-" + fun +  " : " + percFormat(y) + "%</div>";
                    showTooltip(item.pageX, item.pageY - 50,
                      "<strong><div style='color:#0000FF'>" + tipHtml + "</div></strong>");
                };
            } else {
                $("#tooltip").remove();
                $("#tooltipPie").remove();
                previousPoint = null;
            };
        });

      $("#placeholder").bind("plotclick", function (event, pos, item) {
          if (item) {
//              console.log(item.seriesIndex);
              if ($("#chartType").val() == "pie") {
                  drillDown(item.seriesIndex);
              } else {
                  drillDown(item.dataIndex);
              }
          }
      });
      
      $("#doRating").click(function() {
          //alert("isAnonymousUser:"+anonymousUser);
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

  });
  
  var drawTemp = function(thisYearDonateCnt, yearDonateCntGoal){
      $("#temp0").hide();
      $("#temp1").hide();
      $("#temp2").hide();
      $("#temp3").hide();
      $("#temp4").hide();
      var temp = ((thisYearDonateCnt*1.0)/(yearDonateCntGoal*1.0))*100.0;
      if (temp==0){
          $("#temp0").show();
      }else if (temp<=25){
          $("#temp1").show();
      }else if (temp<=50){
          $("#temp2").show();
      }else if (temp<=75){
          $("#temp3").show();
      }else{
          $("#temp4").show();
      }
      $("#tempRate").html(percFormat(temp));
  };
  
  var chkColumns = function() {
      var msg = "";
      
      if ($("#chartType").val() == "") {
          msg = msg + "圖表類型 ";
      }
      if ($("#sDate").val() == "") {
          msg = msg + "發票年月(起) ";
      }
      if ($("#eDate").val() == "") {
          msg = msg + "發票年月(訖) ";
      }
      if ($("#dimension").val() == "") {
          msg = msg + "統計維度 ";
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
      var dimension = $("#dimension").val();
      var selectDimension = null;
      for(var i = 0; i < currentDimensionStatus.length; i++){
          if(dimension == currentDimensionStatus[i]['dimensionType']){
              selectDimension = currentDimensionStatus[i];
          }
      }
      var aggreFun = sumGroup;
      var groupDatas = [];
      drillDownChains = [];
      drillDownOptionsChains = [];
      var sortList=[];//行業、載具排序用
      for(var idx=0;idx<datasets.length;idx++){
          var nodeName = selectDimension['name'];
          var groupData = groupDataSet(datasets[idx], selectDimension['name']);
          groupData = transform(groupData);
          groupData = caculateGroup(groupData, $("#ycolumn").val(), aggreFun);

          //針對載具、行業排序
          groupData = sortCarrierIndust(sortList, nodeName, groupData, idx);
          
          var drillDownChain = [];
          var drillDownOptionsChain = [];
          buildDrillDownChain(drillDownChain, selectDimension['name'], nodeName, groupData, aggreFun);
          
          drillDownChains.push(drillDownChain);
          drillDownOptionsChains.push(drillDownOptionsChain);
          groupDatas.push(groupData);
      }
      
      drawGraph(groupDatas);
      buildDrillDownOption(selectDimension['name']);
  };
  
  var getPlotDataSet = function() {
      //資料   
      var formData = {
          packageId : $("#packageId").val(),
          packageVer : $("#packageVer").val(),
          startDate : $("#sDate").val().replace(/\//g, ""),
          endDate : $("#eDate").val().replace(/\//g, ""),
          chartType :$("#chartType").val()
      };
      
      //RESTFul uri對應參數
      var param = {};
      
      //RESTful查詢
      var url = '/ods-main/ODS314E/plot';

      var promise = chtAjaxGzipRest.find(url, formData, param);
      
      //success處理
      promise.done(function(data) {
          if(data && data.data && data.data.length > 0){
              datasets = [];
              datasets.push(data.data[1]);
              datasets.push(data.data[0]);
              processDraw();
          } else {
              bootbox.alert("查無資料！");
          }
      });
      
      //error處理
      promise.fail(function(xhrInstance, status, xhrException) {
          bootbox.alert("fail:"+status+" Message:"+xhrException);
      });
  };
  
  var chkDownloadColumns = function() {
      var msg = "";

      if ($("#sDate").val() == "") {
          msg = msg + "發票年月(起) ";
      }
      if ($("#eDate").val() == "") {
          msg = msg + "發票年月(訖) ";
      }
      if (msg != "") {
          msg = msg + "未選擇，請選擇後下載，謝謝！";
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
    
          window.open("/" + parseUrl().context + "/ODS314E/downloadDataset/" + $("#packageId").val() + "/" + $("#packageVer").val() + "/" + startDate + "/" + endDate + "/", "newwindow");    
      }
  };
  
  var initDatepicker = function(element) {
      $(element).datepicker({
          dateFormat: "yy/mm",
          changeYear: true,
          changeMonth: true,
          //altField: "#alternate",
          //altFormat: "DD, d MM, yy"
          maxDate: '0',
          minDate: '-5y',
          onClose: function(dateText, inst) { 
              var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
              var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
              $(this).datepicker('setDate', new Date(year, month, 1));
              datehange = true;
          }
      });
      
  };
  var initRangeDatepicker = function(elementFrom, elementTo) {
      initDatepicker(elementFrom);
      initDatepicker(elementTo);
//      $(elementFrom).datepicker("option", "onClose", 
//          function(selectedDate) {
//              //$(elementTo).datepicker("option", "minDate", selectedDate);
//          }
//      );
//      $(elementTo).datepicker("option", "onClose", 
//          function(selectedDate) {
//              //$(elementFrom).datepicker("option", "maxDate", selectedDate);
//          }
//      );
  };
  
  var filterDate = function(data, sDate, eDate){
      var list = [];
      //console.log(sDate + " " + eDate);
       _.each( data, function( val, key ) {
           //console.log(val);
           _.each( val, function( val2, key2 ) {
               //console.log(key2);
               //console.log(val2);
               if (key2 == "發票年月") {
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

  var goParent = function(chainIndex) {
    var datas = [];
    var sortList = [];
    for (var idx = 0; idx < drillDownChains.length; idx++) {
        var drillDownChain = drillDownChains[idx];
        var drillDownOptionsChain = drillDownOptionsChains[idx];
        var dimension = drillDownChain[chainIndex]['dimension'];
        var groupData = drillDownChain[chainIndex]['data'];
        drillDownChain.splice(chainIndex + 1);
        drawDrillDownChain();
        drillDownOptionsChain.splice(chainIndex + 1);
        drawDrillDownOption(drillDownOptionsChain[drillDownOptionsChain.length - 1]);
        
        //針對載具、行業排序
        groupData = sortCarrierIndust(sortList, dimension, groupData, idx);

        datas.push(groupData);
    }
    drawGraph(datas);

  };
  
    var drillDown = function(dataIndex){
          var drillChainColumn = $("#drillChainOptions").val();
          
          if(drillChainColumn == null || drillChainColumn == ""){
            return;
          }

          var datas = [];
          var aggreFun = sumGroup;
          var sortList = [];
          for(var idx=0;idx<drillDownChains.length;idx++){
              var drillDownChain = drillDownChains[idx];
              var nodeName = drillDownChain[drillDownChain.length - 1]['data'][dataIndex]['dimension'];
              var data = drillDownChain[drillDownChain.length - 1]['data'][dataIndex]['data'];
              var groupData = groupDataSet(data, drillChainColumn);
              groupData = transform(groupData);
              groupData = caculateGroup(groupData, $("#ycolumn").val(), aggreFun);
              
              //針對載具、行業排序
              groupData = sortCarrierIndust(sortList, nodeName, groupData, idx);
              
              buildDrillDownChain(drillDownChain, nodeName + '：' + drillChainColumn , drillChainColumn, groupData, aggreFun);
              datas.push(groupData);
          }
          drawGraph(datas);
          buildDrillDownOption(drillChainColumn);
    };
     var buildDrillDownChain = function(drillDownChain, name, dimension, data, aggreFun){
        drillDownChain.push({'name' : name, 'dimension' : dimension,'data' : data, 'aggreFun': aggreFun});
        
        drawDrillDownChain();
      };
      
      var drawDrillDownChain = function(){
        var html = "";
        for(var idx = 0; idx < drillDownOptionsChains.length; idx++){
            var drillDownChain = drillDownChains[idx];
            for(var i = 0; i < drillDownChain.length; i++){
                html += "<a onclick='goParent(" + i + ")'>" + drillDownChain[i]['name'] + "</a>";
                if( i != drillDownChain.length - 1){
                    html += "　＞　";
                }
            }
            break;
        }
        $("#drillChain").html(html);
      };
      
      var sortCarrierIndust = function(sortList, nodeName, groupData, idx){
        var chartType = $("#chartType").val();
          // 行業、載具排序用
        if (nodeName.indexOf("行業") >= 0 || nodeName.indexOf("載具") >= 0) {
            if (idx == 0) {
                // 所有受捐贈機關或團體平均
                if(chartType == "columns"){
                    //橫軸圖不用reverse
                    groupData = _.sortBy(groupData, "yvalue");// 數值大的放前面
                }else{
                    groupData = _.sortBy(groupData, "yvalue").reverse();// 數值大的放前面
                }
                
                for (var i = 0; i < groupData.length; i++) {
                    sortList.push(groupData[i]['dimension']);
                }
            } else {
                // 自己受捐贈機關或團體計算，排序依據所有受捐贈機關或團體順序，若自己沒有則補0
                var tempdataset = [];
                for (var i = 0; i < sortList.length; i++) {
                    var matchFlag = false;
                    for (var j = 0; j < groupData.length; j++) {
                        if (sortList[i] == groupData[j]['dimension']) {
                            tempdataset.push({
                                dimension : groupData[j]['dimension'],
                                yvalue : groupData[j]['yvalue'],
                                data : groupData[j]['data'],
                                idx : i
                            });
                            matchFlag = true;
                            break;
                        }
                    }
                    if (!matchFlag) {
                        tempdataset.push({
                            dimension : sortList[i],
                            yvalue : 0,
                            data : [{}],
                            idx : i
                        });//補0
                    }
                }
                groupData = tempdataset;
            }
        }else {
            groupData = _.sortBy(groupData, 'dimension');
        }  
        return groupData;
      };
        // drillDownOptionsChain = [{type1:{main: true,options:{'name':'鄉鎮市區', 'parent' : '縣市',
        // 'dimensionType':'1'}}}];
        var buildDrillDownOption = function(dimensionName){
        // drillDownOptionsChain
            var dimensionColumn = getDimensionColumn(dimensionName);
            for(var idx = 0; idx < drillDownOptionsChains.length; idx++){
                var drillDownOptionsChain = drillDownOptionsChains[idx];
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
                
                drillDownOptionsChains[idx].push(newDrillDownOptions);
                drawDrillDownOption(newDrillDownOptions);
            }
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
      
      var numFormat = function(numb){
          if(numb==0 || "0"==numb){
              return "0";
          }
          var options = new JsNumberFormatter.formatNumberOptions();
          return JsNumberFormatter.formatNumber(numb, options, false);
//          return $.formatNumber(numb, { format: "#,###", locale: "us" });
      };

      var percFormat = function(numb){
          var options = new JsNumberFormatter.formatNumberOptions().specifyAll('#,##0', '##');
          return JsNumberFormatter.formatNumber(numb, options, false);
      };
      
      
      
      var transform = function(data){
       var list = [];
        _.each( data, function( val, key ) {
              list.push({'dimension':key, data: val['data']});
        });
        
        return list;
      };
      
      var drawGraph = function(datasets){
        //console.log(dataset);
        var dataList = [];
        var nodeName="";
        var yvalueCnt = 0;
        var datasetLength = 0;
        var dataFiledNameLength = 0;
        var chartType = $("#chartType").val();
        var logAxis =  $("#logAxis").val();
        var showType = $("#showType").val();
        
        //產生datalist
        if(chartType == "pie"){
            //圓餅圖只show自己的部份
            for(var i = 0; i< datasets[1].length; i++){
                var tempData = {label:datasets[1][i]['dimension'], data:datasets[1][i]['yvalue']};
                dataList.push(tempData);
            }
        
        } else {
            for(var idx = 0; idx < datasets.length; idx++){
                var nodeNameTmp = $("#ycolumn").val();
                nodeName = drillDownChains[idx][drillDownChains[idx].length - 1]['dimension'];
                if(idx==0){
                    nodeNameTmp = "所有受捐贈機關或團體";
                    datasetLength = datasets[idx].length;
                } else {
                    nodeNameTmp = "受捐贈機關或團體";
                }
                if(datasetLength==0){
                    datasetLength = datasets[idx].length;
                }
                
                var list = [];
                if (chartType == "columns") {
                    for(var i = 0; i< datasets[idx].length; i++){
                        list.push([datasets[idx][i]['yvalue'],datasets[idx][i]['dimension']]);
                        if(datasets[idx][i]['yvalue'].length>dataFiledNameLength){
                            dataFiledNameLength =  datasets[idx][i]['yvalue'].length;
                        }
                    }
                }else {
                    for(var i = 0; i< datasets[idx].length; i++){
                        list.push([datasets[idx][i]['dimension'],datasets[idx][i]['yvalue']]);
                    }
                }
                
                
                //計算zoom放大倍率
                if(idx==0){
                    yvalueCnt = list.length>31?list.length/31:1;
                }
                dataList.push( { data: list, label: nodeNameTmp, hoverable: true, clickable: true});
            }   
        }
        var dataListTmp = [];
        if (chartType == "pie"){
            dataListTmp.push(dataList[0]);
        }else if(dataList.length==2){
            if("all"==showType||"area"==showType){
                dataListTmp.push(dataList[0]);
            }
            if("all"==showType||"self"==showType){
                dataListTmp.push(dataList[1]);
            }
        }else {
            dataListTmp.push(dataList[0]);
        }
        dataList = dataListTmp;
        
        //設定畫布大小
        if (chartType != "columns") {
            $("#placeholder").css({width: '600px', height: '400px'});
        } else {
            var pHeight = 400;
            var pWidth = 600;
            if (datasetLength * 30 >= 400) {
                pHeight = datasetLength * 30;
            }
            if (dataFiledNameLength * 50 >= 600) {
                pWidth = dataFiledNameLength * 50;
            }
            //console.log(pHeight);
            $("#placeholder").css({width: pWidth+'px', height: pHeight + 'px'});
        }
        // console.log(chartType);
        var yaxisName = $("#ycolumn").val().indexOf("金額", 0)>0?"(元)":"(張數)";
        var amountTicks = [0,100,10000,1000000,100000000,10000000000,1000000000000,100000000000000];
        var countTicks = [0,10,100,1000,10000,100000,1000000,10000000];
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
        var ticks = $("#ycolumn").val().indexOf("金額", 0)>0?amountTicks:countTicks;

        var tmpTickformar = "log"==logAxis?logTickFormatter:numFormat;
        var ods314TickFormat = function (v, axis) {
            return tmpTickformar(v, axis);
        };
        
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
                panRange: [0,null],   // or [number, number] (min, max) or false
                };
        options.yaxis = {
                axisLabel: yaxisName,
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 18,
                axisLabelFontFamily: 'Verdana, Arial',
                minTickSize: 1,
                tickFormatter: ods314TickFormat,
                panRange: [0,null],   // or [number, number] (min, max) or false
                };
        if("log"==logAxis){//根據選擇決定是否要show對數座標
            options.yaxis.ticks=ticks;
            options.yaxis.transform=transform;
            options.yaxis.inverseTransform=inverseTransform;
        }
        options.legend = { show: true, noColumns: 2, container: $('#legendholder') };
        options.zoom={interactive: true};
        options.pan={interactive: true};
        
        var plot;
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
            options.series = {
                    bars: { align:"center", show: true }
            };
            options.bars={
                horizontal:true
            };
            //橫條圖 x,y軸互換
            var tmp = options.xaxis;
            options.xaxis = options.yaxis;
            options.yaxis = tmp;
        } else if (chartType == "pie") {
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
            options.legend= {
                    show: false
                };
            options.grid={
                        clickable: true,
                        hoverable: true                        
                };
        }
        plot = $.plot($("#placeholder"), dataList, options);
        //plot.zoom({amount:yvalueCnt, center: { left: 1, top: plot.height() }});
        //plot.pan({ left: -100, top: plot.height() });
        
      };

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
      
      function init() {
          var formData = {packageId:$("#packageId").val(), packageVer:$("#packageVer").val()};

          // RESTFul uri對應參數
          var param = {
              //ajaxdata : $("#ajaxdata").val()
          };

          // 查詢
          url = "/" + parseUrl().context + "/ODS314E/init";

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
              getDonateInfo();
          });

          // error處理
          promise.fail(function(xhrInstance, status, xhrException) {
              bootbox.alert("fail:" + status + " Message:" + xhrException);
          });
      }

      function getDonateInfo() {
          var formData = {packageId:$("#packageId").val(), packageVer:$("#packageVer").val()};

          // RESTFul uri對應參數
          var param = {
              //ajaxdata : $("#ajaxdata").val()
          };

          // 查詢
          url = "/" + parseUrl().context + "/ODS314E/getDonateInfo";

          var promise = chtAjaxRest.find(url, formData, param);

          // success處理
          promise.done(function(data) {
              //alert(JSON.stringify(data));
              if (data.data == null) {
                  return ;
              }

              
              //$("#yearDonateCntGoal").val(data.data.yearDonateCntGoal);
              $("#yearDonateCntGoal").autoNumeric('set', data.data.yearDonateCntGoal);
              $("#lastMonth").html(data.data.lastMonth);
              $("#lastMonthDonateCnt").html(numFormat(data.data.lastMonthDonateCnt));
              $("#lastMonthDonatePercent").html(data.data.lastMonthDonatePercent);
              $("#thisYearAwardAmt").html(numFormat(data.data.thisYearAwardAmt));
              $("#thisYearDonateCnt").html(numFormat(data.data.thisYearDonateCnt));
              $("#thisYearAwardCnt").html(numFormat(data.data.thisYearAwardCnt));
              $("#lastYearMonth").html(data.data.lastYearMonth);
              $("#lastYearMonthDonateCnt").html(numFormat(data.data.lastYearMonthDonateCnt));
              $("#lastYearMonthDonatePercent").html(data.data.lastYearMonthDonatePercent);
              drawTemp(data.data.thisYearDonateCnt*1, data.data.yearDonateCntGoal*1);
              
              //繪製過去一年捐贈佔比
              var datasetYearPer = data.data.percStat;

              var groupData = groupDataSet(datasetYearPer, "發票年月");
              groupData = transform(groupData);
              groupData = caculateGroup(groupData, "捐贈張數", sumGroup);
              groupData = _.sortBy(groupData, 'dimension');
                 
              var nodeName = "自己捐贈張數佔比";
              var list = [];
              for(var i = 0; i< groupData.length; i++){
                  list.push([groupData[i]['dimension'],groupData[i]['yvalue']]);
              }

              var dataList = [];
              dataList.push({ data: list, label: nodeName, hoverable: true});
              var plot = $.plot($("#placeholderYearPer"), dataList, {
                       series: {
                           lines: { align:"center", show: true },
                           points: { align:"center", show: true }
                       },
                       grid: { hoverable: true, 
                               clickable: true,
                               mouseActiveRadius: 30 }, // 指定滑鼠游標距離資料點半徑多遠時觸發事件
                       xaxis: {
                           mode: "categories",
                           tickLength: 0,
                           axisLabel: "發票年月",
                           axisLabelUseCanvas: true,
                           axisLabelFontSizePixels: 18,
                           axisLabelFontFamily: 'Verdana, Arial',
                           axisLabelPadding: 10},
                       yaxis: {
                           axisLabel: "捐贈張數佔比(%)",
                           axisLabelUseCanvas: true,
                           axisLabelFontSizePixels: 18,
                           axisLabelFontFamily: 'Verdana, Arial'}

              });
              --sls.global.blockUi.numBlockUi;
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
      };
      

      var chtAjaxGzipRest = new function(){
          //private 

          var setUrlParams = function(params, url) {
              var urlParams = {};
              $.each(url.split(/\W/), function (key, value) {
                if (value === 'hasOwnProperty') {
                  //throw $resourceMinErr('badname', "hasOwnProperty is not a valid parameter name.");
                }
                if (!(new RegExp("^\\d+$").test(value)) && value &&
                  (new RegExp("(^|[^\\\\]):" + value + "(\\W|$)").test(url))) {
                  urlParams[value] = true;
                }
              });
              url = url.replace(/\\:/g, ':');

              params = params || {};
              $.each(urlParams, function (urlParam, _){
                val = params.hasOwnProperty(urlParam) ? params[urlParam] : "";
                if (isDefined(val) && val !== null) {
                  encodedVal = encodeUriSegment(val);
                  url = url.replace(new RegExp(":" + urlParam + "(\\W|$)", "g"), function (match, p1) {
                    return encodedVal + p1;
                  });
                } else {
                  url = url.replace(new RegExp("(\/?):" + urlParam + "(\\W|$)", "g"), function (match,
                      leadingSlashes, tail) {
                    if (tail.charAt(0) == '/') {
                      return tail;
                    } else {
                      return leadingSlashes + tail;
                    }
                  });
                }
              });

              // strip trailing slashes and set the url (unless this behavior is specifically disabled)
              if (true) {
                url = url.replace(/\/+$/, '') || '/';
              }

              // then replace collapse `/.` if found in the last URL path segment before the query
              // E.g. `http://url.com/id./format?q=x` becomes `http://url.com/id.format?q=x`
              url = url.replace(/\/\.(?=\w+($|\?))/, '.');
              // replace escaped `/\.` with `/.`
              url = url.replace(/\/\\\./, '/.');
              return url;
          };

          var isDefined = function(value){return typeof value !== 'undefined';}
          /**
           * @param data
           *            資料
           * @param actionParams
           *            物件{xx:xx}，value有@則從data找對應欄位
           * @returns {}
           */
          var extractParams = function(data, actionParams) {
              var ids = {};
              $.each(actionParams, function(key, value) {
                  if (isFunction(value)) {
                      value = value();
                  }
                  ids[key] = value && value.charAt && value.charAt(0) == '@' ? lookupDottedPath(data, value
                          .substr(1)) : value;
              });
              return ids;
          };

          // Helper functions and regex to lookup a dotted path on an object
          // stopping at undefined/null. The path must be composed of ASCII
          // identifiers (just like $parse)
          var MEMBER_NAME_REGEX = /^(\.[a-zA-Z_$][0-9a-zA-Z_$]*)+$/;

          var isValidDottedPath = function(path) {
              return (path != null && path !== '' && path !== 'hasOwnProperty' && MEMBER_NAME_REGEX.test('.'
                      + path));
          };

          var lookupDottedPath = function(obj, path) {
              if (!isValidDottedPath(path)) {
                  // throw $resourceMinErr('badmember', 'Dotted member path "@{0}" is invalid.', path);
              }
              var keys = path.split('.');
              for (var i = 0, ii = keys.length; i < ii && obj !== undefined; i++) {
                  var key = keys[i];
                  obj = (obj !== null) ? obj[key] : undefined;
              }
              return obj;
          };
          var isFunction = function(value) {
              return typeof value === 'function';
          };

          /**
           * We need our custom method because encodeURIComponent is too aggressive and doesn't follow
           * http://www.ietf.org/rfc/rfc3986.txt with regards to the character set
           * (pchar) allowed in path segments:
           *    segment       = *pchar
           *    pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
           *    pct-encoded   = "%" HEXDIG HEXDIG
           *    unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
           *    sub-delims    = "!" / "$" / "&" / "'" / "(" / ")"
           *                     / "*" / "+" / "," / ";" / "="
           */
          var encodeUriSegment = function(val) {
            return encodeUriQuery(val, true).
              replace(/%26/gi, '&').
              replace(/%3D/gi, '=').
              replace(/%2B/gi, '+');
          };


          /**
           * This method is intended for encoding *key* or *value* parts of query component. We need a
           * custom method because encodeURIComponent is too aggressive and encodes stuff that doesn't
           * have to be encoded per http://tools.ietf.org/html/rfc3986:
           *    query       = *( pchar / "/" / "?" )
           *    pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
           *    unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
           *    pct-encoded   = "%" HEXDIG HEXDIG
           *    sub-delims    = "!" / "$" / "&" / "'" / "(" / ")"
           *                     / "*" / "+" / "," / ";" / "="
           */
          var encodeUriQuery = function(val, pctEncodeSpaces) {
            return encodeURIComponent(val).
              replace(/%40/gi, '@').
              replace(/%3A/gi, ':').
              replace(/%24/g, '$').
              replace(/%2C/gi, ',').
              replace(/%20/g, (pctEncodeSpaces ? '%20' : '+'));
          };


          var headers = {
              "Accept" : "application/json, text/plain, */*",
              "Accept-Encoding":'gzip,deflate'
          };
          
          //加上block ui
          var unBlockUiFunc = function() {
              --sls.global.blockUi.numBlockUi;
              if (sls.global.blockUi.numBlockUi <= 0) {
                  $.unblockUI();
              }
          };
          $(document).ajaxStop(unBlockUiFunc);
          var ajaxFun = function(url, type, param, formData, contentType, headers, dfr) {
              $.blockUI(sls.global.blockUi.config);
              sls.global.blockUi.numBlockUi++;
              
              var csrf_header = $("[name='_csrf_header']").attr("content");
              headers[csrf_header] = $("[name='_csrf']").attr("content");
              url = setUrlParams(param, url);
              $.ajax({
                  url : url,
                  type : type,
                  data : JSON.stringify(formData),
                  dataType : 'json',
                  contentType : contentType,
                  headers : headers,
                  success : dfr.resolve,
                  error : dfr.reject
              });
          };
          
          //public 
          return {
              find : function(url, formData, param) {
                  var dfr = $.Deferred();
                  ajaxFun(url, "post", param, formData, 'application/json;charset=utf-8', headers, dfr);
                  return dfr.promise();
              },
              update : function(url, formData, param) {
                  var dfr = $.Deferred();
                  ajaxFun(url, "post", param, formData, 'application/json;charset=utf-8', headers, dfr);
                  return dfr.promise();
              },
              create : function(url, formData, param) {
                  var dfr = $.Deferred();
                  ajaxFun(url, "put", param, formData, 'application/json;charset=utf-8', headers, dfr);
                  return dfr.promise();
              },
              remove : function(url, formData, param) {
                  var dfr = $.Deferred();
                  ajaxFun(url, "delete", param, formData, 'application/json;charset=utf-8', headers, dfr);
                  return dfr.promise();
              }
          };
      };
      
      
