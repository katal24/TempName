var tempname = angular.module('tempname', []);


tempname.controller('indexController',
    function ($scope, $http) {
        $http.get('/getIndex/').success(function (data) {
            $scope.indexMessage = data;
        });
    }
);


tempname.controller('productsController',
    function ($scope, $http) {
        $http.get('/getProducts/').success(function (data) {
            $scope.products = data;
        });
    }
);