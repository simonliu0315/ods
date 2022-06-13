(function() {
    var app = angular.module("ods703eApp", ['slsCommonModule','odsCommonFilter']);
    app.factory('ods703eService',function(cWindow, cResource){
        var resource = cResource('rest/:name',{name :"@name"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {
            save : function(model){
                return resource.save(model); // post
            },
//            remove : function(bind){
//                return resource.remove(bind); // delete
//            },
            remove : function(url,model){
                return resource.execute(url, model);
            },
            find : function (model,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                if (model.name){
                    return resource.get(model,options);
                }else{
                    return resource.find(model,options);
                }
            },
            findAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url,param,options);
            },
            create : function(model){
                return resource.create(model); // put
            },
            createVer : function(url,model){
                return resource.execute(url, model);
            },
            saveVer : function(url,model){
                return resource.execute(url, model);
            },
            print : function (url , param) {
                cWindow.popup(url, param);
            },
        };

    });
    
    app.filter('range', function() {
        return function(input, total) {
          total = parseInt(total);
          for (var i=0; i<total; i++)
            input.push(i);
          return input;
        };
    });
    
    app.directive(
            "repeatComplete",
            function( $rootScope ) {
 
                // Because we can have multiple ng-repeat directives in
                // the same container, we need a way to differentiate
                // the different sets of elements. We'll add a unique ID
                // to each set.
                var uuid = 0;
 
 
                // I compile the DOM node before it is linked by the
                // ng-repeat directive.
                function compile( tElement, tAttributes ) {
 
                    // Get the unique ID that we'll be using for this
                    // particular instance of the directive.
                    var id = ++uuid;
 
                    // Add the unique ID so we know how to query for
                    // DOM elements during the digests.
                    tElement.attr( "repeat-complete-id", id );
 
                    // Since this directive doesn't have a linking phase,
                    // remove it from the DOM node.
                    tElement.removeAttr( "repeat-complete" );
 
                    // Keep track of the expression we're going to
                    // invoke once the ng-repeat has finished
                    // rendering.
                    var completeExpression = tAttributes.repeatComplete;
 
                    // Get the element that contains the list. We'll
                    // use this element as the launch point for our
                    // DOM search query.
                    var parent = tElement.parent();
 
                    // Get the scope associated with the parent - we
                    // want to get as close to the ngRepeat so that our
                    // watcher will automatically unbind as soon as the
                    // parent scope is destroyed.
                    var parentScope = ( parent.scope() || $rootScope );
 
                    // Since we are outside of the ng-repeat directive,
                    // we'll have to check the state of the DOM during
                    // each $digest phase; BUT, we only need to do this
                    // once, so save a referene to the un-watcher.
                    var unbindWatcher = parentScope.$watch(
                        function() {
 
                            console.info( "Digest running." );
 
                            // Now that we're in a digest, check to see
                            // if there are any ngRepeat items being
                            // rendered. Since we want to know when the
                            // list has completed, we only need the last
                            // one we can find.
                            var lastItem = parent.children( "*[ repeat-complete-id = '" + id + "' ]:last" );
 
                            // If no items have been rendered yet, stop.
                            if ( ! lastItem.length ) {
 
                                return;
 
                            }
 
                            // Get the local ng-repeat scope for the item.
                            var itemScope = lastItem.scope();
 
                            // If the item is the "last" item as defined
                            // by the ng-repeat directive, then we know
                            // that the ng-repeat directive has finished
                            // rendering its list (for the first time).
                            if ( itemScope.$last ) {
 
                                // Stop watching for changes - we only
                                // care about the first complete rendering.
                                //unbindWatcher();
 
                                // Invoke the callback.
                                itemScope.$eval( completeExpression );
 
                            }
 
                        }
                    );
 
                }
 
                // Return the directive configuration. It's important
                // that this compiles before the ngRepeat directive
                // compiles the DOM node.
                return({
                    compile: compile,
                    priority: 1001,
                    restrict: "A"
                });
 
            }
        );

    app.controller('ods703eController', function($scope,ods703eService,cStateManager,cAlerter,alerter,userHolder,$modal,$location,$window) {
        var state = cStateManager([{ name: 'init', from: 'NONE',   to: 'INIT'  },
                      { name: 'query', from: ['INIT','RESETD'],   to: 'QUERYED'},
                      { name: 'print', from: ['QUERYED'],   to: 'PRINTED'},
                      { name: 'reset', from: ['INIT','QUERYED'],   to: 'RESETD'}]);

        // 取得使用者物件
        userHolder.getUser().then(function(response) {
            console.log(response.data);

        }, function(response) {
            if (200 != response.status) {
                alert("無法取得使用者物件!");
            }
        });

        
        state.init();
        $scope.model = {};
        $scope.gridData1 = [];
        $scope.gridData2 = [];
        $scope.newest = [];
        var executeOnce = true;
        $scope.layout = [];
        $scope.patternData = [];
        $scope.packageLayout = [];
        $scope.category = [];
        $scope.commonRes = [];
        $scope.taWidth = [3,2,1];
        var isValid = true;
        $scope.commonMetadata = [];
        $scope.extraMetadata = [];
        var selectedRow = 0;

        $scope.delMkToUnpublish = function(){
            //如果刪除註記,要將此版本下架
            if ($scope.model.delMk == "true") {
                $scope.model.isPublished = false;
            }
        };

        $scope.publishToUndelMk = function(){
            //如果要上架,要將此版本的刪除註記設為N
            if ($scope.model.delMk == "true") {
                if($scope.openPublishConfirm()) {
                    $scope.model.delMk = false;
                    return true;
                } else {
                    return false;
                }
            }
            return true;
            
        };

        $scope.checkDuplicateResources = function() {
            for (var i = 0; i < $scope.gridData2.length - 1; i++) {
                for (var j = i + 1; j < $scope.gridData2.length; j++) {
                    if ($scope.gridData2[i].resourceId == $scope.gridData2[j].resourceId 
                            && $scope.gridData2[i].resourceVer == $scope.gridData2[j].resourceVer) {
                        alerter.fatal('您所選擇的素材及案例：'+$scope.gridData2[i].resName+'重覆，請檢查，謝謝！');
                        return false;
                    }
                }
            }
            $scope.model.ods703eTab2DialogDtoList = $scope.gridData2;
            return true;
        };
        
        $scope.parsePackageLayout = function() {
            $scope.packageLayout = [];
            for (var i = 0; i < $scope.patternData.length; i++) {
                for (var j = 0; j < $scope.patternData[i]; j++) {
                    var td = document.getElementById('row'+i+'col'+j);
                    var img = td.getElementsByTagName('img');
                    var textarea = td.getElementsByTagName('textarea');
                    var tmpPL;
                    var id = {rowPosition:i, columnPosition:j};
                    if (img[0] != null) {
                        var resId = img[0].id.slice(5,img[0].id.indexOf('resVer'));
                        var resVer = img[0].id.slice(img[0].id.indexOf('resVer')+6);
                        tmpPL = {id:id, resourceId:resId, resourceVer:resVer};
                        $scope.packageLayout.push(tmpPL);
                    } else if (textarea[0] != null) {
                        var resId = textarea[0].id.slice(5,textarea[0].id.indexOf('resVer'));
                        var resVer = textarea[0].id.slice(textarea[0].id.indexOf('resVer')+6);
                        tmpPL = {id:id, resourceId:resId, resourceVer:resVer, description:textarea[0].value};
                        $scope.packageLayout.push(tmpPL);
                    } 
//                        else {
//                        tmpPL = {id:id, resourceId:null, resourceVer:null};
//                    }
//                    
//                    $scope.packageLayout.push(tmpPL);
                }
            }
        };
        
        $scope.showInvalidMsg = function() {
            alerter.fatal('尚有欄位檢核錯誤，請檢查每個Tab，謝謝！');
        };
        
        $scope.createVer = function(){
            if ($scope.validate($scope.ods703eForm.name) != false) {
                if ($scope.checkDuplicateResources()) {
                    $scope.model.packageId = packageId;
                    //新增時預設未發佈
                    $scope.model.isPublished = false;
                    $scope.delMkToUnpublish();
                    $scope.parsePackageLayout();
                    $scope.model.odsPackageLayoutList = $scope.packageLayout;
                    $scope.model.packageMetatemplateDto = $scope.commonMetadata;
                    $scope.model.odsPackageVersionExtraList = $scope.extraMetadata;
                    ods703eService.createVer('rest/createVer', $scope.model).then(function(response){
                        $scope.queryVer($scope.model.packageId, 0, true);
                    });
                }
            } else {
                $scope.showInvalidMsg();
                isValid = false;
            }
        };
        
        $scope.saveVer = function(){
            if ($scope.validate($scope.ods703eForm.name) != false) {
                if ($scope.checkDuplicateResources()) {
                    $scope.delMkToUnpublish();
                    $scope.parsePackageLayout();
                    $scope.model.odsPackageLayoutList = $scope.packageLayout;
                    $scope.model.packageMetatemplateDto = $scope.commonMetadata;
                    $scope.model.odsPackageVersionExtraList = $scope.extraMetadata;
                    //若編排素材沒有資料，按下修改時，則系統自動下架
                    if ($scope.packageLayout.length == 0) {
                        $scope.model.isPublished = false;
                    }
                    //alert(angular.toJson($scope.packageLayout, false));
                    //alert(angular.toJson($scope.model.ods703eTab2DialogDtoList, false));
                    ods703eService.saveVer('rest/saveVer', $scope.model).then(function(response){
                        $scope.queryVer($scope.model.id.packageId, selectedRow,true);
                    });                
                }
            } else {
                $scope.showInvalidMsg();
                isValid = false;
            }
        };
        
        $scope.chkIfCanPublish = function() {
            if ($scope.packageLayout.length == 0) {
                alerter.fatal('編排素材及案例沒有資料，不可發佈！');
                return false;
            }
            return true;
        };
        
        $scope.publishVer = function(){
            if ($scope.validate($scope.ods703eForm.name) != false) {
                if ($scope.publishToUndelMk()) {                    
                    if ($scope.checkDuplicateResources()) {
                        $scope.parsePackageLayout();
                        $scope.model.odsPackageLayoutList = $scope.packageLayout;
                        $scope.model.packageMetatemplateDto = $scope.commonMetadata;
                        $scope.model.odsPackageVersionExtraList = $scope.extraMetadata;
                        if ($scope.chkIfCanPublish()) {
                            $scope.model.isPublished = true;
                            ods703eService.saveVer('rest/saveVer', $scope.model).then(function(response){
                                $scope.queryVer($scope.model.id.packageId, selectedRow, true);
                            });
                        }
                    }
                }
            } else {
                $scope.showInvalidMsg();
                isValid = false;
            }
        };
        
        $scope.unPublishVer = function(){
            if ($scope.validate($scope.ods703eForm.name) != false) {
                if ($scope.checkDuplicateResources()) {
                    $scope.model.isPublished = false;
                    $scope.parsePackageLayout();
                    $scope.model.odsPackageLayoutList = $scope.packageLayout;
                    $scope.model.packageMetatemplateDto = $scope.commonMetadata;
                    $scope.model.odsPackageVersionExtraList = $scope.extraMetadata;
                    ods703eService.saveVer('rest/saveVer', $scope.model).then(function(response){
                        $scope.queryVer($scope.model.id.packageId, selectedRow, true);
                    });
                }
            } else {
                $scope.showInvalidMsg();
                isValid = false;
            }
        };
        
        $scope.queryVer = function(packageId, currentRow, omitAlerts){
            packageId = packageId||($scope.model.packageId ? $scope.model.packageId :"");
            var result = ods703eService.findAll('rest/find/ver',{packageId:packageId},omitAlerts||false);
            result.then(function(response){
                if (response.odsPackageVersionList != ""){
                    state.query();
                    executeOnce = true;
                    selectedRow = currentRow;
                } else {
                    state.reset();
                }
                
                if (angular.isArray(response.odsPackageVersionList)) {
                    $scope.gridData1 = response.odsPackageVersionList;
                } else {
                    $scope.gridData1 = [response.odsPackageVersionList];
                }
                
                if (response.odsPackageVersionList.length == 0) {
                    //如果查無資料,執行queryVersionMetadata,帶出templateMetadata
                    $scope.queryVersionMetadata("","0",true);
                }
                
            });
        };
        
        $scope.queryVerAll = function(omitAlerts){
            var result = ods703eService.findAll('rest/find/verAll',{packageId:packageId},omitAlerts||false);
            result.then(function(response){
                if (response.odsPackageVersionList != ""){
                    state.query();
                    executeOnce = true;
                } else {
                    state.reset();
                }
                
                if (angular.isArray(response.odsPackageVersionList)) {
                    $scope.gridData1 = response.odsPackageVersionList;
                } else {
                    $scope.gridData1 = [response.odsPackageVersionList];
                }
            });
        };
        
        $scope.queryCategory = function(omitAlerts){
            var result = ods703eService.findAll('rest/find/cat',{},true);
            result.then(function(response){
                if (angular.isArray(response)) {
                    $scope.category = response;
                } else {
                    $scope.category = [response];
                }                
            });
        };
        
        $scope.queryRes = function(packageId, packageVer, omitAlerts){
            var result = ods703eService.findAll('rest/find/packRes',{packageId:packageId, packageVer:packageVer},omitAlerts||false);
            result.then(function(response){                
                if (angular.isArray(response.ods703eTab2DialogDtoList)) {
                    $scope.gridData2 = response.ods703eTab2DialogDtoList;
                } else {
                    $scope.gridData2 = [response.ods703eTab2DialogDtoList];
                }
                //alert(angular.toJson($scope.gridData2, false));
                $scope.queryPackLayout(packageId, packageVer, true);
            });
        };
        
        $scope.queryCommonRes = function(omitAlerts){
            var result = ods703eService.findAll('rest/find/commonRes',{},true);
            result.then(function(response){
                if (angular.isArray(response.ods703eTab2DialogDtoList)) {
                    $scope.commonRes = response.ods703eTab2DialogDtoList;
                } else {
                    $scope.commonRes = [response.ods703eTab2DialogDtoList];
                }                
            });
        };
        
        $scope.queryVersionMetadata = function(packageId, packageVer, omitAlerts){
            var result = ods703eService.findAll('rest/find/versionMetadata',{packageId:packageId, packageVer:packageVer},omitAlerts||false);
            result.then(function(response){
                if (angular.isArray(response.packageMetatemplateDto)) {
                    $scope.commonMetadata = response.packageMetatemplateDto;
                } else {
                    $scope.commonMetadata = [response.packageMetatemplateDto];
                }
                if (angular.isArray(response.odsPackageVersionExtraList)) {
                    $scope.extraMetadata = response.odsPackageVersionExtraList;
                } else {
                    $scope.extraMetadata = [response.odsPackageVersionExtraList];
                }
            });
        };
        
        $scope.fillLayout = function(){
            for (var i = 0; i < $scope.patternData.length; i++) {
                for (var j = 0; j < $scope.patternData[i]; j++) {
                    var td = document.getElementById('row'+i+'col'+j);
                    var resName;
                    var resFormat;
                    //alert(angular.toJson($scope.packageLayout[0], false));
                    //先清空
                    td.innerHTML = '';
                    for (var k = 0; k <$scope.packageLayout.length; k++) {
                        if ($scope.packageLayout[k].id.rowPosition == i && $scope.packageLayout[k].id.columnPosition == j &&
                                $scope.packageLayout[k].resourceId != null && $scope.packageLayout[k].resourceId != "") {
                            //Package Version Resources
                            for (var l = 0; l <$scope.gridData2.length; l++) {
                                if ($scope.gridData2[l].resourceId == $scope.packageLayout[k].resourceId && 
                                        $scope.gridData2[l].resourceVer == $scope.packageLayout[k].resourceVer) {
                                    resName = $scope.gridData2[l].resName + " 版本" + $scope.gridData2[l].resourceVer;
                                    resFormat = $scope.gridData2[l].format;
                                    var html = '<div class="drag ng-binding" id="a'+i+j+'c0" style="border-style: none; cursor: move;">' + resName + '<br>';
                                    if (resFormat == "image") {
                                        html = html + '<img id="resId'+$scope.gridData2[l].resourceId+'resVer'+$scope.gridData2[l].resourceVer+'" src="/ods/ODS308E/public/resource/'+$scope.gridData2[l].resourceId+'/image/'+$scope.gridData2[l].resourceId+'-'+$scope.gridData2[l].resourceVer+'.png" style="width:150px;height:120px">';                                        
                                    } else {
                                        html = html + '<img id="resId'+$scope.gridData2[l].resourceId+'resVer'+$scope.gridData2[l].resourceVer+'" src="../images/'+resFormat+'.png" style="width:150px;height:120px">';
                                    }
                                    html = html + '</div>';
                                    td.innerHTML = html;
                                }
                            }
                            //Common Resources
                            for (var l = 0; l <$scope.commonRes.length; l++) {
                                if ($scope.commonRes[l].resourceId == $scope.packageLayout[k].resourceId && 
                                        $scope.commonRes[l].resourceVer == $scope.packageLayout[k].resourceVer) {
                                    //alert(angular.toJson($scope.packageLayout[k], false));
                                    //alert("patternData:" + $scope.patternData[i] + "tdWidth:" + $scope.taWidth[$scope.patternData[i] - 1]);
                                    resName = 'COMMON RESOURCE';
                                    //var html = '<div class="drag ng-binding" id="a'+i+j+'c0" style="border-style: none; cursor: move;">' + resName + '<br>';
                                    var html = '<div class="drag ng-binding" id="a'+i+j+'c0" style="border-style: none; cursor: move;">';
                                    html = html + '<textarea id="resId'+$scope.commonRes[l].resourceId+'resVer'+$scope.commonRes[l].resourceVer+'" rows="4" cols="20" style="width:100%;">'+$scope.packageLayout[k].description+'</textarea>';
                                    html = html + '</div>';
                                    td.innerHTML = html;
                                }                                
                            }
                        }
                    }
                }
            }
            $scope.initDrag();
        };
        
        $scope.queryPackLayout = function(packageId, packageVer, omitAlerts){
            var result = ods703eService.findAll('rest/find/packLayout',{packageId:packageId, packageVer:packageVer},omitAlerts||false);
            result.then(function(response){                
                if (angular.isArray(response)) {
                    $scope.packageLayout = response;
                    //alert(angular.toJson(response, false));
                } else {
                    $scope.packageLayout = [response];
                }
                $scope.fillLayout();                
            });
        };
        
        $scope.preview = function(){
            $window.open('../ODS303E/'+$scope.model.id.packageId+'/'+$scope.model.id.ver+'/','_blank');
        };
        
        $scope.print = function(){
            //var ctx = '${pageContext.request.contextPath}';
            var ctx = $location.absUrl().slice(0, $location.absUrl().indexOf("/ods"));
            var model = {
                    name : $scope.model.name,
                    description : $scope.model.description,
                    packageId : $scope.model.id.packageId,
                    ver : $scope.model.id.ver,
                    ctx : ctx,
                    _csrf : $("meta[name='_csrf']").attr("content")

                };
            ods703eService.print('./print', model);
            //$window.open('./print?name='+$scope.model.name,'_blank');

            // 更新button的狀態(按下print後)
            state.print();            
        };
        
        //網頁起始時先依packageId查詢版本資訊, 素材及案例分類及共用的素材及案例
        //var packageId = $location.absUrl().slice($location.absUrl().indexOf('?') + 1);
        var packageId = $.getUrlVar('packageId');
        $scope.queryVer(packageId, 0);
        $scope.queryCategory();
        $scope.queryCommonRes();
        
          
        $scope.reset = function(){            
            $scope.model = {};
            //$scope.gridData1 = [];
            $scope.girdObject1.selectAll(false);
            $scope.gridData2 = [];
            cAlerter.clear();
            state.reset();
            //executeOnce = true;
            $scope.layout = [];
            $scope.patternData = [];
            $scope.packageLayout = [];
            isValid = true;
            $scope.commonMetadata = [];
            $scope.extraMetadata = [];
            $scope.queryVersionMetadata("","0",true);
            selectedRow = 0;
        };

        $scope.removeResource = function(idx){
            //alert("DDD");
            $scope.gridData2.splice(idx, 1);
        };
        
        $scope.ifNewestVer = function(idx){
            if ($scope.newest[idx]) {
                $scope.gridData2[idx].resourceVer = 0;
            } else {
                $scope.gridData2[idx].resourceVer = $scope.gridData2[idx].ver;
            }
        };
        
        $scope.initDrag = function(){
            //REDIPS.drag.enableDrag('init');
            REDIPS.drag.init();
        };
        
        $scope.chkPattern = function(e){
            if ($scope.patternData.length == 0) {
                return true;;
            }
            return false;
        };
        
        $scope.addMetadata = function(){
            $scope.extraMetadata.push({dataKey:"",dataValue:""});
        };
        
        $scope.removeMetadata = function(idx){
            $scope.extraMetadata.splice(idx, 1);
        };
        
        $scope.girdObject1 = {
            multiSelect : false,
            data : 'gridData1',
            rowHeight : 40,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                field : 'id.ver',
                displayName : '版本'
            }, {
                field : 'name',
                displayName : '名稱'
            }, {
                field : 'versionDatetime',
                displayName : '日期',
                cellTemplate: '<span ng-cell-text ng-bind="row.entity.versionDatetime | longToRocDateFilter"></span>'
            }, {
                field : 'isPublished',
                displayName : '是否發佈',
                cellTemplate: '<span ng-cell-text ng-bind="row.entity.isPublished | booleanFilter"></span>'
                //cellFilter:booleanFilter
            } ],            
            beforeSelectionChange: function(row) {
                row.changed = true;
                return true;
            },
            afterSelectionChange : function(row, event) {
                $scope.model = angular.copy(this.entity);
                //alert(angular.toJson($scope.model, false)); 
                if (executeOnce){
                    if(!angular.isUndefined($scope.model)) {
                        if ($scope.model.delMk == true || $scope.model.delMk == "true") {
                            $scope.model.delMk = "true";
                        } else {
                            $scope.model.delMk = "false";
                        }
                        if ($scope.model.pattern != null) {
                            $scope.patternData = $scope.model.pattern.split(","); 
                        } else {
                            $scope.patternData = [];
                        }
                        $scope.queryRes($scope.model.id.packageId, $scope.model.id.ver, true);
                        //$scope.queryPackLayout($scope.model.id.packageId, $scope.model.id.ver);
                        $scope.queryVersionMetadata($scope.model.id.packageId, $scope.model.id.ver, true);
                    }                    
                    executeOnce=false;
                }
                if (row.changed){
                    if(!angular.isUndefined($scope.model)) {
                        if ($scope.model.delMk == true || $scope.model.delMk == "true") {
                            $scope.model.delMk = "true";
                        } else {
                            $scope.model.delMk = "false";
                        }
                        cAlerter.clear();
                        if ($scope.model.pattern != null) {
                            $scope.patternData = $scope.model.pattern.split(",");
                        } else {
                            $scope.patternData = [];
                        }
                        $scope.queryRes($scope.model.id.packageId, $scope.model.id.ver, true);
                        //$scope.queryPackLayout($scope.model.id.packageId, $scope.model.id.ver);
                        $scope.queryVersionMetadata($scope.model.id.packageId, $scope.model.id.ver, true);
                        selectedRow = row.rowIndex;
                    }
                    state.query();
                    row.changed=false;
                }
            }
        };
        $scope.girdObject2 = {
            multiSelect : false,
            data : 'gridData2',
            rowHeight : 40,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                field : '',
                displayName : '序號',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'resourceId',
                displayName : '素材及案例ID',
                visible : false
            }, {
                field : 'resName',
                displayName : '素材及案例名稱'
            }, {
                field : 'resDesc',
                displayName : '素材及案例說明'
            }, {
                field : '',
                displayName : '永遠讀取最新版本',
                cellTemplate: '<input type="checkbox" data-ng-model="newest[row.rowIndex]" data-ng-change="ifNewestVer(row.rowIndex)" style="width:25px; height:25px;">'
            }, {
                field : 'ver',
                displayName : '選擇的素材及案例版本',
                visible : false
            }, {
                field : 'resourceVer',
                displayName : '素材及案例版本',
                cellTemplate: '<span ng-cell-text>{{row.entity.resourceVer}}</span>　<button type="button" class="btn btn-primary" data-ng-click="choseVer(row.rowIndex)"><span class="glyphicon glyphicon-check"></span> 選擇版本</button>'
            }, {
                field : 'versionDatetime',
                displayName : '版本日期',
                cellTemplate: '<span ng-cell-text>{{row.entity.versionDatetime}}</span><button type="button" class="btn btn-primary" data-ng-click="choseVer(row.rowIndex)"><span class="glyphicon glyphicon-check"></span> 選擇版本</button>',
                visible : false
            }, {
                field : 'format',
                displayName : '素材及案例格式',
                visible : false
            }, {
                field : '',
                displayName : '功能',
                cellTemplate: '<button type="button" class="btn btn-danger btn-sm" data-ng-click="removeResource(row.rowIndex)"><span class="glyphicon glyphicon-remove"></span></button>'
            } ],
            afterSelectionChange : function(){
//                $scope.model = angular.copy(this.entity);
            }
        };
        
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
        
        $(document).on( 'shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
            //console.log(e.target); // activated tab
            if (!isValid) {
                $scope.validate($scope.ods703eForm.name);
            }
            
        });
        
        //tab2 設定
        //預設顯示第一個 Tab
        var _showTab = 0;
        var $defaultLi = $('ul.tabs li').eq(_showTab).addClass('active');
//        $($defaultLi.find('a').attr('href')).siblings().hide();
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
        
        //dialog
        
        $scope.openPublishConfirm = function() {
            if (confirm("目前版本為刪除狀態，若發佈將會取消刪除狀態，是否確認發佈？")) { 
                return true; 
            } else {
                return false; 
            }
        };
        
        $scope.resourceDialogOptions = {
            templateUrl: 'ods703e_choseResource.html',
            resolve: {category: function(){ return angular.copy($scope.category);}, resources: function(){ return angular.copy($scope.gridData2);}}, 
            controller: 'ChoseResourceController'
        };
        
        $scope.openChoseResource = function() {
            var modalInstance = $modal.open($scope.resourceDialogOptions);            
            modalInstance.result.then(function(result){                
                if (result.status == 'add') {                    
                    $scope.gridData2 = result.resources;
                    for (var i=0; i<$scope.gridData2.length; i++) {
                        $scope.gridData2[i].resourceVer = $scope.gridData2[i].resourceVer ? $scope.gridData2[i].resourceVer : 0;
                        $scope.gridData2[i].ver = $scope.gridData2[i].ver ? $scope.gridData2[i].ver : 0;
                        //alert($scope.gridData2[i].format);
                    }
                }
            });
        };
        
        $scope.resourceVerDialogOptions = {
            templateUrl: 'ods703e_choseResourceVer.html',
            resolve: {resourceId: function(){ return angular.copy($scope.resourceId);}}, 
            controller: 'ChoseResourceVerController'
        };
        
        $scope.choseVer = function(idx){
            $scope.resourceId = $scope.gridData2[idx].resourceId;
            var modalInstance = $modal.open($scope.resourceVerDialogOptions);            
            modalInstance.result.then(function(result){                
                if (result.status == 'chose') {
                    $scope.gridData2[idx].ver = result.version[0].id.ver;
                    $scope.gridData2[idx].versionDatetime = result.version[0].versionDatetime;
                    $scope.ifNewestVer(idx);
                }
            });
        };
        
        $scope.layoutDialogOptions = {
            templateUrl: 'ods703e_choseLayout.html',
            //resolve: {category: function(){ return angular.copy($scope.category);}, resources: function(){ return angular.copy($scope.gridData2);}}, 
            controller: 'ChoseLayoutController'
        };
        
        $scope.openChoseLayout = function() {
            var modalInstance = $modal.open($scope.layoutDialogOptions);            
            modalInstance.result.then(function(result){                
                if (result.status == 'import') {                    
                    $scope.layout = result.layout[0];
                    $scope.model.pattern = $scope.layout.pattern;
                    $scope.patternData = $scope.layout.pattern.split(",");                    
                    //alert($scope.patternData);
                }
            });
        };
        
    });

    //dialog
    
    app.controller('ChoseResourceController', function($scope,ods703eService,$modalInstance,category,resources) {        
        $scope.resource = [];                
        $scope.category = category;
        $scope.selectedResources = resources;
        
        $scope.queryResource = function(omitAlerts){
            resName = $scope.resource.resName ? $scope.resource.resName :"";
            catId = $scope.resource.catId ? $scope.resource.catId :"";
            var result = ods703eService.findAll('rest/find/res',{resName:resName,catId:catId},true);
            result.then(function(response){
                if (angular.isArray(response.ods703eTab2DialogDtoList)) {
                    $scope.resourceGridData = response.ods703eTab2DialogDtoList;
                } else {
                    $scope.resourceGridData = [response.ods703eTab2DialogDtoList];
                }
            });
        };
        //起始列出所有resource
        $scope.queryResource();       
                
        $scope.addResource = function(){
            var result = {status:'add', resources: $scope.selectedResources};
            $modalInstance.close(result);
        };
        
        $scope.choseResourceGird = {
            multiSelect : true,
            data : 'resourceGridData',
            rowHeight : 40,
            selectedItems:$scope.selectedResources,
            columnDefs : [ {
                field : '',
                displayName : '序號',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'resourceId',
                displayName : '素材及案例ID',
                visible : false
            }, {
                field : 'resName',
                displayName : '素材及案例名稱'
            }, {
                field : 'resDesc',
                displayName : '素材及案例說明'
            }, {
                field : 'catName',
                displayName : '素材及案例分類'
            }, {
                field : 'format',
                displayName : '素材及案例格式',
                visible : false
            }, {
                field : '',
                displayName : '圖示',
                cellTemplate: '<div ng-if="row.entity.format == \'image\'"><img src="/ods/ODS308E/public/resource/{{row.getProperty(\'resourceId\')}}/image/{{row.getProperty(\'resourceId\')}}-0.png"  style="width:60px;heigth:60px"></div><div ng-if="row.entity.format != \'image\'"><img src="../images/{{row.entity.format}}.png"}"  style="width:60px;heigth:60px"></div>'                    
            } ],
            afterSelectionChange : function(){
                $scope.resource = angular.copy(this.entity);
            }
        };
    });
    
    app.controller('ChoseResourceVerController', function($scope,ods703eService,$modalInstance,resourceId) {
        $scope.resourceId = resourceId;
        $scope.selectedVersion = [];
        
        $scope.queryVersions = function(omitAlerts){
            resourceId = $scope.resourceId ? $scope.resourceId :"";
            var result = ods703eService.findAll('rest/find/resVer',{resourceId:resourceId},true);
            result.then(function(response){
                if (angular.isArray(response.odsResourceVersionList)) {
                    $scope.versionGridData = response.odsResourceVersionList;
                } else {
                    $scope.versionGridData = [response.odsResourceVersionList];
                }
            });
        };
        //起始列出所有resource
        $scope.queryVersions();       
                
        $scope.choseVersion = function(){
            var result = {status:'chose', version: $scope.selectedVersion};
            $modalInstance.close(result);
        };
        
        $scope.choseResourceVerGird = {
            multiSelect : false,
            data : 'versionGridData',
            rowHeight : 40,
            selectedItems:$scope.selectedVersion,
            columnDefs : [ {
                field : 'id.ver',
                displayName : '版本'
            }, {
                field : 'name',
                displayName : '版本名稱'
            }, {
                field : 'description',
                displayName : '版本說明'
            }, {
                field : 'versionDatetime',
                displayName : '版本日期'
            } ],
            afterSelectionChange : function(){
                //$scope.version = angular.copy(this.entity);
            }
        };
    });
    
    app.controller('ChoseLayoutController', function($scope,ods703eService,$modalInstance) {        
        $scope.layout = [];
        $scope.selectedLayout = [];
        $scope.patternData = [];
        
        $scope.queryLayout = function(name , omitAlerts){
            name = name||($scope.layout ? $scope.layout.name :"");
            var result = ods703eService.findAll('rest/find/layout',{name:name},omitAlerts||false);
            result.then(function(response){
                if (angular.isArray(response)){
                    $scope.layoutGridData = response;
                }else{
                    $scope.layoutGridData = [response];
                }
                
                //在查詢後取得一亂數，用來給予grid的image參數，迫使瀏覽器抓取新資料
                $scope.getDynamicNum = new Date().getTime();
            });
        };
        //起始列出所有Layout
        $scope.queryLayout();       
                
        $scope.importLayout = function(){
            var result = {status:'import', layout: $scope.selectedLayout};
            $modalInstance.close(result);
        };
        
        $scope.choseLayoutGird = {
            multiSelect : false,
            data : 'layoutGridData',
            rowHeight : 40,
            selectedItems:$scope.selectedLayout,
            columnDefs : [ {
                field : '',
                displayName : '序號',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'id',
                displayName : '範本ID',
                visible : false
            }, {
                field : 'name',
                displayName : '範本名稱'
            }, {
                field : 'pattern',
                displayName : '範本樣式',
                visible : false
            }, {
                field : '',
                displayName : '範本預覽圖',
                cellTemplate: '<img src="/ods/ODS308E/public/layout/{{row.getProperty(\'id\')}}/image/{{row.getProperty(\'id\')}}.png?t={{getDynamicNum}}" style="width:60px;heigth:60px">'
                //cellTemplate: '<img src="../images/sample5.jpg" style="width:60px;heigth:60px">'
                //cellTemplate: '<span ng-cell-text>id:{{row.getProperty(\'id\')}}</span>'
            } ],
            afterSelectionChange : function(){
                $scope.layout = angular.copy(this.entity);
                $scope.patternData = $scope.layout.pattern.split(",");
            }
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

