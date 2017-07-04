define(['../script/tms', 'jquery'], function(module, $){
    module.controller('dhbCtrl', function($scope, $location){
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
        this.template = 'listTemplate';
        this.phoneBook = null;
        this.changeView = function(operation,obj){
            this.isNull = false;
            this.phoneBook = angular.copy(obj);
            switch(operation){
                case 'list':
                    this.template = 'listTemplate';
                    break;
                case 'add':
                    this.room = {};
                    this.room.isRecordEnable = 0;
                    this.template = 'addTemplate';
                    break;
                case 'update':
                    this.conductor = this.getConductor(this.room.conductorId, this.conductors);
                    this.template = 'updateTemplate';
                    break;
            }
        };

    });
});
