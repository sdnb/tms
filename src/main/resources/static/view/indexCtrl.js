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
                    _this.getRoleMenu($rootScope.loginUser);
                }else{
                    console.log(data);
                }
            });
        };
        //获取角色菜单
        /**
         *菜单应有lvl,parentId属性，无lvl暂用parentId代替菜单层级
         */
        $rootScope.menus = {
            allMenu: [],
            firstMenu: [],
            secondMenu: []
        };
        this.getRoleMenu = function(currentUser){
            this.filter = {
                role: currentUser.role
            };
            loginService.roleMenu(this.filter,function(data){
                if(data.status == 'true'){
                    var menus  = data.message;
                    $rootScope.menus.allMenu = menus;
                    menus.forEach(function(menu){
                        if(menu.parentId == 1){
                            $rootScope.menus.firstMenu.push(menu);
                        }else if(menu.parentId == 2){
                            $rootScope.menus.secondMenu.push(menu);
                        }
                    });
                    _this.activeMenuByRoute($rootScope.menus.firstMenu);
                    _this.checkAuthority($rootScope.menus.allMenu);
                }else{
                    $rootScope.menus = [];
                    console.log(data);
                    $location.path('/tms/notFound');
                }
            });
        };

        this.checkAuthority = function(menus){
            var path = $location.path();
            if(menus.length > 0){
                var hasAuthority = false;
                for(var i=0;i<menus.length;i++){
                    if(menus[i].url == path){
                        hasAuthority = true;
                        break;
                    }
                }
                if(!hasAuthority && path != '/tms/notFound'){
                    $location.path('/tms/noAuthority');
                }
            }else{
                $location.path('/tms/notFound');
            }
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

        $rootScope.activeMenuId = 0;
        this.activeMenu = function(id){
            $rootScope.activeMenuId = id;
        };

        this.activeMenuByRoute = function(menus){
            var url = $location.url();
            if(url == '/' || url == '/tms') url = '/tms/hy';
            for(var i=0;i<menus.length;i++){
                if(url.indexOf(menus[i].url) != -1){
                    $rootScope.activeMenuId = menus[i].id;
                    break;
                }
            }
        };

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