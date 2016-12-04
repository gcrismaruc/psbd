var indexModule = angular.module('indexModule', []);

indexModule.controller('IndexController', ['$scope', '$http', function ($scope, $http) {    

    $scope.flightList = [];

    $http({
        method: 'GET',
        url: ''
    }).then(function setList(response) {
        $scope.flightList = response;
    }, function errorCB(response) {
        alert("A aparut o eroare!");
    });

    /*$scope.flightList = [
        {
            plecare: 'Iasi',
            sosire: 'Bucuresti',
            pret: 10,
            nrLocEc: 15,
            nrLocBus: 2,
            avionId: 10003
        },
        {
            plecare: 'Bucuresti',
            sosire: 'Iasi',
            pret: 10,
            nrLocEc: 15,
            nrLocBus: 2,
            avionId: 1000222
        },
        {
            plecare: 'Bucuresti',
            sosire: 'Roma',
            pret: 20,
            nrLocEc: 20,
            nrLocBus: 10,
            avionId: 100022
        },
        {
            plecare: 'Roma',
            sosire: 'Bucuresti',
            pret: 20,
            nrLocEc: 20,
            nrLocBus: 10,
            avionId: 10002
        },
        {
            plecare: 'Londra',
            sosire: 'Bucuresti',
            pret: 30,
            nrLocEc: 5,
            nrLocBus: 1,
            avionId: 1000111
        },
        {
            plecare: 'Bucuresti',
            sosire: 'Londra',
            pret: 30,
            nrLocEc: 5,
            nrLocBus: 1,
            avionId: 100011
        },
        {
            plecare: 'Iasi',
            sosire: 'Constanta',
            pret: 40,
            nrLocEc: 15,
            nrLocBus: 5,
            avionId: 10001
        },
        {
            plecare: 'Constanta',
            sosire: 'Iasi',
            pret: 40,
            nrLocEc: 15,
            nrLocBus: 5,
            avionId: 1000
        }
    ];*/

    $scope.setDefaultFlight = function (id) {
        $http({
            method: 'GET',
            url: '',
            data: { avionid: id }
        });

        alert(id);
    }
}]);