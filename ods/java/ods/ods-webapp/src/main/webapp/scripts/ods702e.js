(function() {
    var app = angular.module("ods702eApp", ['slsCommonModule']);
    app.factory('ods702eService',function(cResource){

        var resource = cResource('rest/category/:resId',{resId :"@resId"});
        //var resource = cResource('rest/:ped',{ped :"@ped"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {
            /*save : function(model){
                return resource.save(model); // post
            },
            remove : function(bind){
                return resource.remove(bind); // delete
            },
            find : function (model,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                if (model.ped){
                    return resource.get(model,options);
                }else{
                    return resource.find(model,options);
                }
            },
            findAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            create : function(model){
                return resource.create(model); // put
            },*/
            
            findCategoryByName : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            
            findResourceByCategoryId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            findAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url,param,options);
            },
            createCategoryByNameDescGrid1 : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            saveCategoryByNameDescGrid1 : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            deleteCategoryByCategoryId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            }
        };

    });

    app.controller('ods702eController', function($scope,ods702eService,cStateManager,cAlerter,$window,$modal,alerter,userHolder) {
        var state = cStateManager([{ name: 'init', from: 'NONE',   to: 'INIT'  },
                      { name: 'query', from: ['INIT','RESETD','SELECTED'],   to: 'QUERYED'},
                      { name: 'reset', from: ['INIT','QUERYED','SELECTED'],   to: 'RESETD'},
                      { name: 'select', from: ['QUERYED'],   to: 'SELECTED'}]);

        // 取得使用者物件
        userHolder.getUser().then(function(response) {
            console.log(response.data);

        }, function(response) {
            if (200 != response.status) {
                alert("無法取得使用者物件!");
            }
        });

        $scope.model = {}; 
        state.init();
        $scope.gridData1 = [];
        $scope.gridData2 = [];
        var executeOnce = true;
        var sName = "";
        var sDesc = "";
        var selectedRow = 0;

        
        $scope.save = function(omitAlerts){

            if (createCheck($scope.model.categoryName)) {
                ods702eService.saveCategoryByNameDescGrid1('rest/save_category',
                        {categoryId:$scope.model.categoryId, categoryName:$scope.model.categoryName, categoryDescription:$scope.model.categoryDescription, grid1:$scope.gridData1},omitAlerts||false).then(function(response){

                            //$scope.query($scope.model.categoryName,true);
                            //$scope.query(sName, sDesc, selectedRow, true);
                            //$scope.model.categoryId = response;
                            //$scope.findResourceByCategoryId(response,true);

                }, function(response) {
                });
            } else {
                alerter.fatal('請輸入分類名稱');
            }

        };
        $scope.removeConfirm = function() {
            if (confirm("是否確認刪除？")) { 
                return true; 
            } else {
                return false; 
            }
        };
        $scope.remove = function(omitAlerts){
            if (createCheck($scope.model.categoryId)) {
                if ($scope.removeConfirm()) {
                    ods702eService.deleteCategoryByCategoryId('rest/delete_category',
                            {categoryId:$scope.model.categoryId},omitAlerts||false).then(function(response){
    
                                     //$scope.query("",true);
                                     $scope.model = {};
                                     $scope.query(sName, sDesc, 0, true);
                                     //$scope.model.categoryId = "";
    
                    }, function(response) {
                    });
                }
            } else {
                alerter.fatal('請選擇一分類');
            }
        };

        $scope.create = function(omitAlerts){

            if (createCheck($scope.model.categoryName)) {
                ods702eService.createCategoryByNameDescGrid1('rest/create_category',
                        {categoryName:$scope.model.categoryName, categoryDescription:$scope.model.categoryDescription, grid1:$scope.gridData1},omitAlerts||false).then(function(response){

                                 //$scope.query($scope.model.categoryName,true);
                                 $scope.query(sName, sDesc, selectedRow, true);
                                 $scope.model.categoryId = response;
                                 $scope.findResourceByCategoryId(response,true);
                                 

                }, function(response) {
                });
            } else {
                alerter.fatal('請輸入分類名稱');
            }

        };
        $scope.query = function(name, description, currentRow, omitAlerts){
            $scope.gridData1 = []; //qry分類grid時，先將素材及案例grid清空

            
            //$scope.model = {}; 
            //alert(angular.toJson($scope.model.name,true));
            name = name||"";
            description = description||"";
            sName = name;
            sDesc = description;
            var result = ods702eService.findCategoryByName('rest/category_name',{name:name, description:description},omitAlerts||false);
            result.then(function(response){
                if (response != ""){
                    state.query();
                    executeOnce = true;
                    selectedRow = currentRow;
                } else {
                    state.reset();
                }

                if (angular.isArray(response)){
                    $scope.gridData2 = response;
                }else{
                    $scope.gridData2 = [response];
                }
            });
        };
        $scope.reset = function(){
            $scope.model = {};
            $scope.gridData1 = [];
            $scope.gridData2 = [];
            cAlerter.clear();
            state.reset();
            executeOnce = true;
            sName = "";
            sDesc = "";
            selectedRow = 0;
        };

        
        $scope.myGird2Selections = [];
        $scope.girdObject2 = {
                multiSelect : false,
                data : 'gridData2',
                rowHeight : 40,
                keepLastSelected: false,
                enableColumnResize : true,
                columnDefs : [ {
                    field : 'categoryRowCount',
                    displayName : '序號'
                }, {
                    field : 'categoryName',
                    displayName : '分類名稱'
                }, {
                    field : 'categoryDescription',
                    displayName : '分類說明'
                }, {
                    field : 'categoryId',
                    displayName : 'categoryId',
                    visible : false
                } ],
                
                selectedItems: $scope.myGird3Selections,
                
                beforeSelectionChange: function(row) {
                    row.changed = true;
                    return true;
                  },
                afterSelectionChange : function(row, event){
                    $scope.model = this.entity;
                    if (executeOnce){                    
                        state.select();
                        $scope.findResourceByCategoryId($scope.model.categoryId, true);
                        executeOnce=false;
                    }
                    if (row.changed){
                        console.log("deal with row " + row.rowIndex);
                        row.changed=false;
                        
                        //$scope.model = this.entity;

                        state.select();
                        $scope.findResourceByCategoryId($scope.model.categoryId, true);
                        selectedRow = row.rowIndex;
                    }

                }
        };
        
        $scope.findResourceByCategoryId = function(categoryId , omitAlerts){
            //$scope.model = {}; 

            //alert(angular.toJson($scope.model.name,true));
            var result = ods702eService.findResourceByCategoryId('rest/resource_by_category_id',{categoryId :categoryId},omitAlerts||false);
            result.then(function(response){
                /*if (response){
                    state.query();
                }*/

                if (angular.isArray(response)){
                    $scope.gridData1 = response;
                }else{
                    $scope.gridData1 = [response];
                }
            });
        };
        
        $scope.removeResource = function(idx){
            $scope.gridData1.splice(idx, 1);
        };
        
        $scope.resourceDetail = function(idx){
            //$scope.gridData1.splice(idx, 1);
            //alert($scope.gridData1[idx].resCatResourceId);
            
            $window.open("../ODS701E/ods701e_05.html", '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');

            $window.passId=$scope.gridData1[idx].resCatResourceId;
            $window.passWbkId=$scope.gridData1[idx].resCatWorkbookId;
            $window.passFormat=$scope.gridData1[idx].resCatFormat;
            $window.passIsUnloadRefresh=false;
            
        };
        
        
        // grid definition and data
        $scope.girdObject1 = {
            multiSelect : false,
            data : 'gridData1',
            rowHeight : 40,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                field : '',
                displayName : '序號',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'resCatName',
                displayName : '素材名稱'
            }, {
                field : 'resCatDescription',
                displayName : '素材說明'
            }, {
                field : 'resCatResourceId',
                displayName : 'ResourceId',
                visible : false
            }, {
                field : 'resCatWorkbookId',
                displayName : 'WorkbookId',
                visible : false
            }, {
                field : 'resCatFormat',
                displayName : 'Format',
                visible : false
            }, {
                field : 'option',
                displayName : '功能',
                cellTemplate: '<button type="button" class="btn btn-danger btn-sm" data-ng-click="removeResource(row.rowIndex)"><span class="glyphicon glyphicon-remove"></span></button> <button type="button" class="btn btn-success btn-sm" data-ng-click="resourceDetail(row.rowIndex)"><span class="glyphicon glyphicon-search"></span></button>'
            } ],
            afterSelectionChange : function(){
                //$scope.model = this.entity;
                //state.select();
            }
        };


//        $scope.$on('ngGridEventData', function(){
//            if ($scope.gridData1){
//                $scope.girdObject1.selectRow(0, true);
//            }
//        });
//        $scope.$on('ngGridEventData', function(){
//            if ($scope.gridData2){
//                $scope.girdObject2.selectRow(0, true);
//            }
//        });
        
        $scope.gridList=[$scope.girdObject1,$scope.girdObject2];
        
        $scope.$on('ngGridEventData', function(e, gridId){
            var filteredGrid=$scope.gridList.filter(function(grid){
                return grid.gridId==gridId;
              });
             $scope.filteredGrid=filteredGrid[0];
             $scope.filteredGrid.selectRow(selectedRow, true);
             //grid data
             //$scope.filteredGridData=$scope.filteredGrid.ngGrid.data;
        });
        
        $scope.openChoseResource = function() {
            var modalInstance = $modal.open(
                    {
                        templateUrl: 'ods702e_choseResource.html',
                        resolve: {category: function(){ return angular.copy($scope.category);}, resources: function(){ return angular.copy($scope.gridData1);}}, 
                        controller: 'ChoseResourceController'
                      }
            );            
            modalInstance.result.then(function(result){                
                if (result.status == 'add') {   
                    //alert(angular.toJson(result.resources,true));
                    $scope.gridData1 = result.resources;
                    /*alert(angular.toJson($scope.gridData1,true));
                    for (var i=0; i<$scope.gridData1.length; i++) {
                        $scope.gridData1[i].resourceVer = $scope.gridData1[i].resourceVer ? $scope.gridData1[i].resourceVer : 0;
                        $scope.gridData1[i].ver = $scope.gridData1[i].ver ? $scope.gridData1[i].ver : 0;
                    }*/
                }
            });
        };
        
         
    });
    
    
    //dialog
    app.controller('ChoseResourceController', function($scope,ods702eService,$modalInstance,category,resources) {        
        $scope.resource = [];                
        $scope.category = category;
        
        $scope.selectedResources = resources;
        $scope.chooseResources = [];
        
        $scope.queryResource = function(omitAlerts){
            $scope.chooseResources.length = 0;   //查詢時，令小視窗選到的resource都reset
            resCatName = $scope.resource.resCatName ? $scope.resource.resCatName :"";
            
            selectedResList = "";
            for (var i=0; i < $scope.selectedResources.length; i++) {
                selectedResList = selectedResList + $scope.selectedResources[i].resCatResourceId + ",";
            }
            
            var result = ods702eService.findAll('rest/find/un_category_resource',{name:resCatName, selectedResList: selectedResList},true);
            result.then(function(response){
                if (angular.isArray(response)) {
                    $scope.resourceGridData = response;
                } else {
                    $scope.resourceGridData = [response];
                }
            });
        };
        //起始列出所有resource
        $scope.queryResource();       
                
        $scope.addResource = function(){
            for(i = 0; i< $scope.chooseResources.length; i++){ //小視窗選到的reousrce加回大視窗中
                $scope.selectedResources.push($scope.chooseResources[i]);
            }
            var result = {status:'add', resources: $scope.selectedResources};
            $modalInstance.close(result);
        };

        
        $scope.choseResourceGird = {
            multiSelect : true,
            data : 'resourceGridData',
            rowHeight : 40,
            selectedItems:$scope.chooseResources,
            columnDefs : [ {
                field : '',
                displayName : '序號',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'resCatResourceId',
                displayName : '素材及案例ID',
                visible : false
            }, {
                field : 'resCatName',
                displayName : '素材及案例名稱'
            }, {
                field : 'resCatDescription',
                displayName : '素材及案例說明'
            }, {
                field : 'resCatFormat',
                displayName : '素材及案例格式',
                visible : false
            }, {
                field : '',
                displayName : '圖示',
                cellTemplate: '<div ng-if="row.entity.resCatFormat == \'image\'"><img src="/ods/ODS308E/public/resource/{{row.getProperty(\'resCatResourceId\')}}/image/{{row.getProperty(\'resCatResourceId\')}}-0.png"  style="width:60px;heigth:60px"></div><div ng-if="row.entity.resCatFormat != \'image\'"><img src="../images/{{row.entity.resCatFormat}}.png"}"  style="width:60px;heigth:60px"></div>'                    
            } ],
            afterSelectionChange : function(){
                //$scope.resource = angular.copy(this.entity);
            }
        };
    });
})();

function isEmptyOrUndefined(str) {
    return str == '' || angular.isUndefined(str) || str == null;
}

function createCheck(name){
    var ischeck = true;

    if (isEmptyOrUndefined(name)) {
        ischeck = false;
        return;
    }
    return ischeck;
};

