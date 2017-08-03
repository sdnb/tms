define(['../script/tms', 'jquery'], function(module, $){
    module.controller('xtCtrl', function($rootScope, $scope, $resource, $location, commonService){
        var loginCookie = commonService.getCookie('staff_token');
        if(loginCookie == ''){
            window.location.href = '/login';
        }

        $scope.$watch('menus.secondMenu',function(){
            if($rootScope.menus.secondMenu.length > 0 && $location.path() == '/tms/xt'){
                $location.path($rootScope.menus.secondMenu[0].url);
            }
        });
    });
});
