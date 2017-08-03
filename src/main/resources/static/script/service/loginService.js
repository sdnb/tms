define([], function(){
    function LoginService($resource){
        this.loginApi = $resource('/api/admin/login'); //账户密码登录
        this.loginTokenApi = $resource('/api/admin/login/token'); //令牌登录
        this.logoutApi = $resource('/api/admin/logout/token'); //登出
        this.roleMenuApi = $resource('/api/menu/:role'); //角色菜单
    }

    LoginService.prototype.login = function(loginData, cb){
        this.loginApi.save(null, loginData, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    LoginService.prototype.loginToken = function(cb){
        this.loginTokenApi.save(function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    LoginService.prototype.logout = function(cb){
        this.logoutApi.save(function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    LoginService.prototype.roleMenu = function(filter,cb){
        this.roleMenuApi.get(filter,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    return LoginService;
});