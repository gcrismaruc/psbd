var updateModule = angular.module('updateModule', []);

updateModule.controller('UpdateController', ['$scope', '$http', function ($scope, $http) {

    $(document).ready(function () {
        $('[data-toggle="tooltip"]').tooltip();
    });

    $scope.bookingList = [];
    $scope.selectedRezervareID = 2;
    $scope.reservationDetails;

    $scope.bookingFilter = {
        nume: '',
        prenume: '',
        cnp: null
    };

    $scope.step = 1;

    $scope.firstStep = function () {
        return $scope.step == 1;
    }

    $scope.secondStep = function () {
        return $scope.step == 2;
    }

    $scope.thirdStep = function () {
        return $scope.step == 3;
    }

    $scope.back = function () {
        window.location.reload();
    }

    $scope.getBookingList = function () {
        $http({
            method: 'GET',
            url: '/getbookingforcancel',
            params: {
                nume: $scope.bookingFilter.nume,
                prenume: $scope.bookingFilter.prenume,
                cnp: $scope.bookingFilter.cnp
            }
        }).then(function setList(response) {
            $scope.bookingList = response.data;
            $scope.step = 2;
            console.log(response);
        }, function errorCB(response) {
            alert("A aparut o eroare!");
        });
    }

    $scope.edit = function (id) {
        $scope.selectedRezervareID=id;
        $http({
            method: 'GET',
            url: '/editBooking',
            params: { rezervareID: id }
        }).then(function setDefaultValues(response) {
            console.log(response.data);
            $scope.reservationDetails = response.data;
            $scope.step = 3;
        }, function errorCB(response) {
            alert("A aparut o eroare!");
        });
        //window.location.reload();
    }

    $scope.submit = function (id) {
        console.log($scope.selectedRezervareID);
        $http({
            method: 'POST',
            url: '/updateBooking',
            params: {
                rezervareID : $scope.selectedRezervareID,
                rezervare: $scope.reservationDetails }
        }).then(function mesajConfirmare(response) {

            alert("Rezervarea a fost salvata cu succes!");
            window.location.reload();

        });
    }
}]);