var bookingModule = angular.module('bookingModule', ['ngMaterial']);

bookingModule.controller('BookingController', ['$scope','$http', function ($scope, $http) {
    $scope.rezervare = {
        nume: '',
        prenume: '',
        cnp: null,
        //idCursa: null,
        nrLoc: 1
    };

    $scope.step = 1;
    $scope.detaliiBilete = false;

    $scope.selectedFlight = null;

    $scope.flightList = [];

    $scope.dataPlecare = new Date();

    $scope.minDate = new Date();
    $scope.minDate.setFullYear(2016, 0, 1);

    $scope.maxDate = new Date();
    $scope.maxDate.setFullYear(2017, 0, 1);

    $scope.detaliiCursa = {
        oras_plecare: 'asa',
        oras_sosire: 'dsadsa',
        pret: 20,
        nr_loc_ec: 15,
        nr_loc_bs: 5
    };

    $scope.bilete = [];

    $http({
        method: 'GET',
        url: '/Curse'
    }).then(function setList(response) {
        $scope.flightList = response.data;
        console.log(response);
    }, function errorCB(response) {
        alert("A aparut o eroare!");
    });

    $scope.submitBooking = function () {
        $http({
            method: 'POST',
            url: '/booking',
            data: { rezervare: $scope.rezervare }
        });
    };

    //Checking the step of the view
    $scope.firstStep = function () {
        return $scope.step == 1;
    }

    $scope.secondStep = function () {
        return $scope.step == 2;
    }


    $scope.goToStepTwo = function () {
        $http({
            method: 'GET',
            url: '/availableFlight',
            params: { avionID : $scope.selectedFlight,
                dataPlecare: $scope.dataPlecare
            }
        });
        $scope.step = 2;
    }

    $scope.setBookingDetails = function () {
        $scope.detaliiBilete = true;
        $scope.bilete = new Array($scope.rezervare.nrLoc);
    }

    $scope.ticketDetails = function () {
        return $scope.detaliiBilete;
    }

    $scope.saveBooking = function () {
        console.log($scope.bilete);
    }
}]);