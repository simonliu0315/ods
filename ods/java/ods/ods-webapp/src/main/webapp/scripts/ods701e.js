(function() {

    var app = angular.module("ods701eApp", ['slsCommonModule', 'angularFileUpload','odsCommonFilter']);
    app.factory('ods701e02Service',function(cResource){
        var resource = cResource('rest/:ped',{ped :"@ped"});
        //var unSelCategoryResByResId = cResource('rest/findunselcategory_resid/:resId',{resId :"@resId"});
        //var selCategoryResByResId = cResource('rest/findselcategory_resid/:resId',{resId :"@resId"});
        var unSelCategoryResByCategoryId = cResource('rest/findunselcategory_categoryid/:categoryId',{categoryId :"@categoryId"});
        var selCategoryResByCategoryId = cResource('rest/findselcategory_categoryid/:categoryId',{categoryId :"@categoryId"});
        var danWbkAll = cResource('rest/finddanwbk_all');
        var danViewByDanWbkId = cResource('rest/finddanview_danwbkid/:danWbkId',{danWbkId :"@danWbkId"});
        var wbkResFileSize = cResource('rest/get_wbk_res_file_size/:wbkId/:viewId/:resType',{wbkId :"@wbkId", viewId :"@viewId", resType :"@resType"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {

            findAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                //alert(angular.toJson(param, true));
                return resource.execute(url , param,options);
            },
            /*findUnSelCategory : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                alert(angular.toJson(url, true));
                alert(angular.toJson(param, true));
                return resource.execute(url , param,options);
            },*/
            /*findUnSelCategoryResByResId : function(resId){
                return unSelCategoryResByResId.find({resId: resId});
            },*/
            findUnSelCategoryResByResId : function(url,param,omitAlerts) {
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url, param,options);
            },
            
            /*findSelCategoryResByResId : function(resId){
                return selCategoryResByResId.find({resId: resId});
            },*/
            findSelCategoryResByResId : function(url,param,omitAlerts) {
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url, param,options);
            },
            
            findUnSelCategoryResByCategoryId : function(categoryId){
                return unSelCategoryResByCategoryId.find({categoryId: categoryId});
            },
            findSelCategoryResByCategoryId : function(categoryId){
                return selCategoryResByCategoryId.find({categoryId: categoryId});
            },
            findDanWbk : function(){
                return danWbkAll.find();
            },
            findDanViewByDanWbkId : function(danWbkId){
                return danViewByDanWbkId.find({danWbkId: danWbkId});
            },
            getWbkResFileSize : function(wbkId,viewId,resType){
                return wbkResFileSize.find({wbkId :wbkId, viewId :viewId, resType :resType});
            },
            create : function(url,param,omitAlerts){
                //return resource.create(model,bind);
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            save : function(url,param,omitAlerts) {
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url, param,options);
            }
            
            ,findDanWbkByName : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            }
        };

    });


    app.controller('ods701e02Controller', function($scope,ods701e02Service,cStateManager,cAlerter,userHolder,alerter,$window,cResource,$modal) {
        $scope.model = {}; 
        var state = cStateManager([{ name: 'init', from: 'NONE',   to: 'INIT'  },
                      { name: 'query', from: ['INIT','RESETD','DSSELECTED','NONDSSELECTED'],   to: 'QUERYED'},
                      { name: 'reset', from: ['INIT','QUERYED'],   to: 'RESETD'},
                      { name: 'dsselect', from: ['QUERYED','NONDSSELECTED'],   to: 'DSSELECTED'},
                      { name: 'nondsselect', from: ['QUERYED','DSSELECTED'],   to: 'NONDSSELECTED'}]);

        // ?????????????????????
        userHolder.getUser().then(function(response) {
            console.log(response.data);

        }, function(response) {
            if (200 != response.status) {
                alert("???????????????????????????!");
            }
        });

        
        state.init();
        $scope.model.name = "";
        $scope.model.description = "";
        $scope.model.unSelCategory = "";  //?????????????????????????????????????????????????????????????????????
        $scope.model.selCategory = "";  //?????????????????????????????????????????????????????????????????????
        $scope.model.id = "NULL"; //???????????????null????????????????????????null???undefined???""?????????resource?????????/findunselcategory/{resId}????????????<??????grid???click??????????????????????????????"NULL">
        //$scope.model.delMk = false;
        $scope.gridData1 = {};
        //$("#uploadLocalFile").val("");
        $scope.files = "";
        $scope.model.toDatastoreDate = "";
        $scope.importSelectedView = "";
        $scope.optionRadios = "";
        var executeOnce = true;
        var sName = "";
        var sDesc = "";
        var selectedRow = 0;
        
        $scope.init = function(){

            //$scope.query();
            $scope.query("", "", 0, "", "");
            
            $scope.unSelCategoryQryByResId($scope.model.id);
            $scope.selCategoryQryByResId($scope.model.id);

        };
        
        $scope.resDetail = function(){

            //$window.open("ods701e_05.html?name=" + $scope.origResName, "popupWindow");
            $window.open("ods701e_05.html", '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');

            $window.passId=$scope.model.id;
            $window.passWbkName=$scope.model.workbookName;
            $window.passFormat=$scope.model.format;
            $window.passIsUnloadRefresh=true;
            
           
        };
        
        $scope.criteriaDetail = function(){

            //$window.open("../ODS706E/", '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');
            $window.open("../ODS706E/ods706e.html?resId=" + $scope.model.id, 'popupWindow');

            /*$window.passId=$scope.model.id;
            $window.passWbkId=$scope.model.workbookName;
            $window.passFormat=$scope.model.format;*/
            
           
        };
        
       $scope.query = function(name, description, currentRow, workbookName, viewName, omitAlerts){
           //alert(name + '-' + workbookName + '-' + viewName);
            state.init();
            /*$scope.model.name = "";
            $scope.model.description = "";
            $scope.model.unSelCategory = "";  //?????????????????????????????????????????????????????????????????????
            $scope.model.selCategory = "";  //?????????????????????????????????????????????????????????????????????
            $scope.model.id = "NULL"; //???????????????null????????????????????????null???undefined???""?????????resource?????????/findunselcategory/{resId}????????????<??????grid???click??????????????????????????????"NULL">
            //$scope.model.delMk = false;
            $("#uploadLocalFile").val("");
            $scope.model.filesize = "";
            $scope.model.toDatastoreDate = "";
            $scope.importSelectedView = "";
            $scope.optionRadios = "";
            $scope.gridData1 = {};*/
            

            name = name||"";
            description = description||"";
            sName = name;
            sDesc = description;
            //var result = ods701e02Service.find({ped:ped},omitAlerts||false);
            //var result = ods701e02Service.findAll('rest/find/all',{ped:ped},omitAlerts||false);
            var result = ods701e02Service.findAll('rest/find/all',{name :name, description: description, workbookName:workbookName, viewName:viewName},omitAlerts||false);
            result.then(function(response){
                if (response){
                    state.query();
                    executeOnce = true;
                    selectedRow = currentRow;
                } else {
                    state.reset();
                }

                if (angular.isArray(response)){
                    $scope.gridData1 = response;
                }else{
                    $scope.gridData1 = [response];
                }
                
            });
            
            $scope.unSelCategoryQryByResId($scope.model.id);
            $scope.selCategoryQryByResId($scope.model.id);
            
            $scope.danWbkQry();
        };
        
        
        $scope.option1Check = function(value){
            //alert(value);  //??????????????????option???value
            $scope.fileChoose();
            $("#uploadLocalFile").val("");  //??????file?????????????????????????????????
            $scope.files = "";
            $file="";   //??????file?????????????????????


        };
        

        
        //$scope.workbooks = ['item1', 'item2', 'item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3item3'];
        $scope.openImportDialog = function() {
            //$("#optionsRadios1").click();
            $scope.optionRadios = 'optionImport'; //?????????????????????jquery?????????click radioBtn
            
            var modalInstance = $modal.open({
                templateUrl: 'myModalContent.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    danWorkbooks: function () {
                    return $scope.danWorkbooks;
                  },
                //windowClass : 'ods701ImportDialog', // ???????????????????????? Css Class
                backdrop :"static"
                }
              });

              modalInstance.result.then(function (selectedImportFile) {
                 if(selectedImportFile){
                    $scope.importSelected = selectedImportFile;
                    $scope.importSelectedView = 'Workbook??????:{' + selectedImportFile.danWorkbook[0].name + '}, View??????:{' 
                     + selectedImportFile.danView[0].name + '}, ??????:{' + selectedImportFile.format[0].name + '}';
                    
                    
                    var result = ods701e02Service.getWbkResFileSize(selectedImportFile.danWorkbook[0].id, selectedImportFile.danView[0].id, selectedImportFile.format[0].id);
                    result.then(function(response){
                     
                        $scope.model.filesize = getReadableFileSizeString(response);
                        
                        if ("dataset" == selectedImportFile.format[0].id){
//                            if (response < 1000000) {
                                $scope.model.toDatastoreSync = "1";
                                $scope.model.toDatastoreDate = getToday();
//                            } else {
//                                $scope.model.toDatastoreSync = "0";
//                                $scope.model.toDatastoreDate = getTomorrow();
//                            }
                        } else {
                            $scope.model.toDatastoreSync = "0";
                            $scope.model.toDatastoreDate = "";
                        }

                    });

                    
                 }
              }, function () {
                $log.info('Modal dismissed at: ' + new Date());
              });
        };

        
        
        
        $scope.option2Check = function(value){
            //alert(value); 
            $scope.importSelected = "";
            
            $scope.fileChoose();

        };
        
        $scope.unSelCategoryQryByCategoryId = function(categoryId , omitAlerts){
            ods701e02Service.findUnSelCategoryResByCategoryId(categoryId).then(function(response){
                if (angular.isArray(response)){
                    $scope.unSelCategories = response;
                }else{
                    $scope.unSelCategories = [response];
                }


            });
        };
       
       $scope.selCategoryQryByCategoryId = function(categoryId , omitAlerts){
           ods701e02Service.findSelCategoryResByCategoryId(categoryId).then(function(response){
               if (angular.isArray(response)){
                   $scope.selCategories = response;
               }else{
                   $scope.selCategories = [response];
               }

           });
       };
       
       $scope.unSelCategoryQryByResId = function(resId , omitAlerts){
            //resCategoryQry($scope.model.resId);
             //var result2 = ods701e02Service.findUnSelCategory('rest/findunselcategory/:resId',{resId:"628ed6b3-5e7d-43b9-b8c7-ff826c2766f5"},omitAlerts||false);
             //var result2 = ods701e02Service.findUnSelCategory('rest/findunselcategory/:resId',{resId:"628ed6b3-5e7d-43b9-b8c7-ff826c2766f5"},omitAlerts||false);
             /*ods701e02Service.findUnSelCategoryResByResId(resId).then(function(response){
                 if (angular.isArray(response)){
                     $scope.unSelCategories = response;
                 }else{
                     $scope.unSelCategories = [response];
                 }
                 
             });*/
             
           ods701e02Service.findUnSelCategoryResByResId('rest/findunselcategory_resid',
                     {id:resId},omitAlerts||false).then(function(response){
                         if (angular.isArray(response)){
                             $scope.unSelCategories = response;
                         }else{
                             $scope.unSelCategories = [response];
                         }
             });
             
         };
         
         $scope.selCategoryQryByResId = function(resId , omitAlerts){
             /*ods701e02Service.findSelCategoryResByResId(resId).then(function(response){
                 if (angular.isArray(response)){
                     $scope.selCategories = response;
                 }else{
                     $scope.selCategories = [response];
                 }
                 
             });*/
             
             ods701e02Service.findSelCategoryResByResId('rest/findselcategory_resid',
                     {id:resId},omitAlerts||false).then(function(response){
                         if (angular.isArray(response)){
                             $scope.selCategories = response;
                         }else{
                             $scope.selCategories = [response];
                         }
             });
             
         };
         
 
        $scope.clickAddcategory = function(){
            if($scope.model.unSelCategory.length > 0){
                
                
                //$scope.selCategories.push({"id":$scope.model.unSelCategory, 'name':$('#cateUnSel').find("option:selected").text()});
                for(i = 0; i< $scope.model.unSelCategory.length; i++){
                    $scope.selCategories.push($scope.model.unSelCategory[i]);
    
                }
                
                //$("#cateUnSel option:selected").each(function () {
                $($("#cateUnSel option:selected").get().reverse()).each(function () {
    
                    var $this = $(this);
                    if ($this.length) {
                     var selIdx = $this.val();
                     $scope.unSelCategories.splice(selIdx, 1);
                    }
                });
                
                $scope.model.unSelCategory.length = 0;  //???????????????
                
                var categoryIdLists = "";
                for(i = 0; i< $scope.selCategories.length; i++){
                    categoryIdLists = categoryIdLists + $scope.selCategories[i].id + ",";
    
                }
                $scope.model.resCategory = categoryIdLists;  //????????????????????????id??????grid???????????????
                if (categoryIdLists == "")
                {
                    $scope.model.resCategory ="NULL";
                }
                $scope.model.isChange="Y";
            }
            
        };
        $scope.clickDelcategory = function(){
            if($scope.model.selCategory.length > 0){
                //$scope.selCategories.push({"id":$scope.model.unSelCategory, 'name':$('#cateUnSel').find("option:selected").text()});
                for(i = 0; i< $scope.model.selCategory.length; i++){
                    $scope.unSelCategories.push($scope.model.selCategory[i]);
                }
                //$("#cateSel option:selected").remove();
                $($("#cateSel option:selected").get().reverse()).each(function () {
    
                    var $this = $(this);
                    if ($this.length) {
                     var selIdx = $this.val();
                     $scope.selCategories.splice(selIdx, 1);
                    }
                });
                
                $scope.model.selCategory.length = 0;  //???????????????
                
                var categoryIdLists = "";
                for(i = 0; i< $scope.selCategories.length; i++){
                    categoryIdLists = categoryIdLists + $scope.selCategories[i].id + ",";
    
                }
                $scope.model.resCategory = categoryIdLists;  //????????????????????????id??????grid???????????????
                if (categoryIdLists == "")
                {
                    $scope.model.resCategory ="NULL";
                }
                $scope.model.isChange = "Y";

            }
        };




        $scope.create = function(omitAlerts){
            if ($scope.check($scope.model.name)) {
                //ods701e02Service.create('rest/res/create',{name:$scope.model.name, description:$scope.model.description, delMk:$scope.model.delMk=="N"?"0":"1"},omitAlerts||false).then(
                
                //??????jquery????????????????????????print???
                /*var selectArr = []; 
                $('#cateSel option').each(function() {
                    selectArr.push($(this).text());
                });
                alert(selectArr);*/

                ods701e02Service.create('rest/res/create',{name:$scope.model.name, description:$scope.model.description, selCategory:$scope.selCategories},omitAlerts||false).then(
                   function(response){
                       //$scope.query("", "", "", true);
                       //$scope.query(sName, sDesc, selectedRow, "", "", true);
                       $scope.query(sName, sDesc, 0, "", "", true); //????????????????????????1????????????????????????
                }, function(response){

                }); 
            }
        };
        
        
        
        
        
        $scope.danWbkQry = function(omitAlerts){
            ods701e02Service.findDanWbk().then(function(response){
                if (angular.isArray(response)){
                    $scope.danWorkbooks = response;
                }else{
                    $scope.danWorkbooks = [response];
                }
                
            });
        };
        
        

        
        
        $scope.syncCheck = function($toDatastoreSync) {
            if ( $toDatastoreSync == 0)
            {
                $scope.model.toDatastoreDate = getTomorrow();
            } else {
                $scope.model.toDatastoreDate = getToday();
            }
       };
       
       /*$scope.model.datastoreDateChange = function($toDatastoreDate) {
           //TODO ??????????????????????????????????????????????????????
      };*/
       

       


       

       
       $scope.returntrueday = function(toDatastoreDate){
           if(toDatastoreDate != "")
           {
           var toDatastoreDateSplit = toDatastoreDate.split('/');
           //alert(toDatastoreDate);
           toDatastoreDateSplit[0] = parseInt(toDatastoreDateSplit[0], 10) + 1911;
           //alert(toDatastoreDateSplit[0] + toDatastoreDateSplit[1] + toDatastoreDateSplit[2]);
           return toDatastoreDateSplit[0] + toDatastoreDateSplit[1] + toDatastoreDateSplit[2];
           } else {
               return toDatastoreDate;
           }
       };
       
       $scope.fileChoose = function() {
           $scope.model.filesize = "";
           $scope.model.toDatastoreSync = "0";
           $scope.model.toDatastoreDate="";
      };

        
      var $file = "";
      
        //catch file object
        $scope.onFileSelect = function($files) {
            $scope.optionRadios = 'optionUpload';
            
            if (!$files || $files.length == 0) {
                $scope.files = null;
                return;
            }
            $scope.files = $files;
            $file = $files[0];
            //alert($file.name);
          $scope.model.filesize = getReadableFileSizeString($file.size);

          if ("csv" == getFileExtension($file.name)){
//              if ($file.size < 1000000) {
                  $scope.model.toDatastoreSync = "1";
                  $scope.model.toDatastoreDate = getToday();
//              } else {
//                  $scope.model.toDatastoreSync = "0";
//                  $scope.model.toDatastoreDate = getTomorrow();
//              }
          } else {
              $scope.model.toDatastoreSync = "0";
              $scope.model.toDatastoreDate = "";
          }

        };
        

        
        
        
        $scope.upload = function(){
            cResource().uploadFile({
                url: 'rest/fileUpload',
                file: $file
            }).success(function(status, headers, config) {
                // ?????? Callback.
                //$scope.showFlag = true;
                //$scope.progress = $scope.progress + ' ';
            });
       };
       

        
        $scope.createImportUpload = function(omitAlerts){
            /*var myArray = new Array();
            myArray.push("1");
            myArray.push("2");
            myArray2=["3", "4"];
            */

            if ($scope.check($scope.model.name)) {
                selCategoryIdList = "";
                for (var i=0; i < $scope.selCategories.length; i++) {
                    selCategoryIdList = selCategoryIdList + $scope.selCategories[i].id + ",";
                }
                
                //alert(angular.toJson($scope.importSelected, true));
                //????????????import?????????
                selImportInfoList = "";
                if(!isEmptyOrUndefined($scope.importSelected))
                {
                    selImportInfoList = $scope.importSelected.danWorkbook[0].id + ',' 
                    + $scope.importSelected.danView[0].id + ',' + $scope.importSelected.format[0].id + ',' +  $scope.importSelected.danWorkbook[0].name;
                }
                

                if ($scope.optionRadios == 'optionUpload')
                {
                    if ($file != "") {
                        var index = $("#uploadLocalFile").val().indexOf(".");
                        var ext = $("#uploadLocalFile").val().substring(++index);
                        if ("csv" != ext && "pdf" != ext && "jpg" != ext && "png" != ext && "gif" != ext && "jpeg" != ext) {
                            alerter.fatal('????????????????????????!');
                        } else {       
                            cResource().uploadFile({
                                url: 'rest/createAndUpload',
                                //data: {name:$scope.model.name, description:$scope.model.description, selCategory:$scope.selCategories, myAry:myArray2},
                                data: {name:$scope.model.name, description:$scope.model.description, selCategoryIdList:selCategoryIdList, toDatastoreSync:$scope.model.toDatastoreSync, toDatastoreDate:$scope.returntrueday($("#toDatastoreDate").val())},
                                file: $file
                                
                            }
                            /*).success(function(status, headers, config) {
                                 // ?????? Callback.
                                //$scope.showFlag = true;
                                //$scope.progress = $scope.progress + ' ';
                                $("#uploadLocalFile").val("");
                                //$scope.query("", "", "", true);
                                //$scope.query(sName, sDesc, selectedRow, "", "", true);
                                $scope.query(sName, sDesc, 0, "", "", true); //????????????????????????1????????????????????????
                            });*/
                            ).success(function(status, headers, config) {
                                if(status.alerts[0].type == 'success' || status.alerts[0].type == 'warning')
                                {
                                    // ?????? Callback.
                                    //$scope.showFlag = true;
                                    //$scope.progress = $scope.progress + ' ';
                                    //$("#uploadLocalFile").val("");
                                    $scope.files = "";
                                    $scope.optionRadios = "";
                                    //$scope.query("", "", "", true);
                                    //$scope.query(sName, sDesc, selectedRow, "", "", true);
                                    $scope.query(sName, sDesc, 0, "", "", true); //????????????????????????1????????????????????????
                                }
                           });
                        }
                        
                    } else {
                        alerter.fatal('????????????????????????');
                    }    
                        
                 } else {
                     if (selImportInfoList != "") {
                        /*cResource().uploadFile({
                            url: 'rest/createAndImport' , 
                            data: {name:$scope.model.name, description:$scope.model.description, selCategoryIdList:selCategoryIdList, selImportInfoList:selImportInfoList, toDatastoreSync:$scope.model.toDatastoreSync, toDatastoreDate:$scope.returntrueday($("#toDatastoreDate").val())}
                        }
//                        ).success(function(status, headers, config) {
//                            // ?????? Callback.
//                           //$scope.showFlag = true;
//                           //$scope.progress = $scope.progress + ' ';
//                           $("#uploadLocalFile").val("");
//                           //$scope.query("", "", "", true);
//                           //$scope.query(sName, sDesc, selectedRow, "", "", true);
//                           $scope.query(sName, sDesc, 0, "", "", true); //????????????????????????1????????????????????????
//                       });
                        ).success(function(status, headers, config) {
                            if(status.alerts[0].type == 'success')
                            {
                                // ?????? Callback.
                                //$scope.showFlag = true;
                                //$scope.progress = $scope.progress + ' ';
                                $("#uploadLocalFile").val("");
                                $scope.optionRadios = "";
                                //$scope.query("", "", "", true);
                                //$scope.query(sName, sDesc, selectedRow, "", "", true);
                                $scope.query(sName, sDesc, 0, "", "", true); //????????????????????????1????????????????????????
                            }
                       });*/
                         

                        
                        ods701e02Service.create('rest/createAndImport',{name:$scope.model.name, description:$scope.model.description, selCategoryIdList:selCategoryIdList, selImportInfoList:selImportInfoList, toDatastoreSync:$scope.model.toDatastoreSync, toDatastoreDate:$scope.returntrueday($("#toDatastoreDate").val())},omitAlerts||false).then(
                                function(response){
                                    //alert(angular.toJson(response, true));
                                    // ?????? Callback.
                                    //$scope.showFlag = true;
                                    //$scope.progress = $scope.progress + ' ';
                                    //$("#uploadLocalFile").val("");
                                    $scope.files = "";
                                    $scope.optionRadios = "";
                                    //$scope.query("", "", "", true);
                                    //$scope.query(sName, sDesc, selectedRow, "", "", true);
                                    $scope.importSelected = "";
                                    $scope.query(sName, sDesc, 0, "", "", true); //????????????????????????1????????????????????????
                                    
                                    
                                    
                             }, function(response){

                             }); 

                        
                     } else {
                         alerter.fatal('????????????????????????');
                     }
        
                    }
                    
                }

            

        };

        
        
        $scope.check = function(name){
            var ischeck = true;

            if (isEmptyOrUndefined(name)) {
                alerter.fatal('???????????????????????????');
                ischeck = false;
                return;
            }
            return ischeck;
        };
        
        $scope.save = function(omitAlerts){
            //$scope.model.ggg='AAA';  //???click update?????????????????????grid????????????????????????
            //alert(angular.toJson($scope.mySelections, true));
            //alert(angular.toJson($scope.model, true));
            //alert(angular.toJson($scope.gridData1, true)); //????????????gridData1?????????
            if ($scope.checkSave($scope.gridData1)) {
                //alert(angular.toJson($scope.gridData1, true));
               /* ods701e02Service.save('rest/res/saveGrid1',$scope.gridData1, omitAlerts||false).then(
                   function(response){
                       //$scope.query("", "", "", true);
                       $scope.query(sName, sDesc, selectedRow, "", "", true);
                }, function(response){
                });
                */

                
                if($scope.optionRadios !='' || $scope.model.toDatastoreSync!=undefined)
                {
                    bootbox.confirm("??????????????????????????????????????????????????????????", function(result) {
                        if (result){
                            //?????????????????????????????????
                            $scope.optionRadios = "";
                            $scope.option1Check();
                            $scope.option2Check();
                            $scope.model.filesize = undefined;
                            $scope.model.toDatastoreSync = undefined;
                            $scope.model.toDatastoreDate = undefined;

                            
                            //ods701e02Service.save('rest/res/saveGrid1',$scope.gridData1, omitAlerts||false).then(
                            ods701e02Service.save('rest/res/saveGrid1',{id:$scope.model.id, name:$scope.model.name, description:$scope.model.description, resCategory:$scope.model.resCategory, isChange:$scope.model.isChange}, omitAlerts||false).then(
                                    function(response){
                                        //$scope.query("", "", "", true);
                                        $scope.query(sName, sDesc, selectedRow, "", "", true);
                                 }, function(response){
                                 });
                        }
                     });
                } else {
                    /*alert($scope.model.name);
                    alert($scope.model.description);
                    alert($scope.model.resCategory);*/
                    //ods701e02Service.save('rest/res/saveGrid1',$scope.gridData1, omitAlerts||false).then(
                    ods701e02Service.save('rest/res/saveGrid1',{id:$scope.model.id, name:$scope.model.name, description:$scope.model.description, resCategory:$scope.model.resCategory, isChange:$scope.model.isChange}, omitAlerts||false).then(
                            function(response){
                                //$scope.query("", "", "", true);
                                $scope.query(sName, sDesc, selectedRow, "", "", true);
                         }, function(response){
                         });
                    
                    //data: {name:$scope.model.name, description:$scope.model.description, selCategoryIdList:selCategoryIdList},
                }
 
            }
       };
        
        $scope.checkSave = function(gridData1){
            var ischeck = true;
            var failIdx = "";
            //alert(angular.toJson($scope.gridData1, false));
            //alert(angular.toJson($scope.gridData1.length, false));
            //for (dataIdx in gridData1){  //Array indexOf and filter implementation for IE giving other Issues
            for (var dataIdx = 0; dataIdx < gridData1.length; dataIdx++){
                if (isEmptyOrUndefined(gridData1[dataIdx].name)){
                    intDataIdx = parseInt(dataIdx, 10) + 1;
                    failIdx = failIdx + intDataIdx + ", ";
                }
            }
            if (!isEmptyOrUndefined(failIdx)){
                alerter.fatal('??????' + failIdx.slice(0, -2) + '??????????????????????????????');
                ischeck = false;
                return;
            }
            return ischeck;
        };
        
        $scope.reset = function(){
            cAlerter.clear();
            $scope.model.name = "";
            $scope.model.description = "";
            $scope.model.unSelCategory = "";  //?????????????????????????????????????????????????????????????????????
            $scope.model.selCategory = "";  //?????????????????????????????????????????????????????????????????????
            $scope.model.id = "NULL"; //???????????????null????????????????????????null???undefined???""?????????resource?????????/findunselcategory/{resId}????????????<??????grid???click??????????????????????????????"NULL">
            //$scope.model.delMk = false;
            //$("#uploadLocalFile").val("");
            $scope.files = "";
            $scope.model.toDatastoreDate = "";
            $scope.importSelectedView = "";
            $scope.optionRadios = "";
            $scope.model.filesize = "";
            $scope.model.toDatastoreSync = "0";
            
            $scope.model.workbookName = "";
            $scope.model.viewName = "";
            
            //$scope.query("","", "", true);
            $scope.query("", "", "", "", true);
            /*$scope.model = {};
            $scope.gridData1 = [];
            cAlerter.clear();
            state.reset();*/
            executeOnce = true;
            sName = "";
            sDesc = "";
            selectedRow = 0;
        };


    
        $scope.mySelections = [];
        $scope.girdObject1 = {
            multiSelect : false,
            data : 'gridData1',
            rowHeight : 40,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                field : 'rowCount',
                displayName : '??????'
            }, {
                field : 'name',
                displayName : '?????????????????????'
            }, {
                field : 'description',
                displayName : '?????????????????????'
            }, {
                field : 'workbookName',
                displayName : 'Workbook'
            }, {
                field : 'viewName',
                displayName : 'View'
            }, {
                field : 'format',
                displayName : '????????????'
            }, {
                field : 'maxResVer',
                displayName : '????????????'
            }, {
                field : 'maxResVerCreated',
                displayName : '????????????',
                cellTemplate: '<span ng-cell-text ng-bind="row.entity.maxResVerCreated | longToRocDateFilter"></span>'
            }, {
                field : 'id',
                displayName : 'ID',
                visible : false
            }, {
                field : 'isChange',
                displayName : '????????????',
                visible : false
            }, {
                field : 'resCategory',
                displayName : '??????',
                visible : false
            }],
            selectedItems: $scope.mySelections,
            
            beforeSelectionChange: function(row) {
                //singleSelect: add start
                //$scope.model.name = $("#materialName02Backup").val();
                //$scope.model.description = $("#materialNote02Backup").val();
                $scope.model.isChange="N";
                //singleSelect: add end
                row.changed = true;
                return true;
              },
            afterSelectionChange : function(row, event){
                $scope.model =  angular.copy(this.entity);
                if (executeOnce){
                    //???grid??????????????????model????????????????????????????????????grid?????????deselect???????????????text?????????????????????grid?????????????????????
                    if(isEmptyOrUndefined($scope.mySelections))
                    {
                        $scope.model = {};
                        $scope.model.id = "NULL";
                        state.query();
                    } else {
                        
                        if($scope.mySelections[0].format == 'dataset') {
                            state.dsselect();
                        } else {
                            state.nondsselect();
                        }
                        
                    }
                    $scope.unSelCategoryQryByResId($scope.model.id);
                    $scope.selCategoryQryByResId($scope.model.id);
                    executeOnce=false;
                }
                if (row.changed){
                    console.log("deal with row " + row.rowIndex);
                    row.changed=false;
                  
                    //this.entity.isChange='Y';
                    //$scope.model = this.entity;
                    //alert(angular.toJson(this.entity,true));

                    //singleSelect: add start
                    //$("#materialName02Backup").val($scope.model.name) ;
                    //$("#materialNote02Backup").val($scope.model.description);
                    //singleSelect: add end


                    
                    //???grid??????????????????model????????????????????????????????????grid?????????deselect???????????????text?????????????????????grid?????????????????????
                    if(isEmptyOrUndefined($scope.mySelections))
                    {
                        $scope.model = {};
                        $scope.model.id = "NULL";
                        state.query();
                    } else {
                        
                        if($scope.mySelections[0].format == 'dataset') {
                            state.dsselect();
                        } else {
                            state.nondsselect();
                        }
                        
                    }


                    //singleSelect: mark start
                    /*if(isEmptyOrUndefined($scope.model.resCategory))
                    {
                        $scope.unSelCategoryQryByCategoryId("NULL");
                    } else {
                        $scope.unSelCategoryQryByCategoryId($scope.model.resCategory);
                    }
                    if(isEmptyOrUndefined($scope.model.resCategory))
                    {
                        $scope.selCategoryQryByCategoryId("NULL");
                    } else {
                        $scope.selCategoryQryByCategoryId($scope.model.resCategory);
                    }*/
                    //singleSelect: mark end
                    
                    //singleSelect: add start
                    $scope.unSelCategoryQryByResId($scope.model.id);
                    $scope.selCategoryQryByResId($scope.model.id);
                    //singleSelect: add end
                    selectedRow = row.rowIndex;
                //alert(angular.toJson($scope.mySelections,true));
                }
            }
        };
        
        //?????????????????????????????? selectedRow ?????????
        $scope.$on('ngGridEventData', function(){
            if ($scope.gridData1){
                $scope.girdObject1.selectRow(selectedRow, true);
            }
        });

        
        $('#materialName02').bind('keyup', function(){
            $scope.model.isChange="Y";
        });
        $('#materialNote02').bind('keyup', function(){
            $scope.model.isChange="Y";
        });


    });
    
    


    
    var ModalInstanceCtrl = function ($scope,ods701e02Service, $modalInstance, danWorkbooks) {

        $scope.danWorkbooks = danWorkbooks;
        $scope.importSelected = {
                //danWorkbook: $scope.danWorkbooks[0]
                danWorkbook: ''
        };

        $scope.ok = function () {
          $modalInstance.close($scope.importSelected);
        };

        $scope.cancel = function () {
          $modalInstance.dismiss('cancel');
        };
        
        $scope.danWorkbookChoose = function($danWorkbook) {
            if($danWorkbook){   
                $scope.formats = [''];//????????????danWbk????????????????????????format??????????????????
                $scope.importSelected.danView = "";//????????????danWbk??????????????????????????????????????????view???????????????
                $scope.importSelected.format = "";//????????????danWbk???????????????????????????????????????????????????????????????
                
                $scope.importSelected.danWorkbook = $danWorkbook; //???????????????????????????????????????
                //alert(angular.toJson( $scope.importSelected.danWorkbook, true));
              
                ods701e02Service.findDanViewByDanWbkId($danWorkbook[0].id).then(function(response){
                    if (angular.isArray(response)){
                        $scope.danViews = response;
                    }else{
                        $scope.danViews = [response];
                    }
    
                });
                
                $('#importDialogOk').attr("disabled", true); //????????????ok????????????????????????????????????
            }
        };
        
        $scope.danViewChoose = function($danView) {
            if($danView){ 
                $scope.importSelected.format = "";//????????????danView???????????????????????????????????????????????????????????????
                
                $scope.importSelected.danView = $danView; //???????????????????????????????????????
                $scope.formats = [{name:'?????????',id:'dataset'}, {name:'PDF',id:'pdf'}, {name:'??????',id:'image'}]; //????????????danView????????????????????????format?????????
                
                $('#importDialogOk').attr("disabled", true); //????????????ok????????????????????????????????????
            }
        };
       
        $scope.danFormatChoose = function($format) {
            if($format){ //http://stackoverflow.com/questions/21069994/angularjs-keeps-giving-typeerror-cannot-read-property-dhx-security-of-undefin
                $scope.importSelected.format = $format; //???????????????????????????????????????
                //alert(angular.toJson($scope.importSelected, true));
                
                $('#importDialogOk').attr("disabled", false); //????????????ok???????????????????????????
            }
        };
        
        $scope.queryImportResource = function($wbkResName){
            //alert($wbkResName);    
            
            $scope.danViews = [''];//??????????????????????????????view??????????????????
            $scope.formats = [''];//??????????????????????????????format??????????????????
            
            
            $scope.importSelected.danWorkbook = "";//????????????????????????????????????????????????workbook???????????????
            $scope.importSelected.danView = "";//????????????????????????????????????????????????view???????????????
            $scope.importSelected.format = "";//?????????????????????????????????????????????????????????????????????
            
            name = $wbkResName||"";
            var result = ods701e02Service.findDanWbkByName('rest/finddanwbk_by_name',{name :name},false);
            result.then(function(response){
                if (angular.isArray(response)){
                    $scope.danWorkbooks = response;
                }else{
                    $scope.danWorkbooks = [response];
                }
            });
        };
       
      };

    
    
    app.factory('ods701e05Service',function(cResource){
        var resource = cResource('rest/resdetailndel/:ped',{ped :"@ped"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {

            findResDetailNDel : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            },
            findResDetailAll : function(url,param,omitAlerts){
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
            }
        };

    });
    
    app.directive('imageDisplay', function() {
        
        function link (scope, element, attrs) {
            //Pass image data in here
            attrs.$observe('fileData', function (complexValue) {
                var data = '';
                var mime = 'Image/png';
                var fileName = 'test.png';

                //var parts = complexValue.split(',');
                
                //Rebuild Base64 String
                data = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAwACkDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+vO/ib8UoPAYt7G2tVu9WuU8xI3bakSZIDNjk5IIAHoeRgZ9EryDxktxpnxTlnWO0+z6jpsCyy3SKweNJiJbcBs8OpXJ7ZH1pSkoq7Gk27IyfC/x6vZ9Wgt/Eum2sFnO/li6tQyiI8ZJDE7gMjOCCAc89D7rXzN40Wa0+HY0oavBfRWhhIi8tVCMHfdIuMkSMZQCCfuq55zhfojw/cfbPDel3O/f51pFJuznOUBzU06kaivB3Q5RcXaRo0UUVZIjMqIXdgqqMkk4AFePeE4IPij4417xReiSTRbMf2bpkYZlDDhmfggg4wcEf8tMH7tbvxt1qXR/htcxwMUfUZksi4PRWyz/AJqrL/wKrnwgi0+D4badHp8iuA0hmxjcshckhvcAr9RtPQik0nowTsJ4m+Gml6n4O1DS7CELfSIGguJnLMJFO5Rk/dUkYOB0OeTXnHwa8eXGi6j/AMIfrqSRQy3DQ2jS5Bt584aFgemW6ejHHO7j6Br5e+LDQw+O/FEUR8pg1vPHsOD53lJyMdDhmJ96FFJWQ223dn1DRVTSrpr7R7K7fG6e3jlOPVlB/rVumI8/8b3UWqXn9kXFpBJBaSJLieMPvfbwQDxgBj+NYHhXTYdF8bJqdtqUmn2EyFLmwSIeTK2CFJORtAJB6HHOCAxrtdf8JzanqL31rcxq7qAY5QcZHGQw6DpxisGLwvrhu1ha1CITgzmRWRR64B3H6YGfbqPJq/WoVnKKbX4WO6HsZU7M7nWr5NO0S8vGmSIxxHY7kAbzwg57liAB3JArwfX/AAc/iDxEdQuL8fY5ijTxbP3p2jGA/o3c9cnvgV6VdfCfSLwxtcatrkzwuJYvNvNyRyDkMqbdowewAFTad4JuvPH9pTwiBD923ZiZB7kgbfwyfcda6MTGu5xdLtYxpOnytTNHwZfGXTTp5TBswArD7uwk7VHptAxj0ArpqitrWCzhENtCkUY6KgwPr9alrqowlCCjJ3aMpyUpNo//2Q==';

                //Get Canvas Context
                var ctx = element[0].getContext("2d");

                //Clear Previous Display
                ctx.clearRect(0, 0, element[0].width, element[0].height);

                //Parse Content Type
                parts = mime.split('/');
                var contentType = parts[0];

                var image = new Image();
                image.onload = function () {
                    element[0].height = image.height;
                    element[0].width = image.width;
                    ctx.drawImage(image, 1, 1, image.width, image.height);
                    
                };
                image.src = data;
            });
        }
        return {
           restrict: 'A',
           link: link
        };
    });




    
    app.controller('ods701e05Controller', function($scope,ods701e05Service,cStateManager,cAlerter,userHolder,alerter,$window,cResource) {
        $scope.model = {}; 
        $window.onunload = refreshParent;
        function refreshParent() {
            if(window.opener.passIsUnloadRefresh)
            {
                window.opener.location.reload();
            }
        }

        
        //justin
        //window.opener.passId = '7DC9E582-CF3E-44E2-8A1C-921D96769EDF'; 
        //window.opener.passId = '43C5337B-1DB2-43FA-A535-71473DB766D6'; //WBK
        //window.opener.passWbkName = '1';
        //window.opener.passFormat = 'dataset';
        

        if(isEmptyOrUndefined(window.opener) || window.opener.passId=="NULL")
            {
                alerter.fatal('??????????????????????????????????????????');
                $scope.girdObject2 = {
                        multiSelect : false,
                        data : 'gridData2',
                        rowHeight : 40,
                        keepLastSelected: false,
                        enableColumnResize : true,
                        columnDefs : [ {
                            field : 'name',
                            displayName : '?????????????????????'
                        }, {
                            field : 'description',
                            displayName : '?????????????????????'
                        }, {
                            field : 'ver',
                            displayName : '??????'
                        }, {
                            field : 'versionDatetime',
                            displayName : '????????????',
                            cellTemplate: '<span ng-cell-text ng-bind="row.entity.versionDatetime | longToRocDateFilter"></span>'
                        },  {
                            field : 'workbookVer',
                            displayName : 'Workbook??????'
                        },{
                            field : 'delMk',
                            displayName : '????????????'
                        }, {
                            field : 'resourceId',
                            displayName : 'resourceId',
                            visible : false
                        }, {
                            field : 'toDatastoreSyncGrid',
                            displayName : '????????????'
                        }, {
                            field : 'toDatastoreDateGrid',
                            displayName : '??????????????????',
                            cellTemplate: '<span ng-cell-text ng-bind="row.entity.toDatastoreDateGrid | longToRocDateNoTimeFilter"></span>'
                        }, {
                            field : 'toDatastoreSuccessGrid',
                            displayName : '??????????????????'
                        }
                        /*, {
                            field : 'image',
                            displayName : 'resourceId',
                            visible : true
                        }*/
                        ]
                };
                return false;
            }
        passId = window.opener.passId;


        if(isEmptyOrUndefined(window.opener.passWbkName))
        {
            $("#uploadBtn").attr("disabled", false);
        } else {
            $scope.wbkName = window.opener.passWbkName; //wbkName????????????????????????????????????????????????????????????????????????, ?????????????????????
        }

        if(!isEmptyOrUndefined(window.opener.passFormat))
        {
            $scope.fmt = window.opener.passFormat; //fmt????????????????????????????????????????????????????????????????????????????????????
            
            if ("dataset" == window.opener.passFormat){
                $("#csvBtn").attr("disabled", false);
            }
            if ("pdf" == window.opener.passFormat){
                $("#pdfBtn").attr("disabled", false);
            }
            if ("image" == window.opener.passFormat){
                $("#pngBtn").attr("disabled", false);
            }
        }

        $scope.csvPreview = function(omitAlerts){
            cAlerter.clear();
            if(!isEmptyOrUndefined($scope.mySelections))
            {
                resourceId = $scope.model.resourceId;
                ver = $scope.model.ver;
                $window.open("../ODS308E/public/resource/" + resourceId + '/dataset/' + resourceId + '-' + ver + '.csv', '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');

            } else {
                alerter.fatal('????????????????????????????????????');
            }

        };
        
        $scope.pdfPreview = function(omitAlerts){
            cAlerter.clear();
            if(!isEmptyOrUndefined($scope.mySelections))
            {
                resourceId = $scope.model.resourceId;
                ver = $scope.model.ver;
                $window.open("../ODS308E/public/resource/" + resourceId + '/pdf/' + resourceId + '-' + ver + '.pdf', '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');

            } else {
                alerter.fatal('????????????????????????????????????');
            }
        };
        
        $scope.pngPreview = function(omitAlerts){
            cAlerter.clear();
            if(!isEmptyOrUndefined($scope.mySelections))
            {
                resourceId = $scope.model.resourceId;
                ver = $scope.model.ver;
                $window.open("../ODS308E/public/resource/" + resourceId + '/image/' + resourceId + '-' + ver + '.png', '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');

            } else {
                alerter.fatal('????????????????????????????????????');
            }        };

        
        //urlName = $.getUrlVar('name');
        var state = cStateManager([{ name: 'init', from: 'NONE',   to: 'INIT'  },
                      { name: 'query', from: ['INIT','RESETD','SELECTED'],   to: 'QUERYED'},
                      { name: 'reset', from: ['INIT','QUERYED'],   to: 'RESETD'},
                      { name: 'select', from: ['QUERYED'],   to: 'SELECTED'}]);
        

        // ?????????????????????
        userHolder.getUser().then(function(response) {
            console.log(response.data);

        }, function(response) {
            if (200 != response.status) {
                alert("???????????????????????????!");
            }
        });


        state.init();
        $scope.gridData2 = {};
        $scope.changeAgeList = [{code:"01", name:"N"}, {code:"02", name:"Y"}]; 
        $scope.changeAgeType = $scope.changeAgeList[0];
        var executeOnce = true;
        var sName = "";
        var sDesc = "";
        var selectedRow = 0;
        

        $scope.init = function(){

            //$scope.queryNdelMkVerById();
            $scope.queryNdelMkVerById("", "", 0);

        };
        
  

        $scope.queryNdelMkVerById = function(name, description, currentRow, omitAlerts){
            //ped = ped||($scope.model ? $scope.model.ped :"");
            //var result = ods701e05Service.find({ped:ped},omitAlerts||false);
            name = name||"";
            description = description||"";
            sName = name;
            sDesc = description;
            var result = ods701e05Service.findResDetailNDel('rest/resdetailndel/find/res',{id:passId, name:name, description:description},omitAlerts||false);
            result.then(function(response){
                if (response){
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

        $scope.queryAllVerById = function(ped , omitAlerts){
            ped = ped||($scope.model ? $scope.model.ped :"");
            //var result = ods701e05Service.find({ped:ped},omitAlerts||false);
            var result = ods701e05Service.findResDetailAll('rest/resdetailall/find/res',{id:passId},omitAlerts||false);
            result.then(function(response){
                if (response){
                    state.query();
                    executeOnce = true;
                    selectedRow = 0;
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
        
        
        $scope.create = function(omitAlerts){
            if ($scope.check($scope.model.name)) {
                //ods701e02Service.create('rest/res/create',{name:$scope.model.name, description:$scope.model.description, delMk:$scope.model.delMk=="N"?"0":"1"},omitAlerts||false).then(
                ods701e05Service.create('rest/resdetail/create',{resourceId: passId, name:$scope.model.name, description:$scope.model.description},omitAlerts||false).then(
                   function(response){
                       $scope.queryNdelMkVerById(sName, sDesc, selectedRow, true);
                }, function(response){

                }); 
            }
        };
        
        
        
        $scope.syncCheck = function($toDatastoreSync) {
            if ( $toDatastoreSync == 0)
            {
                $scope.model.toDatastoreDate = getTomorrow();
            } else {
                $scope.model.toDatastoreDate = getToday();
            }
       };
       
       /*$scope.model.datastoreDateChange = function($toDatastoreDate) {
           //TODO ??????????????????????????????????????????????????????
      };*/
       



       

       
       $scope.returntrueday = function(toDatastoreDate){
           if(toDatastoreDate != "")
           {
           var toDatastoreDateSplit = toDatastoreDate.split('/');
           //alert(toDatastoreDate);
           //toDatastoreDateSplit[0] = toDatastoreDateSplit[0] - 1911;
           toDatastoreDateSplit[0] = parseInt(toDatastoreDateSplit[0], 10) + 1911;
           //alert(toDatastoreDateSplit[0] + toDatastoreDateSplit[1] + toDatastoreDateSplit[2]);
           return toDatastoreDateSplit[0] + toDatastoreDateSplit[1] + toDatastoreDateSplit[2];
           } else {
               return toDatastoreDate;
           }
       };
       
       $scope.model.fileChoose = function() {
           $scope.model.filesize = "";
           $scope.model.toDatastoreSync = "0";
           $scope.model.toDatastoreDate="";
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
          $scope.model.filesize = getReadableFileSizeString($file.size);

          if ("csv" == getFileExtension($file.name)){
//              if ($file.size < 1000000) {
                  $scope.model.toDatastoreSync = "1";
                  $scope.model.toDatastoreDate = getToday();
//              } else {
//                  $scope.model.toDatastoreSync = "0";
//                  $scope.model.toDatastoreDate = getTomorrow();
//              }
          } else {
              $scope.model.toDatastoreSync = "0";
              $scope.model.toDatastoreDate = "";
          }
        };
        

        

        $scope.createAndUpload = function(){


            if(isEmptyOrUndefined($scope.model.description))
                {
                $scope.model.description = "";
                }
            if ($file != "") {
                var index = $("#uploadLocalFile").val().indexOf(".");
                var ext = $("#uploadLocalFile").val().substring(++index);
                if ($scope.fmt != "dataset" && $scope.fmt != "pdf" && $scope.fmt != "image") {
                    alerter.fatal('???????????????????????????!???????????????????????????csv???pdf????????????????????????');
                } else if (($scope.fmt == "dataset" && "csv" != ext) || ($scope.fmt == "pdf" && "pdf" != ext) || ($scope.fmt == "image" && ("jpg" != ext && "png" != ext && "gif" != ext && "jpeg" != ext))) {
                    alerter.fatal('????????????????????????!');
                } else {                
                    cResource().uploadFile({
                        url: 'rest/resdetail/createAndUpload',
                        data: {resourceId: passId, name:$scope.model.name, description:$scope.model.description,toDatastoreSync:$scope.model.toDatastoreSync, toDatastoreDate:$scope.returntrueday($("#toDatastoreDate").val())},
                        file: $file
                        
                    }).success(function(status, headers, config) {
                         // ?????? Callback.
                        //$scope.showFlag = true;
                        //$scope.progress = $scope.progress + ' ';
                        //$("#uploadLocalFile").val("");
                        $scope.files = "";
                        $scope.queryNdelMkVerById(sName, sDesc, 0, true);
                    });
                }
            } else {
                alerter.fatal('????????????????????????');
            }
            

        };
        
        $scope.check = function(name){
            var ischeck = true;

            if (isEmptyOrUndefined(name)) {
                alerter.fatal('???????????????????????????');
                ischeck = false;
                return;
            }
            return ischeck;
        };

        
        $scope.save = function(omitAlerts){
            //$scope.model.ggg='AAA';  //???click update?????????????????????grid????????????????????????
            //alert(angular.toJson($scope.mySelections, true));
            //alert(angular.toJson($scope.model, true));
            //alert(angular.toJson($scope.gridData1, true)); //????????????gridData1?????????
            if ($scope.checkSave($scope.gridData2)) {
                //alert(angular.toJson(omitAlerts, true));
                //alert(passId);
                //ods701e05Service.save('rest/res/saveGrid2', $scope.gridData2, omitAlerts||false).then(
                


                    
                    if($scope.model.toDatastoreSync !=undefined || $scope.model.toDatastoreDate!=undefined)
                    {
                        bootbox.confirm("??????????????????????????????????????????????????????????", function(result) {
                            if (result){
                                //?????????????????????????????????
                                $scope.model.toDatastoreSync = undefined;
                                $scope.model.toDatastoreDate = undefined;

                                ods701e05Service.save('rest/res/saveGrid2',{resourceId: passId, name:$scope.model.name, description:$scope.model.description,
                                    ver: $scope.model.ver, delMk: $scope.model.delMk, isChange:$scope.model.isChange}, omitAlerts||false).then(
                                //ods701e05Service.save('rest/res/saveGrid2', {resourceId: passId}, $scope.gridData2, omitAlerts||false).then(
                                   function(response){
                                       $scope.queryNdelMkVerById(sName, sDesc, selectedRow, true);
                                }, function(response){
                                });
                            }
                         });
                    } else {
                        ods701e05Service.save('rest/res/saveGrid2',{resourceId: passId, name:$scope.model.name, description:$scope.model.description,
                            ver: $scope.model.ver, delMk: $scope.model.delMk, isChange:$scope.model.isChange}, omitAlerts||false).then(
                        //ods701e05Service.save('rest/res/saveGrid2', {resourceId: passId}, $scope.gridData2, omitAlerts||false).then(
                           function(response){
                               $scope.queryNdelMkVerById(sName, sDesc, selectedRow, true);
                        }, function(response){
                        });
                    }
                    
            }
       };
        

       $scope.today =  getToday().replace(/\//g,"-");
       
       $scope.fileRefresh = function(omitAlerts){

           if (isEmptyOrUndefined($scope.wbkName)){
               if ($file != "") {
                   var index = $("#uploadLocalFile").val().indexOf(".");
                   var ext = $("#uploadLocalFile").val().substring(++index);
                   if ($scope.fmt != "dataset" && $scope.fmt != "pdf" && $scope.fmt != "image") {
                       alerter.fatal('???????????????????????????!???????????????????????????csv???pdf????????????????????????');
                   } else if (($scope.fmt == "dataset" && "csv" != ext) || ($scope.fmt == "pdf" && "pdf" != ext) || ($scope.fmt == "image" && ("jpg" != ext && "png" != ext && "gif" != ext  && "jpeg" != ext))) {
                       alerter.fatal('????????????????????????!');
                   } else { 
                       cResource().uploadFile({
                            url: 'rest/resdetail/fileRefreshUpload',
                            data: {resourceId: passId, ver:$scope.model.ver, toDatastoreSync:$scope.model.toDatastoreSyncGrid == "Y" ? "1":"0", toDatastoreDate:$scope.model.toDatastoreDateGrid},
                            file: $file
                        }).success(function(status, headers, config) {
                             // ?????? Callback.
                            //$scope.showFlag = true;
                            //$scope.progress = $scope.progress + ' ';
                            //$("#uploadLocalFile").val("");
                            $scope.files = "";
                            $scope.queryNdelMkVerById(sName, sDesc, selectedRow, true);
                        });
                   }
                } else {
                    alerter.fatal('????????????????????????');
                } 
           } else {
               
               ods701e05Service.create('rest/resdetail/fileRefreshImport',{resourceId: passId, ver:$scope.model.ver, toDatastoreSync:$scope.model.toDatastoreSyncGrid == "Y" ? "1":"0", toDatastoreDate:$scope.model.toDatastoreDateGrid},omitAlerts||false).then(
                       function(response){
                           $scope.queryNdelMkVerById(sName, sDesc, selectedRow, true);
                    }, function(response){

                    }); 
           }

      };
      

      
       
        $scope.checkSave = function(gridData2){
            var ischeck = true;
            var failIdx = "";
            //alert(angular.toJson($scope.gridData1, false));
            //alert(angular.toJson($scope.gridData1.length, false));
            //for (dataIdx in gridData1){  //Array indexOf and filter implementation for IE giving other Issues
            for (var dataIdx = 0; dataIdx < gridData2.length; dataIdx++){
                if (isEmptyOrUndefined(gridData2[dataIdx].name)){
                    intDataIdx = parseInt(dataIdx, 10) + 1;
                    failIdx = failIdx + intDataIdx + ", ";
                }
            }
            if (!isEmptyOrUndefined(failIdx)){
                alerter.fatal('??????' + failIdx.slice(0, -2) + '??????????????????????????????');
                ischeck = false;
                return;
            }
            return ischeck;
        };
        
        
        $scope.reset = function(){
            $scope.model = {};
            $scope.girdObject2.selectAll(false);
            //$scope.gridData2 = [];
            cAlerter.clear();
            state.reset();
            executeOnce = true;
            sName = "";
            sDesc = "";
            selectedRow = 0;
        };

        $scope.mySelections = [];
        $scope.girdObject2 = {
            multiSelect : false,
            data : 'gridData2',
            rowHeight : 40,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                field : 'name',
                displayName : '?????????????????????'
            }, {
                field : 'description',
                displayName : '?????????????????????'
            }, {
                field : 'ver',
                displayName : '??????',
                width: "auto"
            }, {
                field : 'versionDatetime',
                displayName : '????????????',
                cellTemplate: '<span ng-cell-text ng-bind="row.entity.versionDatetime | longToRocDateFilter"></span>'
            }, {
                field : 'workbookVer',
                displayName : 'Workbook??????'
            }, {
                field : 'delMk',
                displayName : '????????????'
            }, {
                field : 'resourceId',
                displayName : 'resourceId',
                visible : false
            }, {
                field : 'toDatastoreSyncGrid',
                displayName : '????????????'
            }, {
                field : 'toDatastoreDateGrid',
                displayName : '??????????????????',
                cellTemplate: '<span ng-cell-text ng-bind="row.entity.toDatastoreDateGrid | longToRocDateNoTimeFilter"></span>'
            }, {
                field : 'toDatastoreSuccessGrid',
                displayName : '??????????????????'
            }/*, {
                field : 'image',
                displayName : 'resourceId',
                visible : true
            }*/
            ],

            selectedItems: $scope.mySelections,
            
            beforeSelectionChange: function(row) {
                //singleSelect: add start
                //$scope.model.name = $("#materialName05Backup").val();
                //$scope.model.description = $("#materialNote05Backup").val();
                //$scope.model.delMk = $("#materialDelMk05Backup").val();
                $scope.model.isChange="N";
                //singleSelect: add end

                row.changed = true;
                return true;
              },
            afterSelectionChange : function(row, event){
                $scope.model =  angular.copy(this.entity);
                if (executeOnce){
                    executeOnce=false;
                    state.select();
                    
                    //???grid??????????????????model????????????????????????????????????grid?????????deselect???????????????text?????????????????????grid?????????????????????
                    if(isEmptyOrUndefined($scope.mySelections))
                    {
                        $scope.model = {};
                        state.query();
                    }
                }
                if (row.changed){
                    console.log("deal with row " + row.rowIndex);
                    row.changed=false;
                  
                    //$scope.model = this.entity;
                    //alert(angular.toJson($scope.model,true));
                    
                    //singleSelect: add start
                    //$("#materialName05Backup").val($scope.model.name) ;
                    //$("#materialNote05Backup").val($scope.model.description);
                    //$("#materialDelMk05Backup").val($scope.model.delMk);
                    //singleSelect: add end
    
                    //$scope.model.image='data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAwACkDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+vO/ib8UoPAYt7G2tVu9WuU8xI3bakSZIDNjk5IIAHoeRgZ9EryDxktxpnxTlnWO0+z6jpsCyy3SKweNJiJbcBs8OpXJ7ZH1pSkoq7Gk27IyfC/x6vZ9Wgt/Eum2sFnO/li6tQyiI8ZJDE7gMjOCCAc89D7rXzN40Wa0+HY0oavBfRWhhIi8tVCMHfdIuMkSMZQCCfuq55zhfojw/cfbPDel3O/f51pFJuznOUBzU06kaivB3Q5RcXaRo0UUVZIjMqIXdgqqMkk4AFePeE4IPij4417xReiSTRbMf2bpkYZlDDhmfggg4wcEf8tMH7tbvxt1qXR/htcxwMUfUZksi4PRWyz/AJqrL/wKrnwgi0+D4badHp8iuA0hmxjcshckhvcAr9RtPQik0nowTsJ4m+Gml6n4O1DS7CELfSIGguJnLMJFO5Rk/dUkYOB0OeTXnHwa8eXGi6j/AMIfrqSRQy3DQ2jS5Bt584aFgemW6ejHHO7j6Br5e+LDQw+O/FEUR8pg1vPHsOD53lJyMdDhmJ96FFJWQ223dn1DRVTSrpr7R7K7fG6e3jlOPVlB/rVumI8/8b3UWqXn9kXFpBJBaSJLieMPvfbwQDxgBj+NYHhXTYdF8bJqdtqUmn2EyFLmwSIeTK2CFJORtAJB6HHOCAxrtdf8JzanqL31rcxq7qAY5QcZHGQw6DpxisGLwvrhu1ha1CITgzmRWRR64B3H6YGfbqPJq/WoVnKKbX4WO6HsZU7M7nWr5NO0S8vGmSIxxHY7kAbzwg57liAB3JArwfX/AAc/iDxEdQuL8fY5ijTxbP3p2jGA/o3c9cnvgV6VdfCfSLwxtcatrkzwuJYvNvNyRyDkMqbdowewAFTad4JuvPH9pTwiBD923ZiZB7kgbfwyfcda6MTGu5xdLtYxpOnytTNHwZfGXTTp5TBswArD7uwk7VHptAxj0ArpqitrWCzhENtCkUY6KgwPr9alrqowlCCjJ3aMpyUpNo//2Q==';
                    
                    state.select();
                    
                    //???grid??????????????????model????????????????????????????????????grid?????????deselect???????????????text?????????????????????grid?????????????????????
                    if(isEmptyOrUndefined($scope.mySelections))
                    {
                        $scope.model = {};
                        state.query();
                    }
                    selectedRow = row.rowIndex;
                //alert(angular.toJson($scope.mySelections,true));
                }
                
                
            }
        };

        //??????????????????????????????????????????
        $scope.$on('ngGridEventData', function(){
            if ($scope.gridData2){
                $scope.girdObject2.selectRow(selectedRow, true);
            }
        });


        $('#materialName05').bind('keyup', function(){
            $scope.model.isChange="Y";
        });
        $('#materialNote05').bind('keyup', function(){
            $scope.model.isChange="Y";
        });
        $('#materialDelMk05').bind('change', function(){
            $scope.model.isChange="Y";
        });
        
      
        
    });
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    


})();

/*$.extend({
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
});*/
function isEmptyOrUndefined(str) {
    return str == '' || angular.isUndefined(str) || str == null;
}



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
    return year + "/" + month + "/" + day;
};

function getFileExtension(filename) {
    //alert((/[.]/.exec(filename)) ? /[^.]+$/.exec(filename) : undefined);
    return (/[.]/.exec(filename)) ? /[^.]+$/.exec(filename) : undefined;
};
