var app = angular.module('loginApp',['ngResource']);

app.controller('loginCtrl', function($scope, $resource){
    console.log('loginCtrl');

    var _this = this;
    this.isNull = false;
    this.loginData = {};

    //用户登录
    this.login = function(flag){
        this.isNull = true;
        if(!flag) return;

    };
});

app.service('loginService', function(){

});