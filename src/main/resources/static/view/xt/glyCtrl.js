define(['../../script/tms', 'jquery', '../../script/service/loginService', '../../script/service/systemService','md5'],
    function(module, $, LoginService, SystemService,MD5){
    module.controller('glyCtrl', function($scope, $location, $resource, commonService){
        var loginCookie = commonService.getCookie('staff_token');
        if(loginCookie == ''){
            window.location.href = '/login';
        }

        var _this = this;
        this.isNull = false;
        this.loading = false;
        this.message = {
            show: false,
            text: null
        };

        this.hideMessage = function(){
            this.message.show = false;
        };

        var loginService = new LoginService($resource);
        var systemService = new SystemService($resource);

        //获取当前登录人
        this.getLoginUser = function(){
            loginService.loginToken(function(data){
                if(data.status == 'true'){
                    $scope.loginUser = data.message;
                }else{
                    console.log(data);
                }
            });
        };
        this.getLoginUser();

        this.pwdPattern = commonService.regex('password');
        this.account = {};

        this.updatePwd = function(flag){
            this.isNull = true;
            if(!flag) return;
            if(this.account.newPassword != this.account.confirmPassword) return;
            this.accountData = {
                username: $scope.loginUser.username,
                password: MD5(this.account.password),
                newPassword: MD5(this.account.newPassword)
            };
            this.loading = true;
            systemService.updatePwd(this.accountData,function(data){
                _this.loading = false;
                _this.message.show = true;
                _this.message.text = data.message;
                if(data.status == 'true'){
                    _this.reset();
                }else{
                    console.log(data);
                }
            });
        };

        this.reset = function(){
            this.isNull = false;
            this.account = {};
        };
    });
});
