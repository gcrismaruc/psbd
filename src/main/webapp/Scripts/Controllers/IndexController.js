var indexModule = angular.module('indexModule', []);

indexModule.controller('IndexController', ['$scope', '$http', function ($scope, $http) {    

    $scope.flightList = [];

    $http({
        method: 'GET',
        url: '/Curse'
    }).then(function setList(response) {
        $scope.flightList = response.data;
        console.log(response);
    }, function errorCB(response) {
        alert("A aparut o eroare!");
    });

    
}]);