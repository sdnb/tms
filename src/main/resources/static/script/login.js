var app = angular.module('loginApp',['ngResource']);

app.controller('loginCtrl', function($scope, $resource, loginService){
    var loginCookie = getCookie('staff_token');
    if(loginCookie){
        window.location.href = '/';
    }
    var _this = this;
    this.isNull = false;
    this.message = {
        show: false,
        text: null
    };

    this.hideMessage = function(){
        this.message.show = false;
    };

    $('.hide').removeClass('hide');

    this.loginData = {};

    //用户登录
    this.login = function(flag){
        this.isNull = true;
        if(!flag) return;
        this.login = {
            username: this.loginData.username,
            password: md5(this.loginData.password)
        };
        console.log(this.login);
        loginService.login($resource, this.login, function(data){
            if(data.status == 'true'){
                $scope.loginCookie = getCookie('staff_token');
                window.location.href = '/';
            }else{
                _this.message.show = true;
                _this.message.text = data.message;
                console.log(data);
            }
        });
    };

    function getCookie(name){
        var arr = document.cookie.split("; ");
        for(var i=0,len=arr.length;i<len;i++){
            var item = arr[i].split("=");
            if(item[0]==name){
                return item[1];
            }
        }
        return "";
    }
});

app.service('loginService', function(){
    this.login = function($resource, loginData, cb){
        $resource('/api/admin/login').save(null, loginData,function (data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };
});