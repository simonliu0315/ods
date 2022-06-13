(function() {
    var app = angular.module("ods708eApp", ['slsCommonModule']);
    app.factory('ods708eService',function(cResource){
        var resource = cResource('rest/:name',{name :"@name"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {
            findAll : function(url,param,omitAlerts){
                var options = {'omitAlerts':omitAlerts||false};
                return resource.execute(url , param,options);
            }
        };

    });

    app.controller('ods708eController', function($scope,ods708eService,cStateManager,cAlerter,alerter,userHolder,$window) {
        var state = cStateManager([{ name: 'init', from: 'NONE',   to: 'INIT'  }]);

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
        
        $scope.execute = function(row, omitAlerts){
            var serviceName = row.entity.serviceName;
            var methodName = row.entity.methodName;
            var startDate = row.entity.startDate;
            var endDate = row.entity.endDate;
            var formObj = {
                    serviceName:serviceName,
                    methodName:methodName,
                    startDate:startDate,
                    endDate:endDate
                };
            if (startDate != "" && endDate != "") {
                if (endDate < startDate) {
                    alerter.fatal('執行區間(訖)不可小於執行區間(起)，謝謝！');
                    return;
                }
            }
            bootbox.confirm("啟動job則無法停止"
                    +"<br/> serviceName:"+serviceName
                    +"<br/> methodName:"+methodName
                    +"<br/> startDate:"+moment(startDate).format("YYYY-MM-DD")
                    +"<br/> endDate:"+moment(endDate).format("YYYY-MM-DD")
                    +"<br/>是否繼續?", function(result) {
                if (result){
                    var result = ods708eService.findAll('rest/execute', formObj, omitAlerts||false);
                    result.then(function(response){                
                        bootbox.alert("啟動！");
                    });
                }
             });
          
      };

        $scope.query = function(omitAlerts){
            var result = ods708eService.findAll('rest/find/all',{},omitAlerts||false);
            result.then(function(response){                
                if (angular.isArray(response.ods708eGridDtoList)) {
                    $scope.gridData = response.ods708eGridDtoList;
                } else {
                    $scope.gridData = [response.ods708eGridDtoList];
                }
            });
        };
        $scope.query();

        // grid definition and data
        $scope.girdObject = {
            multiSelect : false,
            data : 'gridData',
            rowHeight : 50,
            keepLastSelected: false,
            enableColumnResize : true,
            columnDefs : [ {
                width: 50,
                field : '',
                displayName : '項次',
                cellTemplate: '<span ng-cell-text>{{row.rowIndex + 1}}</span>'
            }, {
                field : 'serviceName',
                displayName : '服務',
                visible : false
            }, {
                field : 'methodName',
                displayName : '方法名稱',
                visible : false
            }, {
                field : 'desc',
                displayName : '說明'
            },{
                width: 100,
                field : 'startDate',
                type: 'date',
                displayName : '開始日期',
                //cellTemplate: '<input type="text" data-ng-model="COL_FIELD" data-datepicker-popup="yyyyMMdd" data-datepicker-append-to-body="true">',
                cellTemplate: '<input type="text" data-ng-model="COL_FIELD" data-c-date-picker="{chrono : \'Minguo\', format : \'yyyMMdd\', converter : \'Date\', trigger : \'focus\'}">',
                enableCellEdit:true
            }, {
                width: 100,
                field : 'endDate',
                type: 'date',
                displayName : '結束日期',
                cellTemplate: '<input type="text" data-ng-model="COL_FIELD" data-c-date-picker="{chrono : \'Minguo\', format : \'yyyMMdd\', converter : \'Date\', trigger : \'focus\'}">',
                enableCellEdit:true
            }, {
                width: 50,
                field : '',
                displayName : '功能',
                cellTemplate: '<button type="button" class="btn btn-success btn-sm" data-ng-click="execute(row)"><span class="glyphicon glyphicon-play"></span></button>'
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

