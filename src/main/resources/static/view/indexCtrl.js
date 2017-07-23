define(['../script/tms', 'jquery', '../script/service/loginService'], function(module, $, LoginService){
    module.controller('indexCtrl', function($rootScope, $scope, $resource, $location, $route, commonService){
        var _this = this;

        var loginCookie = commonService.getCookie('staff_token');
        var loginService = new LoginService($resource);
        //获取用户信息
        this.getLoginUser = function(){
            loginService.loginToken(function(data){
                if(data.status == 'true'){
                    $rootScope.loginUser = data.message;
                }else{
                    console.log(data);
                }
            });
        };
        if(loginCookie == ''){
            window.location.href = '/login';
        }else{
            $rootScope.loginCookie = loginCookie;
            this.getLoginUser();
        }

        $rootScope.$on('$locationChangeSuccess', function (e) {
            $rootScope.path = $location.path();
            if($rootScope.socket){
                $rootScope.socket.close();
            }
        });

        //监控浏览器地址栏变化，如果变化刷新页面。解决点击浏览器的回退，前进按钮页面不刷新的问题
        $rootScope.$watch(function () {return $location.path()}, function (newLocation, oldLocation) {
            if($rootScope.path === newLocation) {
                //alert('Why did you use history back?');
                window.location.href = newLocation
            }
        });

        //刷新页面
        this.refresh = function(){
            $route.reload();
        };

        $rootScope.activeIndex = 0;
        this.activeMenu = function(index){
            $rootScope.activeIndex = index;
        };

        var url = $location.url();
        this.activeMenuByRoute = function(){
            $("#menu > a").each(function(){
               if(url.indexOf($(this).attr('href')) > -1){
                   $rootScope.activeIndex = $(this).index();
               }
            });
        }

        this.activeMenuByRoute();

        //登出
        this.logout = function(){
            loginService.logout(function(data){
                commonService.deleteCookie('staff_token');
                if(data.status == 'false'){
                }
                window.location.href = '/login';
            });
        };

    });
});