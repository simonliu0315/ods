(function() {
    var app = angular.module("sls002eApp", ['slsCommonModule']);
    app.factory('sls002eService',function(cResource){
        var resource = cResource('rest/:ped',{ped :"@ped"});
        // save:post/remove:delete/create:post/ get:get/find:get
        return {
            save : function(model){
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
            }
        };

    });

    app.controller('sls002eController', function($scope,sls002eService,cStateManager,cAlerter,userHolder) {
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
        $scope.gridData = {};
        $scope.changeAgeList = [{code:"01", name:"N"}, {code:"02", name:"Y"}]; 
        $scope.changeAgeType = $scope.changeAgeList[0];
        
        $scope.save = function(){
            sls002eService.save($scope.model,{ped:$scope.model.ped}).then(function(response){
                $scope.query($scope.model.ped,true);
            });

        };
        $scope.remove = function(){
            sls002eService.remove({ped:$scope.model.ped}).then(function(response){
                $scope.query("",true);
            });
        };

        $scope.create = function(){
            sls002eService.create($scope.model,{ped:$scope.model.ped}).then(
               function(response){
                   $scope.query($scope.model.ped,true);
            }, function(response){

            });

        };
        $scope.query = function(ped , omitAlerts){
            ped = ped||($scope.model ? $scope.model.ped :"");
            //var result = sls002eService.find({ped:ped},omitAlerts||false);
            var result = sls002eService.findAll('rest/find/all',{ped:ped},omitAlerts||false);
            result.then(function(response){
                if (response){
                    state.query();
                }

                if (angular.isArray(response)){
                    $scope.gridData = response;
                }else{
                    $scope.gridData = [response];
                }
            });
        };
        $scope.reset = function(){
            $scope.model = {};
            $scope.gridData = [];
            cAlerter.clear();
            state.reset();
        };

        // grid definition and data
        $scope.girdObject = {
            multiSelect : false,
            data : 'gridData',
            columnDefs : [ {
                field : 'ped',
                displayName : '期別'
            }, {
                field : 'styr',
                displayName : '起訖年度(1)'
            }, {
                field : 'edyr',
                displayName : '起訖年度(2)'
            }, {
                field : 'changeAge',
                displayName : '新年度是否更改機關'
            }, {
                field : 'nam',
                displayName : '期別名稱'
            } ],
            afterSelectionChange : function(){
                $scope.model = this.entity;
            }
        };

        $scope.$on('ngGridEventData', function(){
            if ($scope.gridData){
                $scope.girdObject.selectRow(0, true);
            }
        });
    });
})();
