var bookingModule = angular.module('bookingModule', ['ngMaterial']);

bookingModule.controller('BookingController', ['$scope','$http', function ($scope, $http) {
    $scope.rezervare = {
        nume: '',
        prenume: '',
        cnp: null,
        //idCursa: null,
        nrLoc: 1,
        tickets: []
    };

    $scope.step = 1;
    $scope.detaliiBilete = false;

    $scope.selectedFlight = null;

    $scope.flightList = [];

    $scope.dataPlecare = new Date();

    $scope.myDate = new Date();

    $scope.minDate = new Date(
        $scope.myDate.getFullYear(),
        $scope.myDate.getMonth(),
        $scope.myDate.getDate());

    $scope.maxDate = new Date(
        $scope.myDate.getFullYear() + 1,
        $scope.myDate.getMonth(),
        $scope.myDate.getDate());

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
        }).then(function setFlightDetails(response) {
            $scope.detaliiCursa = response.data[0];
            console.log(response);
        }, function errorCB(response) {
            alert("A aparut o eroare!");
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
        $scope.rezervare.tickets.push($scope.bilete);
        console.log($scope.rezervare);
        $http({
            method: 'POST',
            url: '/saveBooking',
            params: { rezervare: $scope.rezervare }
        }).then(function mesajConfirmare(response) {

            alert("Rezervarea a fost salvata cu succes!");
            window.location.reload();

        });

    }
}]);