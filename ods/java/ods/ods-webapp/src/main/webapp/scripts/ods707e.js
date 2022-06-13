(function() {
    var app = angular.module("ods707eApp", ['slsCommonModule']);
    app.factory('ods707eService',function(cResource){
        var resource = cResource('rest/:name',{name :"@name"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {
            findAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            }
        };

    });

    app.controller('ods707eController', function($scope,ods707eService,cStateManager,cAlerter,alerter,userHolder,$window) {
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

        $scope.query = function(packageName , packageDescription, startDate, endDate, omitAlerts){
            packageName = packageName||($scope.model.packageName ? $scope.model.packageName :"");
            packageDescription = packageDescription ||($scope.model.packageDescription ? $scope.model.packageDescription :"");
            startDate = startDate ||($scope.startDate ? $scope.startDate :"");
            endDate = endDate ||($scope.endDate ? $scope.endDate :"");
//            alert($scope.model.startDate);
//            alert($scope.model.endDate);
//            alert(startDate);
//            alert(endDate);
            //var result = ods703eService.find({name:name},omitAlerts||false);
            if (startDate != "" && endDate != "") {
                if (endDate < startDate) {
                    alerter.fatal('查詢區間(訖)不可小於查詢區間(起)，謝謝！');
                    return;
                }
            }
            
            var result = ods707eService.findAll('rest/find/all',{packageName:packageName, packageDescription:packageDescription, startDate:startDate, endDate:endDate},omitAlerts||false);
            result.then(function(response){
                if (response.ods707eGridDtoList != ""){
                    state.query();
                    executeOnce = true;
                } else {
                    state.reset();
                }
                
                if (angular.isArray(response.ods707eGridDtoList)) {
                    $scope.gridData = response.ods707eGridDtoList;
                } else {
                    $scope.gridData = [response.ods707eGridDtoList];
                }
            });
            
        };
        
        $scope.reset = function(){
            $scope.model = {};
            $scope.gridData = [];
            cAlerter.clear();
            state.reset();
            $scope.startDate = "";
            $scope.endDate = "";
        };
        
        $scope.packageDetail = function(packageId){
            $window.open('../ODS703E/ods703e_02.html?packageId='+packageId, '_blank', 'menubar=yes,toolbar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,personalbar=yes,fullscreen=yes');
        };

        // grid definition and data
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
                field : 'packageId',
                displayName : '主題ID',
                visible : false
            }, {
                field : 'packageName',
                displayName : '主題名稱'
            }, {
                field : 'packageDescription',
                displayName : '主題說明'
            }, {
                field : 'avgRate',
                displayName : '評分',
                cellTemplate: '<span ng-cell-text>{{row.entity.avgRate}}({{row.entity.totCount}})</span>'
            }, {
                field : 'clickCount',
                displayName : '瀏覽人數'
            }, {
                field : 'followCount',
                displayName : '訂閱人數'
            }, {
                field : '',
                displayName : '功能',
                cellTemplate: '<button type="button" class="btn btn-success btn-sm" data-ng-click="packageDetail(row.getProperty(\'packageId\'))"><span class="glyphicon glyphicon-search"></span></button>'
            } ],
            afterSelectionChange : function(){
                //$scope.model = this.entity;
            }
        };

        $scope.$on('ngGridEventData', function(){
            if ($scope.gridData){
                $scope.girdObject.selectRow(0, true);
            }
        });
    });
})();

