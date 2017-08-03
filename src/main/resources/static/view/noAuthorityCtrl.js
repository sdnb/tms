define(['../script/tms', 'jquery'], function(module, $){
    module.controller('noAuthorityCtrl', function($rootScope, $scope, $resource, $location, commonService){
        var loginCookie = commonService.getCookie('staff_token');
        if(loginCookie == ''){
            window.location.href = '/login';
        }
        console.log('no Authority');
    });
});
