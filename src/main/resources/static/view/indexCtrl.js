define(['../script/tms', 'jquery'], function(module, $){
    module.controller('indexCtrl', function($rootScope, $scope, $resource, $location, commonService){
        console.log('indexCtrl');
        $location.path('/tms/login');
    });
});