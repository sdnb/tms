define(['../../script/tms', 'jquery', '../../script/service/loginService', '../../script/service/systemService'],
    function(module, $, LoginService, SystemService){
    module.controller('ljCtrl', function($scope, $location, $resource, commonService){
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

        this.setting = null;
        this.getCurrentSetting = function(){
            systemService.getSetting(function(data){
                if(data.status == 'true'){
                    _this.setting = data.message;
                }else{
                    _this.setting = null;
                    console.log(data);
                }
            });
        };
        this.getCurrentSetting();

        this.portPattern = commonService.regex('positiveInteger');

        this.saveSetting = function(flag){
            this.isNull = true;
            if(!flag) return;
            this.loading = true;
            systemService.addSetting(this.setting,function(data){
                _this.loading = false;
                _this.message.show = true;
                _this.message.text = data.message;
                if(data.status == 'true'){
                    _this.getCurrentSetting();
                }else{
                    console.log(data);
                }
            });
        };

        this.updateCurrentSetting = function(flag){
            this.isNull = true;
            if(!flag) return;
            this.loading = true;
            systemService.updateSetting(this.setting.id,this.setting,function(data){
                _this.loading = false;
                _this.message.show = true;
                _this.message.text = data.message;
                if(data.status == 'true'){
                    _this.getCurrentSetting();
                }else{
                    console.log(data);
                }
            });
        };

        this.reset = function(){
            this.isNull = false;
            this.getCurrentSetting();
        };
    });
});
