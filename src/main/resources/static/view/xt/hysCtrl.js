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

            for(var k in this.filter){
                if(!this.filter[k]) this.filter[k] = null;
            }

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


        //定义确认弹出框提示问题
        var confirmInfo = {"delete":{tips:"请问是否删除该会议室?"}};

        //操作确认
        $scope.confirmDialogShow = false;
        $scope.confirmOper = function(type, obj){
            this.room = obj;
            $scope.confirmTips = "";
            $scope.confirmTips = confirmInfo[type].tips;
            $scope.confirmType = type;
            $scope.confirmDialogShow = false;
        };

        $scope.cancelConfirm = function(){
            $scope.confirmDialogShow = false;
        };

        //操作提交
        $scope.confirmCommit = function(type){
            $scope.cancelConfirm();
            if(type == "delete"){
                this.deleteRoom();
            }
        };


        this.deleteRoom = function(){
            this.loading = true;
        };
    });
});
