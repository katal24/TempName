angular.module('hello', ['ngRoute']).config(function ($routeProvider, $httpProvider) {
    //wstrzknelimsy ngroute do modulu

    //tu sie ustawia kontoler i szablon html w zaleznosci od mapowania
    $routeProvider.when('/', {
        //index to slash
        templateUrl: 'home.html',
        controller: 'home'
    }).when('/login', {
        templateUrl: 'login.html',
        controller: 'navigation'//nawigation tez jest na indeksie
    }).when('/products', {
        templateUrl: 'products.html',
        controller: 'productsController'
    }).when('/newAccount', {
        templateUrl: 'newAccount.html',
        controller: 'newAccountController'
    }).otherwise('/');


    //<wylacza okno dialogowe popup przegladarki pytajace o haslo
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

}).controller(
    'navigation',

    function ($rootScope, $scope, $http, $location, $route) {

        $scope.tab = function (route) {
            return $route.current && route === $route.current.controller;
        };

        var authenticate = function (credentials, callback) {

            var headers = credentials ? {
                authorization: "Basic "
                + btoa(credentials.username + ":"
                + credentials.password)
            } : {};

            $http.get('user', {headers: headers}).success(function (data) {
                //mapowanie user zwraca Principal a on ma getName()
                if (data.name) {
                    $rootScope.authenticated = true;
                    $rootScope.loggedUser = credentials.username;

                } else {
                    $rootScope.authenticated = false;
                }
                callback && callback($rootScope.authenticated);
            }).error(function () {
                $rootScope.authenticated = false;
                callback && callback(false);
            });

        }

        //proboje samo bez wysylania fomularza jesli juz mamy to ciastko a jedynie odswiezylismy
        authenticate();

        $scope.credentials = {};
        $scope.login = function () {
            authenticate($scope.credentials, function (authenticated) {
                if (authenticated) {
                    console.log("Login succeeded")
                    $location.path("/");
                    $scope.error = false;
                    $rootScope.authenticated = true;
                } else {
                    console.log("Login failed")
                    $location.path("/login");
                    $scope.error = true;
                    $rootScope.authenticated = false;
                }
            })
        };

        $scope.logout = function () {
            $http.post('logout', {}).success(function () {
                $rootScope.authenticated = false;
                $location.path("/");
            }).error(function (data) {
                console.log("Logout failed")
                $rootScope.authenticated = false;
            });
        }

    }).controller('home', function ($scope, $http) {
        $http.get('/getResources/').success(function (data) {

            $scope.greeting = data;


        })
    }) .controller('newAccountController', function ($scope, $http) {
            $http.post('/receiveNewConfiguration', configuration).success(function (data) {
                $scope.products = data;
                $rootScope.errorNewAccount = false;
            }).error(function (data) {
                console.log("Setting up account failed")
                $rootScope.errorNewAccount = true;
            });

    }).controller('productsController', function ($scope, $http) {
        $http.get('/getProducts/').success(function (data) {
            $scope.products = data;
        });
    }
);
