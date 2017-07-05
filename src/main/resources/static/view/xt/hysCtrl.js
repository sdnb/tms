define(['../../script/tms', 'jquery', '../../script/service/conferenceRoomService', '../../script/service/conductorService', '../../view/pagination'],
    function(module, $, ConferenceRoomService, ConductorService){
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
        var conductorService = new ConductorService($resource);

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
                    this.room = {};
                    this.conductor = null;
                    this.room.isRecordEnable = 0;
                    this.template = 'addTemplate';
                    break;
                case 'update':
                    this.conductor = this.getConductor(this.room.conductorId, this.conductors);
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

        this.ivrPattern = commonService.regex('ivr'); //ivr
        this.numberPattern = commonService.regex('mustPositiveNumber'); //正整数

        this.conductor = null;
        this.conductors = [];
        this.getConductors = function(){
            conductorService.pageList({currentPage:1,pageSize:1000},function(data){
                if(data.status == 'true'){
                    _this.conductors = data.message;
                }else{
                    _this.conductros = [];
                }
            });
        };
        this.getConductors();

        this.getConductor = function(id, conductorArray){
            for(var i=0;i<conductorArray.length;i++){
                if(conductorArray[i].id == id){
                    return conductorArray[i];
                }
            }
        };


        this.addRoom = function(flag){
            this.isNull = true;
            if(!flag) return;
            this.room.conductorId = this.conductor.id;
            this.room.conductorName = this.conductor.realname;
            this.loading = true;
            conferenceRoomService.save(this.room, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.cancel();
                    _this.getAllRooms('reload');
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };


        this.updateRoom = function(flag){
            this.isNull = true;
            if(!flag) return;
            this.room.conductorId = this.conductor.id;
            this.room.conductorName = this.conductor.realname;
            this.loading = true;
            conferenceRoomService.update(this.room.id, this.room, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.cancel();
                    _this.getAllRooms();
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                }
            });
        };


        //定义确认弹出框提示问题
        var confirmInfo = {"delete":{tips:"请问是否删除该会议室?"}};

        //操作确认
        $scope.confirmDialogShow = false;
        $scope.confirmOper = function(type, obj){
            _this.room = obj;
            $scope.confirmTips = "";
            $scope.confirmTips = confirmInfo[type].tips;
            $scope.confirmType = type;
            $scope.confirmDialogShow = true;
        };

        $scope.cancelConfirm = function(){
            $scope.confirmDialogShow = false;
        };

        //操作提交
        $scope.confirmCommit = function(type){
            $scope.cancelConfirm();
            if(type == "delete"){
                _this.deleteRoom();
            }
        };


        this.deleteRoom = function(){
            this.loading = true;
            conferenceRoomService.delete(this.room.id, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getAllRooms();
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };
    });
});
