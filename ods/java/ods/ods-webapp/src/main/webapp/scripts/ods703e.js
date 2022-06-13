(function() {
    var app = angular.module("ods703eApp", ['slsCommonModule','odsCommonFilter']);
    app.factory('ods703eService',function(cResource){
        var resource = cResource('rest/:name',{name :"@name"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {
            remove : function(url,model){
                return resource.execute(url, model);
            },
            removeUaaCheck : function(url,model){
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
            create : function(url,model){
                return resource.execute(url, model);
            },
            save : function(url,model){
                return resource.execute(url, model);
            },
            deletePackageDocument : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url,param,options);
            }
        };

    });

    app.controller('ods703eController', function($scope,ods703eService,cStateManager,cAlerter,alerter,userHolder,$location,$window,cResource) {
        var state = cStateManager([{ name: 'init', from: 'NONE',   to: 'INIT'  },
                      { name: 'query', from: ['INIT','RESETD'],   to: 'QUERYED'},
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
        $scope.gridData = [];
        $scope.commonMetadata = [];
        $scope.extraMetadata = [];
        $scope.packageTags = [];
        $scope.packageDocuments = [];
        var executeOnce = true;
        $scope.files = [];
        $scope.docs = [];
        $scope.docDescription = "";
        var sName = "";
        var sDesc = "";
        var isValid = true;
        var cName = "";
        var cDesc = "";
        var cPid = "";
        var selectedRow = 0;
        
        $scope.showInvalidMsg = function() {
            alerter.fatal('尚有欄位檢核錯誤，請檢查每個Tab，謝謝！');
        };
        
        $scope.chkFile = function() {
            if ($scope.files.length != 0) {
                var $file = $scope.files[0];
                var re = /\.(jpg|jpeg|gif|png)$/i;  //允許的圖片副檔名
                if (!re.test($file.name)) {
                    alerter.fatal('只允許上傳JPG/JPEG/PNG/GIF影像檔，謝謝！');
                    return false;
                }
            }
            return true;
        };
        
        $scope.chkDocumentFile = function($file) {
            var re = /\.(pdf|ppt|pptx|odp|doc|docx|odt|xls|xlsx|ods)$/i;  //允許的文件副檔名
            if (!re.test($file.name)) {
                alerter.fatal('只允許上傳PDF/PPT/PPTX/ODP/DOC/DOCX/ODT/XLS/XLSX/ODS文件檔，謝謝！');
                return false;
            }
            return true;
        };
        
        $scope.chkType = function() {
            if ($scope.model.type == null || $scope.model.type == "") {
                alerter.fatal('請輸入主題類型，謝謝！');
                return false;
            } else if ($scope.model.type == "01") {
                $scope.model.code = "";
            } else if ($scope.model.type == "02") {
                if ($scope.model.code == null || $scope.model.code == "") {
                    alerter.fatal('請輸入主題代碼，謝謝！');
                    return false;
                } else if ($scope.model.code.length > 10) {
                    alerter.fatal('主題代碼最多由十個字元組成，謝謝！');
                    return false;
                }
            }            
            return true;
        };
        
        $scope.save = function(){
            //$scope.currentTab = 'packageInfo';
            if ($scope.validate($scope.ods703eForm.name) != false) {
                if ($scope.chkFile()) {
                    if ($scope.chkType()) {
                        $scope.model.odsPackageTagList = $scope.packageTags;
                        $scope.model.packageMetatemplateDto = $scope.commonMetadata;
                        $scope.model.odsPackageExtraList = $scope.extraMetadata;
                        cName = $scope.model.name;
                        cDesc = $scope.model.description;
                        ods703eService.save('rest/save', $scope.model).then(function(response){                    
                            $scope.query(sName, sDesc, selectedRow, true);                
                        });
                    }
                }
            } else {
                $scope.showInvalidMsg();
                isValid = false;
            }
        };
        
        $scope.removeConfirm = function() {
            if (confirm("是否確認刪除？")) { 
                return true; 
            } else {
                return false; 
            }
        };
        
        $scope.processRemove = function(){
            ods703eService.remove('rest/delete', $scope.model).then(function(response){
                $scope.model = {};
                $scope.query(sName, sDesc, 0, true);
            });
        };
        
        $scope.remove = function(){
            ods703eService.removeUaaCheck('rest/uaaCheck', $scope.model).then(function(response){
                if (response.isUaaCheck == "true") {
                    if (confirm("此主題已有設定資料權，是否確認刪除？")) {
                        $scope.processRemove();
                    }
                } else {
                    if ($scope.removeConfirm()) {
                        $scope.processRemove();
                    }
                }
            });            
        };
        
        $scope.create = function(){
            if ($scope.validate($scope.ods703eForm.name) != false) {
                if ($scope.chkFile()) {
                    if ($scope.chkType()) {
                        $scope.model.odsPackageTagList = $scope.packageTags;
                        $scope.model.packageMetatemplateDto = $scope.commonMetadata;
                        $scope.model.odsPackageExtraList = $scope.extraMetadata;
                        cName = $scope.model.name;
                        cDesc = $scope.model.description;
                        //alert(angular.toJson($scope.model, false));
                        ods703eService.create('rest/create', $scope.model).then(function(response){
                               //$scope.query(sName, sDesc, selectedRow, true);
                            $scope.query("", "", 0, true);
                        });
                    }
                }
            } else {
                $scope.showInvalidMsg();
                isValid = false;
            }

        };
        $scope.query = function(name , description, currentRow, omitAlerts){
            if (angular.isUndefined(name) || name == null) {
                name = $scope.model.name ? $scope.model.name :"";
            }
            if (angular.isUndefined(description) || description == null) {
                description = $scope.model.description ? $scope.model.description :"";
            }
            sName = name;
            sDesc = description;
            //var result = ods703eService.find({name:name},omitAlerts||false);
            var result = ods703eService.findAll('rest/find/all',{name:name, description:description},omitAlerts||false);
            result.then(function(response){
                if (response.packageAndVersionList != ""){
                    state.query();
                    executeOnce = true;
                    selectedRow = currentRow;
                } else {
                    state.reset();
                }
                
                if (angular.isArray(response.packageAndVersionList)) {
                    $scope.gridData = response.packageAndVersionList;
                } else {
                    $scope.gridData = [response.packageAndVersionList];
                }
            });
        };
        $scope.queryPid = function(name , description, omitAlerts){
            if ($scope.files.length != 0) {
                var result = ods703eService.findAll('rest/find/all',{name:name, description:description},omitAlerts||false);
                result.then(function(response){
                    if (angular.isArray(response.packageAndVersionList)) {
                        cPid = response.packageAndVersionList[0].id;
                    } else {
                        cPid = [response.packageAndVersionList][0].id;
                    }
                    $scope.onFileSelect($scope.files);
                    //$scope.ClearFileUpload();
                });
            }
        };
        queryTags = function(id, omitAlerts){
            id = id||($scope.model.id ? $scope.model.id :"");
            var result = ods703eService.findAll('rest/find/tags',{id:id},omitAlerts||false);
            result.then(function(response){
                if (angular.isArray(response.odsPackageTagList)) {
                    $scope.packageTags = response.odsPackageTagList;
                } else {
                    $scope.packageTags = [response.odsPackageTagList];
                }
            });
        };
        queryMetadata = function(id, omitAlerts){
            id = id||($scope.model.id ? $scope.model.id :"");
            var result = ods703eService.findAll('rest/find/metadata',{id:id},omitAlerts||false);
            result.then(function(response){
                if (angular.isArray(response.packageMetatemplateDto)) {
                    $scope.commonMetadata = response.packageMetatemplateDto;
                } else {
                    $scope.commonMetadata = [response.packageMetatemplateDto];
                }
                if (angular.isArray(response.odsPackageExtraList)) {
                    $scope.extraMetadata = response.odsPackageExtraList;
                } else {
                    $scope.extraMetadata = [response.odsPackageExtraList];
                }
            });
        };
        queryDocuments = function(id, omitAlerts){
            id = id||($scope.model.id ? $scope.model.id :"");
            var result = ods703eService.findAll('rest/find/documents',{id:id},omitAlerts||false);
            result.then(function(response){
                if (angular.isArray(response.odsPackageDocumentList)) {
                    $scope.packageDocuments = response.odsPackageDocumentList;
                } else {
                    $scope.packageDocuments = [response.odsPackageDocumentList];
                }
            });
        };
        $scope.removePackageDocument = function(documentId, packageId, omitAlerts) {
            var result = ods703eService.deletePackageDocument('rest/delete/document',{documentId:documentId, packageId:packageId},omitAlerts||false);
            result.then(function(response){
                if (angular.isArray(response.odsPackageDocumentList)) {
                    $scope.packageDocuments = response.odsPackageDocumentList;
                } else {
                    $scope.packageDocuments = [response.odsPackageDocumentList];
                }
            });
        };
        $scope.listMetadata = function(){
            for (var i=0; i<$scope.commonMetadata.length;i++) {
                alert($scope.commonMetadata[i].dataKey+":"+$scope.commonMetadata[i].dataValue);
            }
            for (var i=0; i<$scope.extraMetadata.length;i++) {
                alert($scope.extraMetadata[i].dataKey+":"+$scope.extraMetadata[i].dataValue);
            }
        };
        
        $scope.onSelect = function($files) {
            $scope.files = $files;
        };
        
        $scope.onFileSelect = function() {
            if ($scope.files.length != 0) {
                var $file = $scope.files[0];                
                cResource().uploadFile({
                    url: 'rest/fileUpload',
                    data: {packageId: cPid},
                    file: $file
                }).success(function(data, status, headers, config) {
                    // 成功 Callback.
                    //$scope.model.imageUrl = data.data;
                    //alert('success!');
                });
            }
        };
        
        $scope.ClearFileUpload = function() {
//            var fu = document.getElementById('img');
//            if (fu != null) {
//                document.getElementById('img').outerHTML = fu.outerHTML;
//            }
            $scope.files = [];
            $scope.docs = [];
            $scope.docDescription = "";
        };
        
        $scope.onDocSelect = function($files) {
            $scope.docs = $files;
        };
        
        $scope.onDocUpload = function() {
            if ($scope.docs.length != 0) {
                var $doc = $scope.docs[0];
                if ( ! $scope.chkDocumentFile($doc) ) {
                    return false;
                }
                cResource().uploadFile({
                    url: 'rest/docUpload',
                    data: {packageId: $scope.model.id, description: $scope.docDescription},
                    file: $doc
                }).success(function(data, status, headers, config) {
                    cAlerter.clear();
                    queryDocuments($scope.model.id);
                });
            }
        };
        
        $scope.reset = function(){
            $scope.model = {};
            $scope.gridData = [];
            cAlerter.clear();
            state.reset();
            $scope.commonMetadata = [];
            $scope.extraMetadata = [];
            $scope.packageTags = [];
            $scope.packageDocuments = [];
            executeOnce = true;
            queryMetadata();
            $scope.ClearFileUpload();
            sName = "";
            sDesc = "";
            isValid = true;
            cName = "";
            cDesc = "";
            cPid = "";
            $scope.ods703eForm.$setPristine();
            selectedRow = 0;
        };

        $scope.addTag = function(){
            $scope.packageTags.push({tagName:""});
        };
        
        $scope.removeTag = function(idx){
            $scope.packageTags.splice(idx, 1);
        };
        
        $scope.addMetadata = function(){
            $scope.extraMetadata.push({dataKey:"",dataValue:""});
        };
        
        $scope.removeMetadata = function(idx){
            $scope.extraMetadata.splice(idx, 1);
        };
        
        $scope.verEdit = function () {
            //$window.location.href="ods703e_02.html?"+$scope.model.id;
            $window.open('ods703e_02.html?packageId='+$scope.model.id,'_blank');
        };

        $scope.girdObject = {
            multiSelect : false,
            data : 'gridData',
            rowHeight : 40,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                field : '',
                displayName : '項次',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'id',
                displayName : '主題ID',
                visible : false
            }, {
                field : 'name',
                displayName : '主題名稱'
            }, {
                field : 'description',
                displayName : '主題說明'
            }, {
                field : 'ver',
                displayName : '最新版本'
            }, {
                field : 'versionDatetime',
                displayName : '版本日期',
                cellTemplate: '<span ng-cell-text ng-bind="row.entity.versionDatetime | longToRocDateFilter"></span>'
            } ],
            beforeSelectionChange: function(row) {
                row.changed = true;
                return true;
            },
            afterSelectionChange : function(row, event) {
                $scope.model = angular.copy(this.entity);                
                if (executeOnce){                    
                    $scope.queryPid(cName, cDesc, true);
                    queryTags($scope.model.id);
                    queryMetadata($scope.model.id);
                    queryDocuments($scope.model.id);
                    executeOnce=false;
                }
                if (row.changed){
                    queryTags($scope.model.id);
                    queryMetadata($scope.model.id);
                    queryDocuments($scope.model.id);
                    selectedRow = row.rowIndex;
                    cAlerter.clear();
                    $scope.ClearFileUpload();
                    row.changed=false;
                }
            }
        };

        $scope.packageDocumentGrid = {
                multiSelect : false,
                data : 'packageDocuments',
                rowHeight : 40,
                keepLastSelected: false,
                enableColumnResize : true,
                columnDefs : [ {
                    field : 'id',
                    displayName : '文件id',
                    visible : false
                }, {
                    field : 'packageId',
                    displayName : '主題id',
                    visible : false
                }, {
                    field : 'description',
                    displayName : '文件說明'
                }, {
                    field : 'filename',
                    displayName : '文件檔名'
                }, {
                    field : '',
                    displayName : '操作',
                    cellTemplate: '<button type="button" class="btn btn-danger btn-sm" data-ng-click="removePackageDocument(row.entity.id,row.entity.packageId)"><span class="glyphicon glyphicon-remove"></span></button>'
                } ]
            };
        
        $scope.$on('ngGridEventData', function(){            
            if ($scope.gridData){
                $scope.girdObject.selectRow(selectedRow, true);
            }         
        });
        
        $(document).on( 'shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
            //console.log(e.target); // activated tab
            if (!isValid) {
                $scope.validate($scope.ods703eForm.name);
            }
            
        });
        
        //初始執行一次queryMetadata,帶出templateMetadata
        queryMetadata();

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
        
    });

})();

