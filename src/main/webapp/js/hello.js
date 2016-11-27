/**
 * Created by Gheorghe on 11/26/2016.
 */
angular.module('demo', [])
    .controller('Hello', function($scope, $http) {
        $http.get('/test/model').
        then(function(response) {
            console.log(response.data);
            $scope.greetings = response.data;
        });
    });