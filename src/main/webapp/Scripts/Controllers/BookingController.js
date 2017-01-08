var bookingModule = angular.module('bookingModule', ['ngMaterial']);

bookingModule.controller('BookingController', ['$scope','$http', function ($scope, $http) {
    $scope.rezervare = {
        nume: '',
        prenume: '',
        cnp: null,
        dataPlecare: null,
        dataRetur: null,
        orasPlecare: '',
        //idCursa: null,
        nrLoc: 1,
        tickets: []
    };

    $scope.step = 1;
    $scope.retur = false;
    $scope.detaliiBilete = false;

    $scope.selectedFlight = null;

    $scope.flightList = [];

    $scope.dataPlecare = new Date();

    $scope.myDate = new Date();

    $scope.dataRetur = new Date();

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


    //Detalii locuri
    $scope.listaBilete = []; //lista cu toate biletele
    $scope.bilet = {
        loc: null,
        tip: null,
        pozitie: null
    };
    //$scope.loc = []; //array in care se salveaza biletele selectate
    $scope.locuriOcupate = []; //lista cu locuri rezervate, primita de la baza de date
    $scope.bileteEc = 0; //numarul de bilete ec. al cursei
    $scope.bileteBs = 0; //numarul de bilete bs. al cursei
    $scope.numarBilete = 0; //numarul de bilete al rezervarii
    var i = 0;
    var j = 0;
    //--------------

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

    $scope.dusIntors = function () {
        return $scope.retur;
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
            //
        }, function errorCB(response) {
            alert("A aparut o eroare!");
        });
        $scope.step = 2;
    }

    $scope.setBookingDetails = function () {
        $scope.detaliiBilete = true;
        $scope.bilete = new Array($scope.rezervare.nrLoc);

        $scope.bileteEc = $scope.detaliiCursa.nr_loc_ec;
        $scope.bileteBs = $scope.detaliiCursa.nr_loc_bs;
        $scope.numarBilete = $scope.rezervare.nrLoc;
        //$scope.locuriOcupate = $scope.detaliiCursa.locuriOcupate;
        $scope.locuriOcupate = [1, 2, 5, 6, 10, 25, 36, 62];
        //$scope.loc = new Array($scope.rezervare.nrLoc);

        for (i = 1; i <= $scope.bileteEc + $scope.bileteBs; i++) {
            if (i <= $scope.bileteEc) {
                $scope.bilet = { loc: i, tip: " E", pozitie: " Mijloc" };
                if (i % 5 == 1) {
                    $scope.bilet = { loc: i, tip: " E", pozitie: " Geam" };
                }
                if (i % 5 == 3 || i % 5 == 4) {
                    $scope.bilet = { loc: i, tip: " E", pozitie: " Culoar" };
                }
                $scope.listaBilete.push($scope.bilet);
            }
            else {
                $scope.bilet = { loc: i, tip: " B", pozitie: " Mijloc" };
                if (i % 5 == 1) {
                    $scope.bilet = { loc: i, tip: " B", pozitie: " Geam" };
                }
                if (i % 5 == 3 || i % 5 == 4) {
                    $scope.bilet = { loc: i, tip: " B", pozitie: " Culoar" };
                }
                $scope.listaBilete.push($scope.bilet);
            }
        }

        for (i = 0; i < $scope.listaBilete.length; i++) {
            for (j = 0; j < $scope.locuriOcupate.length; j++) {
                if ($scope.listaBilete[i].loc == $scope.locuriOcupate[j])
                    $scope.listaBilete.splice(i, 1);
            }
        }
    }

    $scope.ticketDetails = function () {
        return $scope.detaliiBilete;
    }

    $scope.saveBooking = function () {
        console.log($scope.bilete);
        $scope.rezervare.tickets.push($scope.bilete);
        $scope.rezervare.dataPlecare = $scope.dataPlecare;
        $scope.rezervare.dataRetur = $scope.dataRetur;
        $scope.rezervare.orasPlecare = $scope.detaliiCursa.oras_plecare;
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