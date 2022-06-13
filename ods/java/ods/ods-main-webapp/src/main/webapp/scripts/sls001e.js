(function() {
    var app = angular.module("sls001eApp", ['slsCommonModule']);

    app.controller('sls001eController', function($scope, cValidation) {
        //自定義regex使用ng-pattern進行驗證，
        $scope.exactly3Digits = /\b\d{3}\b/;
        
        $scope.doQuery = function() {
            if ($scope.validate($scope.apb140eForm.payDate) != false) {
                //驗證通過後，呼叫Service和後端進行溝通
                alert('Send Request!');
            }
        };
        
        //客制驗證
        var customValidation = function(){
            return false;
        };

        cValidation.register('customValidation', customValidation, "測試");
        $scope.certYr = '092';
        $scope.itemData = [ {
            a : 'tester',
            b : 'type1'
        } ];

        $scope.itemGrid = {
            data : 'itemData',
            columnDefs : [ {
                field : 'a',
                displayName : '受款人'
            }, {
                field : 'b',
                displayName : '憑證類別'
            }]
        };
    });
})();
