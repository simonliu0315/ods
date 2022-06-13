    var dimensionTypes = [{'dimensionType': 1, 'name' : '地區別'},{'dimensionType': 2, 'name' : '時間'},{'dimensionType': 3, 'name' : '商店種類'},{'dimensionType': 4, 'name' : '載具別'}];       
    var dimensionColumns = [{'name':'縣市', 'parent' : null, 'dimensionType':'1'}, {'name':'鄉鎮市區', 'parent' : '縣市', 'dimensionType':'1'}
                            , {'name':'年', 'parent' : null, 'dimensionType':'2'}, {'name':'年月', 'parent' : '年', 'dimensionType':'2'}
                            , {'name':'發票日期', 'parent' : '年月', 'dimensionType':'2'}, {'name':'工作日類別', 'parent' : '年月', 'dimensionType':'2'}
                            , {'name':'當月週次', 'parent' : '年月', 'dimensionType':'2'}
                            , {'name':'商店種類', 'parent' : null, 'dimensionType':'3'}
                            , {'name':'載具別', 'parent' : null, 'dimensionType':'4'}
                            , {'name':'載具名稱', 'parent' : '載具別', 'dimensionType':'4'}];
                            
    var currentDimensionStatus = [{'name':'縣市', 'depth':'0', 'dimensionType':'1'},
                           //{'name':'年月', 'depth':'0', 'dimensionType':'2'},
                           {'name':'年', 'depth':'0', 'dimensionType':'2'},
                           {'name':'商店種類', 'depth':'0', 'dimensionType':'3'},
                           {'name':'載具別', 'depth':'0', 'dimensionType':'4'}];

    // dataset = [{'dimension':'x1', 'yvalue': 'y1', 'data':[]},{'dimension':'x2', 'yvalue': 'y2',
    // 'data':[]}]
    // drillDownChain = [{'name' : '縣市', 'dimension':'縣市', 'data' : dataset, 'aggreFun': aggreFun},
    // {'name' : '鄉鎮市區(三民區)', 'dimension':'鄉鎮市區', 'data' : dataset, 'aggreFun': aggreFun}]
    //
    var drillDownChain = [];
    var drillDownOptionsChain = [];
    var dataset;
    var anonymousUser;
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
      if("true"==isSub && "RN"==role){
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
        $("#placeholder").bind("plothover", function (event, pos, item) {
            if (item) {
                var fun = "";
                if ($("#aggreFun").val() == "sum") {
                    fun = "總數";
                } else if ($("#aggreFun").val() == "avg") {
                    fun = "平均數";
                }
                
                if (previousPoint != item.dataIndex) {
                    previousPoint = item.dataIndex;
                    
                    $("#tooltip").remove();
                    $("#tooltipPie").remove();
                     
                    var x = item.datapoint[0];
                    var y = item.datapoint[1];                
                     
                    if ($("#chartType").val() == "columns") {    
                        var label = item.series.data[y][1];
                        var tipHtml = "";
                        tipHtml = "<div>" + item.series.label + ":" + label + "</div><div>" + $("#ycolumn").val() + "-" + fun + " : " + x + "</div>";
                        showTooltip(item.pageX, item.pageY,
                          "<strong><div style='color:#0000FF'>" + tipHtml + "</div></strong>");
                    } else if ($("#chartType").val() != "pie") {
                        var label = item.series.data[x][0];
                        var tipHtml = "";
                        tipHtml = "<div>" + item.series.label + ":" + label + "</div><div>" + $("#ycolumn").val() + "-" + fun +  " : " + y + "</div>";
                        showTooltip(item.pageX, item.pageY - 50,
                          "<strong><div style='color:#0000FF'>" + tipHtml + "</div></strong>");
                    };
                };
                if ($("#chartType").val() == "pie") {
                    $("#tooltip").remove();
                    $("#tooltipPie").remove();
                    //alert("exeute");
                    // console.log(item);
                    var label = drillDownChain[drillDownChain.length - 1]['dimension'];
                    
                    showTooltipPie(
                      label + " : " + item.series.label + " <br>" + $("#ycolumn").val() + "-" + fun + " : " + item.series.data[0][1]);
                };
            } else {
                $("#tooltip").remove();
                $("#tooltipPie").remove();
                previousPoint = null;
            };
        });

      $("#placeholder").bind("plotclick", function (event, pos, item) {
          if (item) {
              console.log(item.seriesIndex);
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
      if ($("#dimension").val() == "") {
          msg = msg + "統計維度 ";
      }
      if ($("#ycolumn").val() == "") {
          msg = msg + "統計欄位 ";
      }
      if ($("#aggreFun").val() == "") {
          msg = msg + "統計總數/平均數 ";
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
//      var sDate = $("#sDate").val();
//      var eDate = $("#eDate").val();
      
      for(var i = 0; i < currentDimensionStatus.length; i++){
          if(dimension == currentDimensionStatus[i]['dimensionType']){
              selectDimension = currentDimensionStatus[i];
          }
      }

//      var dataFilterDate = filterDate(dataset, sDate, eDate);
      // dataset = [{'dimension':'x1', 'data':[]},{'dimension':'x2', 'data':[]}]
      var groupData = groupDataSet(dataset, selectDimension['name']);
      groupData = transform(groupData);
      var aggreFun = null;
      if($("#aggreFun").val() == "sum"){
        aggreFun = sumGroup;
      } else {
        aggreFun = avgGroup;
      }
      
      groupData = caculateGroup(groupData, $("#ycolumn").val(), aggreFun);
      groupData = _.sortBy(groupData, 'dimension');
      drillDownChain = [];
      drillDownOptionsChain = [];
      buildDrillDownChain(drillDownChain, selectDimension['name'], selectDimension['name'], groupData, aggreFun);
      
      drawGraph(groupData);
      buildDrillDownOption(selectDimension['name']);
  };
  
  var getPlotDataSet = function() {
      //資料
      var formData = {
          packageId : $("#packageId").val(),
          packageVer : $("#packageVer").val(),
          startDate : $("#sDate").val(),
          endDate : $("#eDate").val()
      };
      
      //RESTFul uri對應參數
      var param = {};
      
      //RESTful查詢
      var url = '/ods-main/ODS310E/plot';
      //url = '/ods-main/ODS311E/rest/initPage';

      var promise = chtAjaxRest.find(url, formData, param);
      
      //success處理
      promise.done(function(data) {
          if(data && data.data && data.data.length > 0){
              dataset = data.data;
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
          msg = msg + "發票日期(起) ";
      }
      if ($("#eDate").val() == "") {
          msg = msg + "發票日期(訖) ";
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
    
          window.open("/" + parseUrl().context + "/ODS310E/downloadDataset/" + $("#packageId").val() + "/" + $("#packageVer").val() + "/" + startDate + "/" + endDate + "/", "newwindow");    
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
  
//  function downloadDataset() {
//      //資料
//      var formData = {
//          packageId : $("#packageId").val(),
//          packageVer : $("#packageVer").val(),
//          dataArray: dataset
//      };
//      
//      console.log(formData.dataArray);
//      
//      //RESTFul uri對應參數
//      var param = {};
//      
//      //RESTful查詢
//      var url = '/ods-main/ODS310E/downloadDataset';
//
//      var promise = chtAjaxRest.find(url, formData, param);
//      
//      //success處理
//      promise.done(function(data) {          
//          console.log(data);
//      });
//      
//      //error處理
//      promise.fail(function(xhrInstance, status, xhrException) {
//          alert("fail:"+status+" Message:"+xhrException);
//      });
//  }
  
//  var getPlotDataSet = function () {
//      var formData = {
//          packageId : $("#packageId").val(),
//          packageVer : $("#packageVer").val()
//      };
//      var headers = {
//          "Accept" : "application/json, text/plain, */*"
//      };
//      var searchUrl = '/ods-main/ODS310E/plot';
//      var jqxhr = jQuery.ajax({
//          url: searchUrl,
//          type: 'POST',
//          headers : headers,
//          contentType : 'application/json;charset=utf-8',
//          data: formData
//      });
//      console.log(jqxhr);
//      return jqxhr;
//  };

  var goParent = function(chainIndex){
          var dimension = drillDownChain[chainIndex]['dimension'];
          //console.log(drillDownChain[chainIndex]['data']);
          var data = drillDownChain[chainIndex]['data'];
          drillDownChain.splice(chainIndex + 1);
          drawDrillDownChain();
          drillDownOptionsChain.splice(chainIndex + 1);
          drawDrillDownOption(drillDownOptionsChain[drillDownOptionsChain.length - 1]);
          data = _.sortBy(data, 'dimension');
          drawGraph(data);

  };
  
    var drillDown = function(dataIndex){
          var drillChainColumn = $("#drillChainOptions").val();
          
          if(drillChainColumn == null || drillChainColumn == ""){
            return;
          }
          
          var nodeName = drillDownChain[drillDownChain.length - 1]['data'][dataIndex]['dimension'];
          var data = drillDownChain[drillDownChain.length - 1]['data'][dataIndex]['data'];
          var groupData = groupDataSet(data, drillChainColumn);
          groupData = transform(groupData);
          var aggreFun = null;
          if($("#aggreFun").val() == "sum"){
            aggreFun = sumGroup;
          } else {
            aggreFun = avgGroup;
          }
          
          groupData = caculateGroup(groupData, $("#ycolumn").val(), aggreFun);
          groupData = _.sortBy(groupData, 'dimension');
          buildDrillDownChain(drillDownChain, nodeName + '：' + drillChainColumn , drillChainColumn, groupData, aggreFun);
          drawGraph(groupData);
          buildDrillDownOption(drillChainColumn);
    };
     var buildDrillDownChain = function(drillDownChain, name, dimension, data, aggreFun){
        drillDownChain.push({'name' : name, 'dimension' : dimension,'data' : data, 'aggreFun': aggreFun});
        
        drawDrillDownChain();
      };
      
      var drawDrillDownChain = function(){
        var html = "";
        for(var i = 0; i < drillDownChain.length; i++){
            html += "<a onclick='goParent(" + i + ")'>" + drillDownChain[i]['name'] + "</a>";
            if( i != drillDownChain.length - 1){
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
      // var dimensionColumns = [{'name':'縣市', 'parent' : null, 'dimensionType':'1'},
        // {'name':'鄉鎮市區', 'parent' : '縣市', 'dimensionType':'1'}
        // , {'name':'月份', 'parent' : null, 'dimensionType':'2'}
        // , {'name':'發票日期', 'parent' : '月份', 'dimensionType':'2'}, {'name':'工作日類別', 'parent' :
        // '月份', 'dimensionType':'2'}
        // , {'name':'週次', 'parent' : '月份', 'dimensionType':'2'}];
      // var dimensionTree = [{'name':'縣市', 'depth':'0', 'dimensionType':'1',
        // children:[{'name':'鄉鎮市區', 'depth':'1', 'dimensionType':'1'}]},
        // {'name':'月份', 'depth':'0', 'dimensionType':'2', children:[{'name':'發票日期', 'depth':'1',
        // 'dimensionType':'2'}, {'name':'工作日類別', 'depth':'1', 'dimensionType':'2'},]}];

      // var currentDimensionStatus = [{'name':'縣市', 'depth':'-1', 'dimensionType':'1'},
        // {'name':'月份', 'depth':'-1', 'dimensionType':'2'}];
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
      
      var avgGroup = function(item, valueColumn){
          var avg = sumGroup(item, valueColumn)/item['data'].length;
          avg = Math.round(avg * 100) / 100;
          return avg;
      };
      
      var transform = function(data){
       var list = [];
        _.each( data, function( val, key ) {
              list.push({'dimension':key, data: val['data']});
        });
        
        return list;
      };
      
      var drawGraph = function(dataset){
        //console.log(dataset);
        
        var list = [];
        for(var i = 0; i< dataset.length; i++){
            list.push([dataset[i]['dimension'],dataset[i]['yvalue']]);
        }
        
        var nodeName = drillDownChain[drillDownChain.length - 1]['dimension'];        
        var chartType = $("#chartType").val();
        if (chartType != "columns") {
            $("#placeholder").css({width: '600px', height: '400px'});
        } else {
            var pHeight = 400;
            if (dataset.length * 40 >= 400) {
                pHeight = dataset.length * 40;
            }
            //console.log(pHeight);
            $("#placeholder").css({width: '600px', height: pHeight + 'px'});
        }
        // console.log(chartType);
        if (chartType == "lines-and-points") {
            var plot = $.plot($("#placeholder"),
                 [ { data: list, label: nodeName, hoverable: true, clickable: true}], {
                     series: {
                         lines: { align:"center", show: true },
                         points: { align:"center", show: true }
                     },
                     grid: { hoverable: true, 
                             clickable: true,
                             mouseActiveRadius: 30 }, // 指定滑鼠游標距離資料點半徑多遠時觸發事件
                     xaxis: {
                         mode: "categories",
                         tickLength: 0}

            });
//            showValue(plot, list, "lines-and-points");
            
        } else if (chartType == "lines") {
            var plot = $.plot($("#placeholder"),
                 [ { data: list, label: nodeName, hoverable: true, clickable: true}], {
                     series: {
                         lines: { align:"center", show: true }
                     },
                     grid: { hoverable: true, 
                             clickable: true,
                             mouseActiveRadius: 30 }, // 指定滑鼠游標距離資料點半徑多遠時觸發事件
                     xaxis: {
                         mode: "categories",
                         tickLength: 0}

            });
//            showValue(plot, list, "lines");
            
        } else if (chartType == "points") {
            var plot = $.plot($("#placeholder"),
                 [ { data: list, label: nodeName, hoverable: true, clickable: true}], {
                     series: {
                         points: { align:"center", show: true }
                     },
                     grid: { hoverable: true, 
                             clickable: true,
                             mouseActiveRadius: 30 }, // 指定滑鼠游標距離資料點半徑多遠時觸發事件
                     xaxis: {
                         mode: "categories",
                         tickLength: 0}

            });
//            showValue(plot, list, "points");
            
        } else if (chartType == "bars") {
            var plot = $.plot($("#placeholder"),
                 [ { data: list, label: nodeName, hoverable: true, clickable: true}], {
                     series: {
                         bars: { align:"center", show: true }
                     },
                     grid: { hoverable: true, 
                             clickable: true,
                             mouseActiveRadius: 30 }, // 指定滑鼠游標距離資料點半徑多遠時觸發事件
                     xaxis: {
                         mode: "categories",
                         tickLength: 0}
                         
                    

            });            
//            showValue(plot, list, "bars");
            
        } else if (chartType == "columns") {
            var listColumns = [];
            for(var i = 0; i< dataset.length; i++){
                listColumns.push([dataset[i]['yvalue'],dataset[i]['dimension']]);
            }
            var plot = $.plot($("#placeholder"),
                 [ { data: listColumns, label: nodeName, hoverable: true, clickable: true}], {
                     series: {
                         bars: { align:"center", show: true }
                     },
                     bars:{
                         horizontal:true
                     },
                     grid: { hoverable: true, 
                             clickable: true,
                             mouseActiveRadius: 30 // 指定滑鼠游標距離資料點半徑多遠時觸發事件
                             //backgroundColor: { colors: ["#00FF00", "#FF00FF"] } 
                     },
                     yaxis: {
                         mode: "categories",
                         tickLength: 0}

            });
//            showValue(plot, listColumns, "columns");
            
        } else if (chartType == "pie") {

            var data = [];
            for(var i = 0; i< dataset.length; i++){
                var tempData = {label:dataset[i]['dimension'], data:dataset[i]['yvalue']};
                data.push(tempData);
            }

            var options = {
                series: {
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
                    
                },
                legend: {
                    show: false
                },
                grid: {
                        clickable: true,
                        hoverable: true                        
                }
            };
            
            $.plot($("#placeholder"), data, options);
        }
        
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
      
      function init() {
          var formData = {packageId:$("#packageId").val(), packageVer:$("#packageVer").val()};

          // RESTFul uri對應參數
          var param = {
              //ajaxdata : $("#ajaxdata").val()
          };

          // 查詢
          url = "/" + parseUrl().context + "/ODS310E/init";

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
      
      
