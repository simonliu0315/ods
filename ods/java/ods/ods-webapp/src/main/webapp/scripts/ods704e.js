(function() {
    var app = angular.module("ods704eApp", ['slsCommonModule', 'angularFileUpload']);
    app.factory('ods704eService',function(cResource){
        var resource = cResource('rest/:ped',{ped :"@ped"});
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
            }*/
            
            findIdentities : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            findGroupByName : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            findPackageByGroupId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            findIndentityByGroupId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            findAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url,param,options);
            },
            saveGroupByNameDescIdtIdLstPkgIdLst : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url,param,options);
            },
            deleteGroupByGroupId : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url,param,options);
            }
        };

    });

    app.controller('ods704eController', function($scope,ods704eService,cStateManager,$modal,cAlerter,userHolder,cResource,alerter) {
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
        
        $scope.chooseIdentities = {};

        
        //一進入就直接查詢分眾推廣群
        var result = ods704eService.findIdentities('rest/identities',{},false);
        result.then(function(response){

            if (angular.isArray(response)){
                $scope.identities = response;
            }else{
                $scope.identities = [response];
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

        
        $scope.aaa = function(){
            //alert(angular.toJson($scope.identities,true));
            //alert(angular.toJson($scope.chooseIdentities,true));
            
            angular.forEach($scope.chooseIdentities, function (value, index) {
                //console.log(index, value);
                //if (value) $scope.shirts[index].colors.push($scope.colors[index2]);
                if (value) alert(index);
              });
        };
        
        
        
        $scope.save = function(){
            if (createCheck($scope.model.name)) {
                chkIdentityIdList = "";
                //找出目前被勾選的分眾推廣群id
                angular.forEach($scope.chooseIdentities, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    if (value) chkIdentityIdList = chkIdentityIdList + index + ",";
                  });
                
                selPackageIdList = "";
                //找出目前被選擇的主題id
                angular.forEach($scope.gridData1, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    //if (value) selPackageIdList = selPackageIdList + index + ",";
                    //alert(angular.toJson(value.pkgGupPackageId,true));
                    //alert(index);
                    selPackageIdList = selPackageIdList + value.pkgGupPackageId + ",";
                  });
                

                
                
                if ($file != "") {
                    var re = /\.(jpg|jpeg|gif|png)$/i;  //允許的圖片副檔名
                    if (!re.test($file.name)) {
                        alerter.fatal('只允許上傳JPG/JPEG/PNG/GIF影像檔，謝謝！');
                    } else {
                        cResource().uploadFile({
                            url: 'rest/saveGroupImage',
                            data: {id:$scope.model.id, name:$scope.model.name, description:$scope.model.description,chkIdentityIdList:chkIdentityIdList,selPackageIdList:selPackageIdList},
                            file: $file
                            
                        }).then(function(response){
                           //$("#uploadLocalFile").val("");
                            $scope.files = "";
                            $scope.query(sName, sDesc, selectedRow, true);
                         }, function(response) {
                        });
                    }
                    
                } else {
                    ods704eService.saveGroupByNameDescIdtIdLstPkgIdLst('rest/save_group',
                            {id:$scope.model.id, name:$scope.model.name, description:$scope.model.description,chkIdentityIdList:chkIdentityIdList,selPackageIdList:selPackageIdList},false ).then(function(response){

                                //$scope.query($scope.model.name,true);
//                                $scope.query(sName, sDesc, selectedRow, true);
//                                $scope.model.id = response;
//                                $scope.findIndentityByGroupId(response,true);
//                                $scope.findPackageByGroupId(response,true);
                                

                    }, function(response) {
                    });
                }
                
                
                
            } else {
                alerter.fatal('請輸入主題群組名稱');
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
            if (createCheck($scope.model.id)) {
                if ($scope.removeConfirm()) {
                    ods704eService.deleteGroupByGroupId('rest/delete_group',
                            {id:$scope.model.id},omitAlerts||false).then(function(response){
    
                                     //$scope.query("",true);
                                     //$scope.model.id = "";
                                     $scope.model = {};
                                     $scope.query(sName, sDesc, 0, true);
                                     
    
                    }, function(response) {
                    });
                }
            } else {
                alerter.fatal('請選擇一主題群組');
            }
        };

        
        var $file = "";
        
        //catch file object
        $scope.onFileSelect = function($files) {
            
            if (!$files || $files.length == 0) {
                $scope.files = null;
                return;
            }
            $scope.files = $files;
            
            $file = $files[0];
            //alert($file.name);
        };
        
        
        $scope.create = function(){

            
            if(createCheck($scope.model.name)){
                chkIdentityIdList = "";
                //找出目前被勾選的分眾推廣群id
                angular.forEach($scope.chooseIdentities, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    if (value) chkIdentityIdList = chkIdentityIdList + index + ",";
                  });
                
                selPackageIdList = "";
                //找出目前被選擇的主題id
                angular.forEach($scope.gridData1, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    //if (value) selPackageIdList = selPackageIdList + index + ",";
                    //alert(angular.toJson(value.pkgGupPackageId,true));
                    //alert(index);
                    selPackageIdList = selPackageIdList + value.pkgGupPackageId + ",";
                  });

                if ($file != "") {
                    var re = /\.(jpg|jpeg|gif|png)$/i;  //允許的圖片副檔名
                    if (!re.test($file.name)) {
                        alerter.fatal('只允許上傳JPG/JPEG/PNG/GIF影像檔，謝謝！');
                    } else {  
                        cResource().uploadFile({
                            url: 'rest/createAndUpload',
                            data: {name:$scope.model.name, description:$scope.model.description,chkIdentityIdList:chkIdentityIdList,selPackageIdList:selPackageIdList},
                            file: $file
                            
                        /*}).success(function(status, headers, config) {
                             // 成功 Callback.
                            //$scope.showFlag = true;
                            //$scope.progress = $scope.progress + ' ';
                            $("#uploadLocalFile").val("");
                            $scope.query(sName, sDesc, selectedRow, true);
                            //$scope.query("", true);                        
                        });*/
                        }).then(function(response){
                        //$("#uploadLocalFile").val("");
                            $scope.files = "";
                         $scope.query(sName, sDesc, selectedRow, true);
                          }, function(response) {
                         });
                    }
                    
                } else {
                    alerter.fatal('請選擇主題群組圖示');
                }
            } else {
                alerter.fatal('請輸入主題群組名稱');
            }

        };
        

        
        
        $scope.query = function(name, description, currentRow, omitAlerts){
            $scope.gridData1 = []; //qry主題grid時，先將素材及案例grid清空
            $scope.chooseIdentities = {}; //qry時，先將分眾推廣群的所有check清空

            
            //$scope.model = {}; 
            //alert(angular.toJson($scope.model.name,true));
            name = name||"";
            description = description||"";
            sName = name;
            sDesc = description;
            var result = ods704eService.findGroupByName('rest/group_name',{name:name, description:description},omitAlerts||false);
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
                
                //在查詢後取得一亂數，用來給予grid的image參數，迫使瀏覽器抓取新資料
                $scope.getDynamicNum = new Date().getTime();
            });
        };
        
        
        $scope.reset = function(){
            $scope.model = {};
            $scope.gridData1 = [];
            $scope.gridData2 = [];
            $scope.chooseIdentities = {};
            cAlerter.clear();
            state.reset();
            executeOnce = true;
            sName = "";
            sDesc = "";
            selectedRow = 0;
        };



        
        
        $scope.girdObject2 = {
            multiSelect : false,
            data : 'gridData2',
            rowHeight : 60,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                field : '',
                displayName : '序號',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'name',
                displayName : '主題群組名稱'
            }, {
                field : 'description',
                displayName : '主題群組說明'
            }, {
                field : 'imageUrl',
                displayName : 'imageUrl',
                visible : false
            }, {
                field : '',
                displayName : '圖示',
                cellTemplate: '<div><img src="/ods/ODS308E/public/group/{{row.getProperty(\'id\')}}/image/{{row.getProperty(\'imageUrl\')}}?t={{getDynamicNum}}"  style="width:60px;heigth:60px"></div>'                    
            }, {
                field : 'id',
                displayName : 'groupId',
                visible : false
            } ],
            beforeSelectionChange: function(row) {
                row.changed = true;
                return true;
              },
            afterSelectionChange : function(row, event){
                $scope.model = this.entity;
                if (executeOnce){                    
                    state.select();
                    $scope.findPackageByGroupId($scope.model.id, true);                    
                    $scope.findIndentityByGroupId($scope.model.id, true);
                    executeOnce=false;
                }
                if (row.changed){
                    console.log("deal with row " + row.rowIndex);
                    row.changed=false;
                    
                    //$scope.model = this.entity;

                    state.select();
                    $scope.findPackageByGroupId($scope.model.id, true);
                    
                    $scope.findIndentityByGroupId($scope.model.id, true);
                    
                    selectedRow = row.rowIndex;
                }

            }
              
        };
        
        $scope.findIndentityByGroupId = function(groupId , omitAlerts){
            $scope.chooseIdentities = {}; //查詢勾勾前，先將所有的勾勾都清除

            var result = ods704eService.findIndentityByGroupId('rest/identity_by_group_id',{groupId :groupId},omitAlerts||false);
            result.then(function(response){


                angular.forEach(response, function (item) {
                    //alert(angular.toJson(item,true));
                    //$scope.chooseIdentities["54F7DD1B-BE08-49DA-8EE5-43FA557D1327"] = true;
                    $scope.chooseIdentities[item.id.identityId] = true; //將查出來的identity entity，抓出其id，勾選所對應的框框
                });
                

            });
        };
        
        $scope.findPackageByGroupId = function(groupId , omitAlerts){
            //$scope.model = {}; 

            //alert(angular.toJson($scope.model.name,true));
            var result = ods704eService.findPackageByGroupId('rest/package_by_group_id',{groupId :groupId},omitAlerts||false);
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

        $scope.removePackage = function(idx){
            $scope.gridData1.splice(idx, 1);
        };

        $scope.girdObject1 = {
                multiSelect : false,
                data : 'gridData1',
                rowHeight : 60,
                keepLastSelected: false,
                enableColumnResize : true,
                columnDefs : [ {
                    field : '',
                    displayName : '序號',
                    cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
                }, {
                    field : 'pkgGupName',
                    displayName : '主題名稱'
                }, {
                    field : 'pkgGupDescription',
                    displayName : '主題說明'
                }, {
                    field : 'pkgGupPackageId',
                    displayName : 'packageId',
                    visible : false
                }, {
                    field : 'pkgGupImageUrl',
                    displayName : 'imageurl',
                    visible : false
                }, {
                    field : 'image',
                    displayName : '圖示',
                    cellTemplate: '<div><img src="/ods/ODS308E/public/package/{{row.getProperty(\'pkgGupPackageId\')}}/image/{{row.getProperty(\'pkgGupImageUrl\')}}"  style="width:60px;heigth:60px"></div>'
                }, {
                    field : 'option',
                    displayName : '功能',
                    cellTemplate: '<button type="button" class="btn btn-danger btn-sm" data-ng-click="removePackage(row.rowIndex)"><span class="glyphicon glyphicon-remove"></span></button>'
                } ],
                afterSelectionChange : function(){
//                    $scope.model = this.entity;
                }
            };
        
        
//        $scope.$on('ngGridEventData', function(){
//            if ($scope.gridData1){
//                $scope.girdObject1.selectRow(0, true);
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
        
        
        $scope.openChosePackage = function() {
            var modalInstance = $modal.open(
                    {
                        templateUrl: 'ods704e_chosePackage.html',
                        resolve: {group: function(){ return angular.copy($scope.group);}, packages: function(){ return angular.copy($scope.gridData1);}}, 
                        controller: 'ChosePackageController'
                      }
            );            
            modalInstance.result.then(function(result){                
                if (result.status == 'add') {   
                    //alert(angular.toJson(result.resources,true));
                    $scope.gridData1 = result.packages;
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
    app.controller('ChosePackageController', function($scope,ods704eService,$modalInstance,group,packages) {        
        $scope.pkg = [];                
        $scope.group = group;
        
        $scope.selectedPackages = packages;
        $scope.choosePackages = [];
        
        $scope.queryPackage = function(omitAlerts){
            $scope.choosePackages.length = 0;   //查詢時，令小視窗選到的resource都reset
            pkgGupName = $scope.pkg.pkgGupName ? $scope.pkg.pkgGupName :"";
            
            selectedPkgList = "";
            for (var i=0; i < $scope.selectedPackages.length; i++) {
                selectedPkgList = selectedPkgList + $scope.selectedPackages[i].pkgGupPackageId + ",";
            }

            var result = ods704eService.findAll('rest/find/un_group_package',{name:pkgGupName, selectedPkgList: selectedPkgList},true);
            result.then(function(response){
                if (angular.isArray(response)) {
                    $scope.packageGridData = response;
                } else {
                    $scope.packageGridData = [response];
                }
            });
        };
        //起始列出所有resource
        $scope.queryPackage();       
                
        $scope.addPackage = function(){
            for(i = 0; i< $scope.choosePackages.length; i++){ //小視窗選到的package加回大視窗中
                $scope.selectedPackages.push($scope.choosePackages[i]);
            }
            var result = {status:'add', packages: $scope.selectedPackages};
            $modalInstance.close(result);
        };

        $scope.chosePackageGird = {
            multiSelect : true,
            data : 'packageGridData',
            rowHeight : 60,
            selectedItems:$scope.choosePackages,
            columnDefs : [ {
                field : '',
                displayName : '序號',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'pkgGupPackageId',
                displayName : '素材及案例ID',
                visible : true
            }, {
                field : 'pkgGupName',
                displayName : '主題名稱'
            }, {
                field : 'pkgGupDescription',
                displayName : '主題說明'
            }, {
                field : '',
                displayName : '圖示',
                cellTemplate: '<div><img src="/ods/ODS308E/public/package/{{row.getProperty(\'pkgGupPackageId\')}}/image/{{row.getProperty(\'pkgGupImageUrl\')}}"  style="width:60px;heigth:60px"></div>'                    
            } ],
            afterSelectionChange : function(){
               // $scope.pkg = this.entity;
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
