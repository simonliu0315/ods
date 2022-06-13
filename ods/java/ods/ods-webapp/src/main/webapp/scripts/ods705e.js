(function() {
    var app = angular.module("ods705eApp", ['slsCommonModule']);
    app.factory('ods705eService',function(cResource){
        var resource = cResource('rest/:name',{name :"@name"});
        // save:post/remove:delete/create:put/ get:get/find:get
        return {
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
                return resource.execute(url , param,options);
            },
            create : function(url,model){
                return resource.execute(url, model);
            },
            save : function(url,model){
                return resource.execute(url, model);
            }
        };

    });

    app.controller('ods705eController', function($scope,ods705eService,cStateManager,cAlerter,alerter,userHolder,cValidation) {
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

        var patternValidation = function(){
            if ($scope.model.pattern == "") {
                return false;
            }
            return true;
        };

        cValidation.register('patternValidation', patternValidation, "請至少新增一列");
        
        $scope.chkPattern = function(){
            if ($scope.model.pattern == "") {
                alerter.fatal('範本設計：請至少新增一列，謝謝！');
                return false;
            }
            return true;
        };
        
        state.init();
        $scope.gridData = [];
        $scope.model = {pattern:""};
        $scope.patternDesign = "1";
        $scope.patternData = [];
        var executeOnce = true;
        var sName = "";
        var selectedRow = 0;
        
        $scope.save = function(){
            if ($scope.validate($scope.ods705eForm.name) != false && $scope.chkPattern() != false) {
                ods705eService.save('rest/save', $scope.model,{}).then(function(response){
                    $scope.query(sName, selectedRow,true);
                });
            }

        };
        
        $scope.removeConfirm = function() {
            if (confirm("是否確認刪除？")) { 
                return true; 
            } else {
                return false; 
            }
        };
        
        $scope.remove = function(){
//            ods705eService.remove({id:$scope.model.id}).then(function(response){
//                $scope.query("",true);
//            });
            if ($scope.removeConfirm()) {
                ods705eService.remove('rest/delete', $scope.model).then(function(response){
                    $scope.model = {};
                    $scope.query(sName, 0,true);
                });
            }
        };

        $scope.create = function(){
            if ($scope.validate($scope.ods705eForm.name) != false && $scope.chkPattern() != false) {
                ods705eService.create('rest/create', $scope.model,{}).then(
                   function(response){
                       $scope.query(sName, selectedRow, true);
                }, function(response){
    
                });
            }

        };
        $scope.query = function(name, currentRow, omitAlerts){
            if (angular.isUndefined(name) || name == null) {
                name = $scope.model.name ? $scope.model.name :"";
            }
            sName = name;
            //var result = ods705eService.find({name:name},omitAlerts||false);
            var result = ods705eService.findAll('rest/find/all',{name:name},omitAlerts||false);
            result.then(function(response){
                if (response != ""){
                    state.query();
                    executeOnce = true;
                    selectedRow = currentRow;
                } else {
                    state.reset();
                }
                
                if (angular.isArray(response)){
                    $scope.gridData = response;
                }else{
                    $scope.gridData = [response];
                }
                //在查詢後取得一亂數，用來給予grid的image參數，迫使瀏覽器抓取新資料
                $scope.getDynamicNum = new Date().getTime();
            });
        };
        $scope.reset = function(){
            //$scope.ods705eForm.$setPristine();
            $scope.gridData = [];
            cAlerter.clear();
            state.reset();
            $scope.model = {pattern:""};
            $scope.patternDesign = "1";
            $scope.patternData = [];
            executeOnce = true;
            sName = "";
            selectedRow = 0;
        };
        
        $scope.addPattern = function(){
            if ($scope.model.pattern == "") {
                $scope.model.pattern = $scope.patternDesign;
            } else {
                $scope.model.pattern = $scope.model.pattern + "," + $scope.patternDesign;
            }

            $scope.patternData = $scope.model.pattern.split(",");
            //alert($scope.patternData);
            
            //var el = angular.element("#patternPreview");
            //patternNum++;
            //alert(patternNum);
//            el.append("<div id='p"+patternNum+"'");
//            el.append("<img src='../images/column"+$scope.patternDesign+".jpg' class='img-polaroid' height='100' width='650'> ");
//            el.append("<button type='button' class='btn btn-danger' data-ng-click='removePattern('p"+patternNum+"')'><span class='glyphicon glyphicon-minus'></span> 刪除</button>");
//            el.append("</div>");
        };
        
        $scope.removePattern = function(idx){
            $scope.patternData.splice(idx, 1);
            $scope.model.pattern = $scope.patternData.join();
            //alert($scope.patternData);
            //alert($scope.model.pattern);
        };
        
//        function removePattern(pn) {
//            alert("fff");
//        }

        // grid definition and data
//        $scope.gridData = [
//            {
//                sno : '1',
//                styr: '範本1',
//                edyr: '素材1說明'
//            },
//            {
//                sno : '2',
//                styr: '範本2',
//                edyr: '素材2說明'
//            },
//            {
//                sno : '3',
//                styr: '範本3',
//                edyr: '素材3說明'
//            },
//        ];
        $scope.girdObject = {
            multiSelect : false,
            data : 'gridData',
            rowHeight : 70,
            keepLastSelected: false,
            enableColumnResize : true,
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
                cellTemplate: '<div><img src="/ods/ODS308E/public/layout/{{row.getProperty(\'id\')}}/image/{{row.getProperty(\'id\')}}.png?t={{getDynamicNum}}" style="width:100px;heigth:100%;margin-top:4px;margin-bottom:4px;"></div>'
                //cellTemplate: '<img th:src="@{\'/ODS308E/public/layout/{{row.getProperty(\'id\')}}/image/{{row.getProperty(\'id\')}}.png\'}" style="width:60px;heigth:60px">'
                //cellTemplate: '<img src="../images/sample5.jpg" style="width:60px;heigth:60px">'
                //cellTemplate: '<span ng-cell-text>id:{{row.getProperty(\'id\')}}</span>'
            } ],
            beforeSelectionChange: function(row) {
                row.changed = true;
                return true;
            },
            afterSelectionChange : function(row, event){
                $scope.model = angular.copy(this.entity);                
                if (executeOnce){                    
                    $scope.patternData = $scope.model.pattern.split(",");
                    executeOnce=false;
                }
                if (row.changed){
                    $scope.patternData = $scope.model.pattern.split(",");
                    selectedRow = row.rowIndex;
                    row.changed=false;
                }
            }
        };

        $scope.$on('ngGridEventData', function(){
            if ($scope.gridData){
                $scope.girdObject.selectRow(selectedRow, true);                
            }
        });
    });
})();

