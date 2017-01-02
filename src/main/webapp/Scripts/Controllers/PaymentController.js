var paymentModule = angular.module('paymentModule', []);

paymentModule.controller('PaymentController', ['$scope', '$http', function ($scope, $http) {

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

    $scope.doPay = function (id) {
        $http({
            method: 'POST',
            url: '/dopay',
            params: { rezervareID: id }
        }).then(function mesajConfirmare(response){
            alert("Plata a fost efectuata cu succes!");
        });
        window.location.reload();
    }


}]);