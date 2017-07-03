define(['../../script/tms', 'jquery', '../../script/service/conferenceRoomService', '../../view/pagination'], function(module, $, ConferenceRoomService){
    module.controller('hysCtrl', function($scope, $location, $resource, commonService){
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
        var conferenceRoomService = new ConferenceRoomService($resource);

        this.template = 'listTemplate';
        this.room = null;
        this.changeView = function(operation,obj){
            this.isNull = false;
            this.room = angular.copy(obj);
            switch(operation){
                case 'list':
                    this.template = 'listTemplate';
                    break;
                case 'add':
                    this.template = 'addTemplate';
                    break;
                case 'update':
                    this.template = 'updateTemplate';
                    break;
            }
        };

        this.cancel = function(){
            this.template = 'listTemplate';
        };


        $scope.pageObject = {
            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            pages: []
        };

        this.filter = {};
        this.rooms = [];

        this.getAllRooms = function(type){
            if(type == 'reload'){
                $scope.pageObject.currentPage = 1;
            }
            this.filter.currentPage = $scope.pageObject.currentPage;
            this.filter.pageSize = $scope.pageObject.pageSize;

            this.loading = true;
            conferenceRoomService.pageList(this.filter, function(data, header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.rooms = data.message;
                    $scope.pageObject.totalPage = header('Page-Count');
                    $scope.pageObject.pages = [];
                    for(var i=1;i<=$scope.pageObject.totalPage;i++){
                        $scope.pageObject.pages.push(i);
                    }
                    console.log(_this.rooms);
                }else{
                    _this.rooms = [];
                    console.log(data);
                }
            });
        };

        $scope.$watch('pageObject.currentPage', function(){
            _this.getAllRooms();
        });


        this.addRoom = function(flag){
            this.isNull = true;
            if(!flag) return;
        };


        this.updateRoom = function(flag){
            this.isNull = true;
            if(!flag) return;
        };

        this.isDelete = false;
        this.showDelete = function(room){
            this.room = room;
            this.isDelete = true;
        };

        this.hideDelete = function(){
            this.isDelete = false;
        };

        this.deleteRoom = function(){
            this.hideDelete();
            this.loading = true;
        };
    });
});
