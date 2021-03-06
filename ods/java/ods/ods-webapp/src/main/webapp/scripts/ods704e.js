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

        // ?????????????????????
        userHolder.getUser().then(function(response) {
            console.log(response.data);

        }, function(response) {
            if (200 != response.status) {
                alert("???????????????????????????!");
            }
        });
        
        $scope.chooseIdentities = {};

        
        //???????????????????????????????????????
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
                //???????????????????????????????????????id
                angular.forEach($scope.chooseIdentities, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    if (value) chkIdentityIdList = chkIdentityIdList + index + ",";
                  });
                
                selPackageIdList = "";
                //??????????????????????????????id
                angular.forEach($scope.gridData1, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    //if (value) selPackageIdList = selPackageIdList + index + ",";
                    //alert(angular.toJson(value.pkgGupPackageId,true));
                    //alert(index);
                    selPackageIdList = selPackageIdList + value.pkgGupPackageId + ",";
                  });
                

                
                
                if ($file != "") {
                    var re = /\.(jpg|jpeg|gif|png)$/i;  //????????????????????????
                    if (!re.test($file.name)) {
                        alerter.fatal('???????????????JPG/JPEG/PNG/GIF?????????????????????');
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
                alerter.fatal('???????????????????????????');
            }

        };
        
        $scope.removeConfirm = function() {
            if (confirm("?????????????????????")) { 
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
                alerter.fatal('????????????????????????');
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
                //???????????????????????????????????????id
                angular.forEach($scope.chooseIdentities, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    if (value) chkIdentityIdList = chkIdentityIdList + index + ",";
                  });
                
                selPackageIdList = "";
                //??????????????????????????????id
                angular.forEach($scope.gridData1, function (value, index) {
                    //console.log(index, value);
                    //if (value) alert(index);
                    //if (value) selPackageIdList = selPackageIdList + index + ",";
                    //alert(angular.toJson(value.pkgGupPackageId,true));
                    //alert(index);
                    selPackageIdList = selPackageIdList + value.pkgGupPackageId + ",";
                  });

                if ($file != "") {
                    var re = /\.(jpg|jpeg|gif|png)$/i;  //????????????????????????
                    if (!re.test($file.name)) {
                        alerter.fatal('???????????????JPG/JPEG/PNG/GIF?????????????????????');
                    } else {  
                        cResource().uploadFile({
                            url: 'rest/createAndUpload',
                            data: {name:$scope.model.name, description:$scope.model.description,chkIdentityIdList:chkIdentityIdList,selPackageIdList:selPackageIdList},
                            file: $file
                            
                        /*}).success(function(status, headers, config) {
                             // ?????? Callback.
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
                    alerter.fatal('???????????????????????????');
                }
            } else {
                alerter.fatal('???????????????????????????');
            }

        };
        

        
        
        $scope.query = function(name, description, currentRow, omitAlerts){
            $scope.gridData1 = []; //qry??????grid???????????????????????????grid??????
            $scope.chooseIdentities = {}; //qry????????????????????????????????????check??????

            
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
                
                //??????????????????????????????????????????grid???image???????????????????????????????????????
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
                displayName : '??????',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'name',
                displayName : '??????????????????'
            }, {
                field : 'description',
                displayName : '??????????????????'
            }, {
                field : 'imageUrl',
                displayName : 'imageUrl',
                visible : false
            }, {
                field : '',
                displayName : '??????',
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
            $scope.chooseIdentities = {}; //????????????????????????????????????????????????

            var result = ods704eService.findIndentityByGroupId('rest/identity_by_group_id',{groupId :groupId},omitAlerts||false);
            result.then(function(response){


                angular.forEach(response, function (item) {
                    //alert(angular.toJson(item,true));
                    //$scope.chooseIdentities["54F7DD1B-BE08-49DA-8EE5-43FA557D1327"] = true;
                    $scope.chooseIdentities[item.id.identityId] = true; //???????????????identity entity????????????id???????????????????????????
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
                    displayName : '??????',
                    cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
                }, {
                    field : 'pkgGupName',
                    displayName : '????????????'
                }, {
                    field : 'pkgGupDescription',
                    displayName : '????????????'
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
                    displayName : '??????',
                    cellTemplate: '<div><img src="/ods/ODS308E/public/package/{{row.getProperty(\'pkgGupPackageId\')}}/image/{{row.getProperty(\'pkgGupImageUrl\')}}"  style="width:60px;heigth:60px"></div>'
                }, {
                    field : 'option',
                    displayName : '??????',
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
            $scope.choosePackages.length = 0;   //?????????????????????????????????resource???reset
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
        //??????????????????resource
        $scope.queryPackage();       
                
        $scope.addPackage = function(){
            for(i = 0; i< $scope.choosePackages.length; i++){ //??????????????????package??????????????????
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
                displayName : '??????',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'pkgGupPackageId',
                displayName : '???????????????ID',
                visible : true
            }, {
                field : 'pkgGupName',
                displayName : '????????????'
            }, {
                field : 'pkgGupDescription',
                displayName : '????????????'
            }, {
                field : '',
                displayName : '??????',
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
