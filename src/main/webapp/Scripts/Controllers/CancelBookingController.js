var cancelBookingModule = angular.module('cancelBookingModule', []);

cancelBookingModule.controller('CancelBookingController', ['$scope', '$http',  function ($scope, $http) {

    $scope.bookingList = [];

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

    $scope.back = function () {
        window.location.reload();
    }

    $scope.getBookingList = function () {
        $http({
            method: 'GET',
            url: '/getpaid',
            params: {
                cnp : $scope.bookingFilter.cnp,
                nume : $scope.bookingFilter.nume,
                prenume : $scope.bookingFilter.prenume
            }
        }).then(function setList(response) {
            $scope.bookingList = response.data;
            $scope.step = 2;
            console.log(response);
        }, function errorCB(response) {
            alert("A aparut o eroare!");
        });
    }

    $scope.cancelBooking = function (id) {
        $http({
            method: 'POST',
            url: '/cancelbooking',
            params: {
                rezervareID: id
            }
        }).then(function mesajConfirmare(response) {
            alert("Rezervarea a fost stearsa cu succes!" + response.statusText);
        });
        window.location.reload();
    }


}]);