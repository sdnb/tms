define(['../script/tms', 'jquery'], function(module, $){
    module.controller('notFoundCtrl', function($rootScope, $scope, $resource, $location, commonService){
        var loginCookie = commonService.getCookie('staff_token');
        if(loginCookie == ''){
            window.location.href = '/login';
        }

    });
});