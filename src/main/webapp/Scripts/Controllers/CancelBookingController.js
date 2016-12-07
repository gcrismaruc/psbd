var cancelBookingModule = angular.module('cancelBookingModule', []);

cancelBookingModule.controller('CancelBookingController', ['$scope', '$http', '$state', function ($scope, $http, $state) {

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

    $state.back = function () {
        $state.reload();
    }

    $scope.getBookingList = function () {
        $http({
            method: 'GET',
            url: '/Curse',
            params: {
                filter: $scope.bookingFilter
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
            url: '/booking',
            data: { rezervareID: id }
        });
        $state.reload();
    }


}]);