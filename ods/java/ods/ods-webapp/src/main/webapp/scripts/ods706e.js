(function() {

    var app = angular.module("ods706eApp", ['slsCommonModule', 'angularFileUpload']);
    app.factory('ods706eService',function(cResource){
        var resource = cResource('rest/criterias/:resId',{resId :"@resId"});
        //勿刪!!!用find會導致ie8抓cache的問題，導致在新增、修改後的查詢無法執行
        //var criteriasByResId = cResource('rest/criterias/:resId',{resId :"@resId"});
        //var criteriaDetailByResIdCriId = cResource('rest/criteria_detail/:resId/:criId',{resId :"@resId", criId :"@criId"});
        //var c_criteriaByResId = cResource('rest/create_criteria/:resId/:name/:description',{resId :"@resId", name:"@name", description:"@description"});
        //var s_criteriaByResIdCriId = cResource('rest/save_criteria/:resId/:criId/:name/:description',{resId :"@resId", criId:"@criId", name:"@name", description:"@description"});
        //var r_criteriaByResIdCriId = cResource('rest/remove_criteria/:resId/:criId',{resId :"@resId", criId:"@criId"});

        //var c_criteriaDetailByResIdCriId = cResource('rest/create_criteria_detail/:resId/:criId/:dataField/:aggregateFunc/:operator/:target',{resId :"@resId", criId:"@criId", dataField:"@dataField", aggregateFunc:"@aggregateFunc", operator:"@operator", target:"@target"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {
            
            //勿刪!!!用find會導致ie8抓cache的問題，導致在新增、修改後的查詢無法執行
            /*findCriteriasByResId : function(resId,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return criteriasByResId.find({resId: resId},options);
                //return criteriasByResId.execute({resId: resId},options);
            },*/
            findCriteriasByResId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            
            /*findCriteriaDetailByResIdCriId : function(resId, criId){
                return criteriaDetailByResIdCriId.find({resId: resId, criId: criId});
            },*/
            findCriteriaDetailByResIdCriId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            
            /*createCriteriaByResId : function(resId, name, description){
                return c_criteriaByResId.create({resId: resId, name: name, description: description});
            },*/
            createCriteriaByResId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            
            /*saveCriteriaByResIdCriId : function(resId, criId, name, description){
                return s_criteriaByResIdCriId.save({resId: resId, criId: criId, name: name, description: description});
            },*/
            saveCriteriaByResIdCriId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            
            /*removeCriteriaByResIdCriId : function(resId, criId){
                return r_criteriaByResIdCriId.remove({resId: resId, criId: criId});
            },*/
            removeCriteriaByResIdCriId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            
            findDatasetColsByResId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },


            /*createCriteriaDetailByResIdCriId : function(resId, criId, dataField, aggregateFunc, operator, target){
                return c_criteriaDetailByResIdCriId.create({resId: resId, criId: criId, dataField: dataField, aggregateFunc:aggregateFunc, operator:operator, target:target});
            }*/
            createCriteriaDetailByResIdCriId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            saveCriteriaDetailByResIdCriIdCond : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            removeCriteriaDetailByResIdCriIdCond : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            }
            
            /*findResDetailAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            create : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            save : function(url,param,omitAlerts) {
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url, param,options);
            }*/
        };

    });
    

    app.controller('ods706Controller', function($scope,ods706eService,cStateManager,cAlerter,userHolder,alerter,$window,cResource, cValidation, $modal) {
        
        function decimalValidation(value, scope) {
            if (!value) return true;
                
            if((parseFloat(value) == parseFloat(value,10)) && !isNaN(value)) {
                return true;
            } else {
                return false;
            }
        }

        cValidation.register('decimal', decimalValidation, '必須為數值');
        
        
        $scope.model = {};
        $scope.formula = {};
        var selectedRow = 0;
        var selectedFormulaRow = 0;
        var executeOnce = true;
        var sName = "";
        var sDesc = "";
        
        $scope.tabNum = 1;  //紀錄目前的tab頁面，不可刪
        

        
        
        //justin
        //window.opener.passId = 'F038E95C-0B0D-4E4B-8DB2-4EE48E2A21D6';
        

        var passId = $.getUrlVar('resId');
        
        var passCriteriaId = "";  //紀錄選取的criteriaId全域變數
        
        if(isEmptyOrUndefined(passId))
        {
            alerter.fatal('請由主檔頁面選擇一素材及案例');
            $scope.girdObject3 = {
                    multiSelect : false,
                    data : 'gridData3',
                    rowHeight : 40,
                    keepLastSelected: false,
                    enableColumnResize : true,
                    columnDefs : [ {
                        field : 'rowCount',
                        displayName : '項次'
                    }, {
                        field : 'name',
                        displayName : '方案名稱'
                    }, {
                        field : 'description',
                        displayName : '方案說明'
                    }, {
                        field : 'resourceId',
                        displayName : 'resourceId',
                        visible : true
                    } ]
            };
            return false;
        }

        
        var state = cStateManager([{ name: 'init', from: 'NONE',   to: 'INIT'  },
                      { name: 'query', from: ['INIT','RESETD','SELECTED'],   to: 'QUERYED'},
                      { name: 'reset', from: ['INIT','QUERYED'],   to: 'RESETD'},
                      { name: 'select', from: ['QUERYED'],   to: 'SELECTED'}]);

                     

         // 取得使用者物件
         userHolder.getUser().then(function(response) {
             console.log(response.data);
        
         }, function(response) {
             if (200 != response.status) {
                 alert("無法取得使用者物件!");
             }
         });
        
        
         state.init();
         $scope.gridData3 = [];
         $scope.gridData4 = [];

        
         $scope.init = function(){

             $scope.query("","",0,false);

         };
                 
         

         $scope.reset = function(){            
             $scope.model = {};
             $scope.girdObject3.selectAll(false);
             $scope.gridData4 = [];
             cAlerter.clear();
             state.reset();
             $scope.formula = {};
             selectedRow = 0;
             selectedFormulaRow = 0;
             executeOnce = true;
             sName = "";
             sDesc = "";
         };
           
        
         $scope.query = function(name, description, currentRow, omitAlerts){
             //$scope.model = {}; 
//             if($scope.tabNum == 1)  //tab頁面是1時，做頁面1的修改
//             {
                 //勿刪!!!用find會導致ie8抓cache的問題，導致在新增、修改後的查詢無法執行
                 /*ods706eService.findCriteriasByResId(passId,omitAlerts||false).then(function(response){
                     
                     if (response){
                         state.query();
                     }
                     
                     if (angular.isArray(response)){
                         $scope.gridData3 = response;
                     }else{
                         $scope.gridData3 = [response];
                     }
    
    
                 });*/
                 name = name||"";
                 description = description||"";
                 sName = name;
                 sDesc = description;
                 var result = ods706eService.findCriteriasByResId('rest/criterias',{resId :passId, name:name, description:description},omitAlerts||false);
                 result.then(function(response){
                     if (response != ""){
                         state.query();
                         executeOnce = true;
                         selectedRow = currentRow;
                     } else {
                         state.reset();
                     }
    
                     state.reset();
                     
                     if (angular.isArray(response)){
                         $scope.gridData3 = response;
                     }else{
                         $scope.gridData3 = [response];
                     }
                                          
                 });
//             } else {
                 /*
                 ods706eService.findCriteriaDetailByResIdCriId(passId, $scope.model.criteriaId).then(function(response){
                     if (response){
                         state.query();
                     }
                     
                     if (angular.isArray(response)){
                         $scope.gridData4 = response;
                     }else{
                         $scope.gridData4 = [response];
                     }
                 });*/
//                 var result = ods706eService.findCriteriaDetailByResIdCriId('rest/criteriaDetail',{resourceId :passId, criteriaId :passCriteriaId},omitAlerts||false);
//                 result.then(function(response){
//                     if (response){
//                         state.query();
//                     }
//    
//                     if (angular.isArray(response)){
//                         $scope.gridData4 = response;
//                     }else{
//                         $scope.gridData4 = [response];
//                     }
//                     
//                 });
//             }
             

         };
         
         $scope.queryFormula = function(omitAlerts){
                 var result = ods706eService.findCriteriaDetailByResIdCriId('rest/criteriaDetail',{resId :passId, criId :passCriteriaId},omitAlerts||false);
                 result.then(function(response){
                     if (response){
                         state.query();
                     }
    
                     if (angular.isArray(response)){
                         $scope.gridData4 = response;
                     }else{
                         $scope.gridData4 = [response];
                     }
                     
                 });
         };
         
         
         $scope.clickTab = function(tabNum){

             
             cAlerter.clear();
             
             $scope.tabNum = tabNum;    //將使用者所選取的頁面號碼紀錄
             
             //選到tab2時，查詢criteria細部內容
             if(tabNum == 2){
                 
                 $scope.gridData4 = []; //一開始grid內的資料先清除
                 
                 if (isEmptyOrUndefined($scope.model.id))
                 {
                     alerter.fatal('請由基本資料頁籤選擇方案後再進行欄位設定');
                     return false;
                 }
                 
                 $scope.query();
                 

                 //紀錄dataset的欄位，一個dataset只會有一種欄位，因此tab2點下後只需query一次，之後就不必再query
                 if(isEmptyOrUndefined($scope.datasetCols))
                 {
                     //或許可以考慮直接讀實體檔案...(雖然到時需考慮型態<又或許變成只需要存型態就好>，但至少不會都要把資料寫到db中)
                     var result = ods706eService.findDatasetColsByResId('rest/dataset_cols',{id :passId},false);
                     result.then(function(response){
                         if (angular.isArray(response)){
                             $scope.datasetCols = response;
                         }else{
                             $scope.datasetCols = [response];
                         }

                         
                     });
                     

                 }
                 
             } 
        };
        
        
        $scope.tabs = [{
            title: '基本資料',
            url: 'one.tpl.html',
            tabNum : 1
        }, {
            title: '欄位設定',
            url: 'two.tpl.html',
            tabNum : 2
        }/*, {
            title: 'Three',
            url: 'three.tpl.html',
            tabNum : 3
    }*/];

    $scope.currentTab = 'one.tpl.html';


    $scope.onClickTab = function (tab) {

        $scope.currentTab = tab.url;
        
       // alert(angular.toJson(tab.tabNum,true));

        
        //cAlerter.clear();
        
        $scope.tabNum = tab.tabNum;    //將使用者所選取的頁面號碼紀錄

        
        //選到tab2時，查詢criteria細部內容
//        if($scope.tabNum == 2){
//
//            
//            $scope.gridData4 = []; //一開始grid內的資料先清除
//            
//            if (isEmptyOrUndefined($scope.model.criteriaId))
//            {
//                alerter.fatal('請由基本資料頁籤選擇方案後再進行欄位設定');
//                return false;
//            }
//            
//            $scope.query();
//            
//
//            //紀錄dataset的欄位，一個dataset只會有一種欄位，因此tab2點下後只需query一次，之後就不必再query
//            if(isEmptyOrUndefined($scope.datasetCols))
//            {
//                //或許可以考慮直接讀實體檔案...(雖然到時需考慮型態<又或許變成只需要存型態就好>，但至少不會都要把資料寫到db中)
//                var result = ods706eService.findDatasetColsByResId('rest/dataset_cols',{id :passId},false);
//                result.then(function(response){
//                    if (angular.isArray(response)){
//                        $scope.datasetCols = response;
//                    }else{
//                        $scope.datasetCols = [response];
//                    }
//
//                    
//                });
//                
//
//            }
//            
//        } 
        

    };
    
    $scope.isActiveTab = function(tabUrl) {
        return tabUrl == $scope.currentTab;
    };

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!記得修改時要加condition，還有要排除當上面的msg出現時，html會錯誤!!!!!!!!!!!!!!
         
         $scope.create = function(omitAlerts){
            
//             if($scope.tabNum == 1)  //tab頁面是1時，做頁面1的新增
//             {
                 //if (tab1Check($scope.model.name)) {
                     /*var description = $scope.model.description;
                     if(isEmptyOrUndefined(description)){
                         description = "NULL";
                     }
                     ods706eService.createCriteriaByResId(passId, $scope.model.name, description).then(function(response){
                        $scope.query("",true);
                     }, function(response) {
                     });*/
                     $scope.model.resId = passId;
                     $scope.model.ods706eTab2FormBeanList = $scope.gridData4;
                     ods706eService.createCriteriaByResId('rest/create_criteria',
                             $scope.model,omitAlerts||false).then(function(response){
                                $scope.query(sName, sDesc, selectedRow, true);
                     }, function(response) {
                     });
//                     ods706eService.createCriteriaByResId('rest/create_criteria',
//                             {resId:passId, name:$scope.model.name, description:$scope.model.description},omitAlerts||false).then(function(response){
//                                $scope.query("",true);
//                     }, function(response) {
//                     });
                     
                 //} else {
                 //    alerter.fatal('請輸入方案名稱');
                 //}
//             } else {    //tab頁面是2時，做頁面2的新增
                 //if (tab2Check($scope.model.dataField, $scope.model.aggregateFunc, $scope.model.operator, $scope.model.target)) {

                     //ods706eService.createCriteriaDetailByResIdCriId(passId, $scope.model.criteriaId, $scope.model.dataField, $scope.model.aggregateFunc, $scope.model.operator, $scope.model.target).then(function(response){
//                     ods706eService.createCriteriaDetailByResIdCriId('rest/create_criteria_detail',
//                             {resId:passId, criId:passCriteriaId, dataField:$scope.model.dataField,aggregateFunc:$scope.model.aggregateFunc, operator:$scope.model.operator,
//                                  target:$scope.model.target},omitAlerts||false).then(function(response){
//
//                                      $scope.query("",true);
                            //
                        /* var result = ods706eService.findCriteriaDetailByResIdCriId('rest/criteriaDetail',{resourceId :passId, criteriaId :$scope.model.criteriaId},true);
                         result.then(function(response){
                             if (response){
                                 state.query();
                             }
            
                             if (angular.isArray(response)){
                                 $scope.gridData4 = response;
                             }else{
                                 $scope.gridData4 = [response];
                             }
                             
                         });*/
//                     }, function(response) {
//                     });
                 //} else {
                 //    alerter.fatal('請完整輸入表單內容');
                 //}
//             }

         };
         

         

         
         
         $scope.save = function(omitAlerts){
             
//             if($scope.tabNum == 1)  //tab頁面是1時，做頁面1的修改
//             {
                 //if (tab1Check($scope.model.name)) {
                     /*var description = $scope.model.description;
                     if(isEmptyOrUndefined(description)){
                         description = "NULL";
                     }
                     ods706eService.saveCriteriaByResIdCriId(passId, $scope.model.criteriaId, $scope.model.name, description).then(function(response){
                         $scope.query("",true);
                     }, function(response) {
                     });*/
                     $scope.model.resId = passId;
                     $scope.model.criId = $scope.model.id;
                     $scope.model.ods706eTab2FormBeanList = $scope.gridData4;
                     ods706eService.saveCriteriaByResIdCriId('rest/save_criteria',
                             $scope.model,omitAlerts||false).then(function(response){
                                 $scope.query(sName, sDesc, selectedRow, true);
                     }, function(response) {
                     });
//                     ods706eService.saveCriteriaByResIdCriId('rest/save_criteria',
//                             {resId:passId, criId:$scope.model.criteriaId, name:$scope.model.name, description:$scope.model.description},omitAlerts||false).then(function(response){
//                                $scope.query("",true);
//                     }, function(response) {
//                     });
                 //} else {
                 //   alerter.fatal('請輸入方案名稱');
                 //}
                
//             } else {    //tab頁面是2時，做頁面2的修改
                 //if (tab2Check($scope.model.dataField, $scope.model.aggregateFunc, $scope.model.operator, $scope.model.target)) {

                     //ods706eService.createCriteriaDetailByResIdCriId(passId, $scope.model.criteriaId, $scope.model.dataField, $scope.model.aggregateFunc, $scope.model.operator, $scope.model.target).then(function(response){
//                     ods706eService.saveCriteriaDetailByResIdCriIdCond('rest/save_criteria_detail',
//                             {resId:passId, criId:passCriteriaId, condition:$scope.model.condition, dataField:$scope.model.dataField,aggregateFunc:$scope.model.aggregateFunc, operator:$scope.model.operator,
//                                  target:$scope.model.target},omitAlerts||false).then(function(response){
//
//                                      $scope.query("",true);
//
//                     }, function(response) {
//                     });
                 //} else {
                 //    alerter.fatal('請完整輸入表單內容');
                 //}
                 
//             }

         };
         
         
         $scope.removeConfirm = function() {
             if (confirm("是否確認刪除？")) { 
                 return true; 
             } else {
                 return false; 
             }
         };
         
         $scope.remove = function(omitAlerts){
             
             if ($scope.removeConfirm()) {
//                 if($scope.tabNum == 1)  //tab頁面是1時，做頁面1的刪除
//                 {
                    /*ods706eService.removeCriteriaByResIdCriId(passId, $scope.model.id).then(function(response){
                        $scope.query(sName, sDesc, 0, true);
                     }, function(response) {
                     });*/
                    ods706eService.removeCriteriaByResIdCriId('rest/remove_criteria',
                            {resId:passId,criId:$scope.model.id},omitAlerts||false).then(function(response){
                                $scope.query(sName, sDesc, 0, true);
                    }, function(response) {
                    });
    
//                 } else {    //tab頁面是2時，做頁面2的刪除
                     //if (tab2Check($scope.model.dataField, $scope.model.aggregateFunc, $scope.model.operator, $scope.model.target)) {
    
                         //ods706eService.createCriteriaDetailByResIdCriId(passId, $scope.model.criteriaId, $scope.model.dataField, $scope.model.aggregateFunc, $scope.model.operator, $scope.model.target).then(function(response){
//                         ods706eService.removeCriteriaDetailByResIdCriIdCond('rest/remove_criteria_detail',
//                                 {resId:passId, criId:passCriteriaId, condition:$scope.model.condition},omitAlerts||false).then(function(response){
//    
//                                          $scope.query("",true);
//                                 
//    
//                         }, function(response) {
//                         });
                     //}
                     
//                 }
             }
 
         };
        
         
         
         $scope.myGird3Selections = [];
         $scope.girdObject3 = {
                 multiSelect : false,
                 data : 'gridData3',
                 rowHeight : 40,
                 //keepLastSelected: false,
                 keepLastSelected: false,
                 enableColumnResize : true,
                 columnDefs : [ {
                     field : 'rowCount',
                     displayName : '項次'
                 }, {
                     field : 'name',
                     displayName : '方案名稱'
                 }, {
                     field : 'description',
                     displayName : '方案說明'
                 }, {
                     field : 'id',
                     displayName : 'criteriaId',
                     visible : false
                 } ],


                 selectedItems: $scope.myGird3Selections,
                 
                 beforeSelectionChange: function(row) {

                     row.changed = true;
                     return true;
                   },
                 afterSelectionChange : function(row, event){
                     $scope.model = this.entity;
                     //alert(angular.toJson($scope.model,true));
                     if (executeOnce){
                         if(!angular.isUndefined($scope.model)) {                                                  
                             selectedFormulaRow = 0;
                             state.select();
                             passCriteriaId = $scope.model.id;
                             $scope.queryFormula(true);
                         }
                         executeOnce=false;
                     }
                     if (row.changed){
                         if(!angular.isUndefined($scope.model)) {   
                             console.log("deal with row " + row.rowIndex);
                             selectedFormulaRow = 0;
                             passCriteriaId = $scope.model.id;
                             state.select();
                             selectedRow = row.rowIndex;
                             
                             
                             //當grid未選到時，將model清空，如果不清的話，下面grid雖然被deselect，但上面的text改了後，下面的grid還是會跟著改變
                             /*if(isEmptyOrUndefined($scope.myGird3Selections))
                             {
    
                                 $scope.model = {};
                                 state.query();
                             }*/
                             
                         //alert(angular.toJson($scope.myGird3Selections,true));
                             $scope.queryFormula(true);
                         }
                         row.changed=false;
                     }
                     
                     
                 }
         
         };

         
//         $scope.$on('ngGridEventData', function(){
//             if($scope.tabNum == 1){  
//
//                 if (!isEmptyOrUndefined($scope.gridData3)){
//                     $scope.girdObject3.selectRow(0, true);
//                     //alert(angular.toJson($scope.myGird3Selections,true));
//                     if(!isEmptyOrUndefined($scope.myGird3Selections)){
//                         //alert(angular.toJson($scope.myGird3Selections,true));
//                         $scope.model = $scope.myGird3Selections[0];
//                         passCriteriaId = $scope.model.criteriaId;
//                         //alert(angular.toJson(passCriteriaId,true));
//                         state.select();
//                     }
//                 }  else {
//                        state.query();
//                 }
//             } else {
//                  if (!isEmptyOrUndefined($scope.gridData4)){
//                     $scope.girdObject4.selectRow(0, true);
//                     //alert(angular.toJson($scope.myGird3Selections,true));
//                     if(!isEmptyOrUndefined($scope.myGird4Selections)){
//                         //alert(angular.toJson($scope.myGird3Selections,true));
//                         $scope.model = $scope.myGird4Selections[0];
//                         //passCriteriaId = $scope.model.criteriaId;
//                         //alert(angular.toJson(passCriteriaId,true));
//                         state.select();
//                     }
//                 }  else {
//                        state.query();
//                 }
//             }
//         });

         
         
         
         
         
         $scope.aggregateFuncSel = function(aggregateFunc) {
             alert(aggregateFunc);
             
        };
         
        $scope.operatorSel = function(operator) {
            alert(operator);
            
       };
         
       $scope.removeFormula = function(idx){
           $scope.gridData4.splice(idx, 1);
       };
         
         
         $scope.myGird4Selections = [];
         $scope.girdObject4 = {
                 multiSelect : false,
                 data : 'gridData4',
                 rowHeight : 40,
                 //keepLastSelected: false,
                 keepLastSelected: false,
                 enableColumnResize : true,
                 columnDefs : [ {
                     field : '',
                     displayName : '項次',
                     cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
                 }, {
                     field : 'condition',
                     displayName : '方案編號',
                     visible : false
                 }, {
                     field : 'dataField',
                     displayName : '欄位'
                 }, {
                     field : 'aggregateFunc',
                     displayName : '聚合函數'
                 }, {
                     field : 'operator',
                     displayName : '關係運算子'
                 }, {
                     field : 'target',
                     displayName : '目標值'
                 }, {
                     field : 'option',
                     displayName : '功能',
                     cellTemplate: '<button type="button" class="btn btn-primary btn-sm" data-ng-click="openModifyFormula(row.rowIndex)"><span class="glyphicon glyphicon-pencil"></span></button> <button type="button" class="btn btn-danger btn-sm" data-ng-click="removeFormula(row.rowIndex)"><span class="glyphicon glyphicon-remove"></span></button>'
                 } ],


                 selectedItems: $scope.myGird4Selections,
                 
                 beforeSelectionChange: function(row) {

                     row.changed = true;
                     return true;
                   },
                 afterSelectionChange : function(row, event){
                     $scope.formula = this.entity;
                     if (row.changed){
                         console.log("deal with row " + row.rowIndex);
                         row.changed=false;
                         //alert(angular.toJson($scope.formula,true));
                         selectedFormulaRow = row.rowIndex;                         
                         
                         //當grid未選到時，將model清空，如果不清的話，下面grid雖然被deselect，但上面的text改了後，下面的grid還是會跟著改變
                         /*if(isEmptyOrUndefined($scope.myGird4Selections))
                         {

                             $scope.model = {};
                             state.query();
                         }*/
                         
                     //alert(angular.toJson($scope.model,true));
                     }
                     
                     
                 }
         
         };
         
         $scope.gridList=[$scope.girdObject3,$scope.girdObject4];
         
         $scope.$on('ngGridEventData', function(e, gridId){
             var filteredGrid=$scope.gridList.filter(function(grid){
                 return grid.gridId==gridId;
               });
              $scope.filteredGrid=filteredGrid[0];
              $scope.filteredGrid.selectRow(selectedRow, true);
              //grid data
              //$scope.filteredGridData=$scope.filteredGrid.ngGrid.data;
         });
         
       //tab2 設定
         //預設顯示第一個 Tab
         var _showTab = 0;
         var $defaultLi = $('ul.tabs li').eq(_showTab).addClass('active');
//         $($defaultLi.find('a').attr('href')).siblings().hide();
         $($defaultLi.find('a').attr('href')).siblings().css({"position":"absolute","top":"-2000px"});
         
         // 當 li 頁籤被點擊時...
         // 若要改成滑鼠移到 li 頁籤就切換時, 把 click 改成 mouseover
         $('ul.tabs li').click(function() {
             // 找出 li 中的超連結 href(#id)
             var $this = $(this),
                 _clickTab = $this.find('a').attr('href');
             // 把目前點擊到的 li 頁籤加上 .active
             // 並把兄弟元素中有 .active 的都移除 class
             $this.addClass('active').siblings('.active').removeClass('active');
             // 淡入相對應的內容並隱藏兄弟元素
             //$(_clickTab).stop(false, true).fadeIn().siblings().hide();
             $(_clickTab).stop(false, true).fadeIn().css("position","static").siblings().css({"position":"absolute","top":"-2000px"});

             return false;
         }).find('a').focus(function(){
             this.blur();
         });

         //dialog Add
         $scope.addFormulaDialogOptions = {
                 templateUrl: 'ods706e_addFormula.html',
                 resolve: {passId: function(){ return angular.copy(passId);} },
                 controller: 'AddFormulaController'
             };
             
         $scope.openAddFormula = function() {
             var modalInstance = $modal.open($scope.addFormulaDialogOptions);            
             modalInstance.result.then(function(result){                
                 if (result.status == 'add') {
                     $scope.gridData4.push(result.formula);
                 }
             });
         };
         
       //dialog Modify
         $scope.modifyFormulaDialogOptions = {
                 templateUrl: 'ods706e_modifyFormula.html',
                 resolve: {formula: function(){ return angular.copy($scope.formula);}, passId: function(){ return angular.copy(passId);} },
                 controller: 'ModifyFormulaController'
             };
             
         $scope.openModifyFormula = function(idx) {
             $scope.formula = $scope.gridData4[idx];
             var modalInstance = $modal.open($scope.modifyFormulaDialogOptions);            
             modalInstance.result.then(function(result){
                 if (result.status == 'modify') {
                     //alert(selectedFormulaRow);
                     $scope.gridData4[idx].dataField = result.formula.dataField;
                     $scope.gridData4[idx].aggregateFunc = result.formula.aggregateFunc;
                     $scope.gridData4[idx].operator = result.formula.operator;
                     $scope.gridData4[idx].target = result.formula.target;                         
                     //alert(angular.toJson($scope.gridData4,true));
                 }
             });
         };
     
    });
    
    
    
    
   //dialog Add
    
    app.controller('AddFormulaController', function($scope,ods706eService,$modalInstance,passId) {
        //alert(passId);
        $scope.formula = {};
        //alert(angular.toJson($scope.formula,true));
        $scope.datasetCols = [];
        
        $scope.queryDatasetCols = function(omitAlerts){
            var result = ods706eService.findDatasetColsByResId('rest/dataset_cols',{id :passId},false);
            result.then(function(response){
                if (angular.isArray(response)){
                    $scope.datasetCols = response;
                }else{
                    $scope.datasetCols = [response];
                }

                
            });
        };
        //起始查詢datasetCols
        $scope.queryDatasetCols();
                
        $scope.addFormula = function(){
            var result = {status:'add', formula: $scope.formula};
            $modalInstance.close(result);
        };        
    });
    
    
    //dialog Modify
    
    app.controller('ModifyFormulaController', function($scope,ods706eService,$modalInstance,formula, passId) {
        //alert(passId);
        $scope.formula = formula;
        //alert(angular.toJson($scope.formula,true));
        $scope.datasetCols = [];
        
        $scope.queryDatasetCols = function(omitAlerts){
            var result = ods706eService.findDatasetColsByResId('rest/dataset_cols',{id :passId},false);
            result.then(function(response){
                if (angular.isArray(response)){
                    $scope.datasetCols = response;
                }else{
                    $scope.datasetCols = [response];
                }

                
            });
        };
        //起始查詢datasetCols
        $scope.queryDatasetCols();
        
        $scope.modifyFormula = function(){
            var result = {status:'modify', formula: $scope.formula};
            $modalInstance.close(result);
        }; 
        
    });


})();



$.extend({
    getUrlVars : function() {
        var vars = [], hash;
        var hashes = window.location.href.slice(
                window.location.href.indexOf('?') + 1).split('&');
        for ( var i = 0; i < hashes.length; i++) {
            hash = hashes[i].split('=');
            vars.push(hash[0]);
            vars[hash[0]] = hash[1];
        }
        return vars;
    },
    getUrlVar : function(name) {
        return $.getUrlVars()[name];
    }
});



function getReadableFileSizeString(fileSizeInBytes) {

    var i = -1;
    var byteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
    do {
        fileSizeInBytes = fileSizeInBytes / 1024;
        i++;
    } while (fileSizeInBytes > 1024);

    return Math.max(fileSizeInBytes, 0.1).toFixed(1) + byteUnits[i];
};

function getToday() {
    var d=new Date();
    var year=d.getFullYear();
    var month=d.getMonth()+1;
    if (month<10){
    month="0" + month;
    };
    var day=d.getDate();
    year = year + 1911;
    return year + "/" + month + "/" + day;
};

function getTomorrow() {
    var d=new Date();
    var year=d.getFullYear();
    var month=d.getMonth()+1;
    if (month<10){
    month="0" + month;
    };
    var day=d.getDate()+1;
    year = year + 1911;
    return year + "/" + month + "/" + day;
};

function getFileExtension(filename) {
    //alert((/[.]/.exec(filename)) ? /[^.]+$/.exec(filename) : undefined);
    return (/[.]/.exec(filename)) ? /[^.]+$/.exec(filename) : undefined;
};

function isEmptyOrUndefined(str) {
    return str == '' || angular.isUndefined(str) || str == null;
}

function tab1Check(name){
    var ischeck = true;

    if (isEmptyOrUndefined(name)) {
        ischeck = false;
        return;
    }
    return ischeck;
};

function tab2Check(dataField, aggregateFunc, operator, target){
    var ischeck = true;

    if (isEmptyOrUndefined(dataField) || isEmptyOrUndefined(aggregateFunc) || isEmptyOrUndefined(operator) || isEmptyOrUndefined(target) ) {
        ischeck = false;
        return;
    }
    return ischeck;
};
