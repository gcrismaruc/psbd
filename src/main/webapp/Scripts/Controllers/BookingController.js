var bookingModule = angular.module('bookingModule', []);

bookingModule.controller('BookingController', ['$scope','$http', function ($scope, $http) {
    $scope.rezervare = {
        nume: '',
        prenume: '',
        cnp: null,
        idCursa: null,
        nrLoc: null
    };

    $scope.submitBooking = function () {
        //alert('asda');
        console.log($scope.rezervare);
        $http({
            method: 'POST',
            url: '/booking',
            data: { rezervare: $scope.rezervare }
        });
    };
}]);