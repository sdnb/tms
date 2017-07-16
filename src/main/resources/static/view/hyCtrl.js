define(['../script/tms', 'jquery','../script/service/loginService','../script/service/conferenceService','../script/service/conferenceRoomService','../script/service/conductorService','../script/service/contactService','./pagination'],
    function(module, $, LoginService, ConferenceService, ConferenceRoomService, ConductorService, ContactService){
    module.controller('hyCtrl', function($scope, $location,$resource,commonService){
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
        var conferenceService = new ConferenceService($resource);
        var conferenceRoomService = new ConferenceRoomService($resource);
        var conductorService = new ConductorService($resource);
        var contactService = new ContactService($resource);


        //获取当前会议主持人及对应的会议室电话簿
        this.conductor = null; //选中主持人
        this.conductors = []; //主持人列表
        //获取用户信息 判断角色如果是管理员允许选择会议主持人，如果是普通会议主持人不允许更改会议主持人（待API完善）
        this.getLoginUser = function(){
            loginService.loginToken(function(data){
                if(data.status == 'true'){
                    $scope.loginUser = data.message;
                    if($scope.loginUser.username == 'admin'){//系统管理员可更换主持人
                        //获取主持人
                        _this.getConductors();
                    }else{ //非主持人直接获取联系人
                        this.conductor = {};
                        this.conductor.id = $scope.loginUser.conductorId;
                        _this.getRooms(this.conductor.id);
                        _this.getContacts('reload');
                    }
                }else{
                    console.log(data);
                }
            });
        };

        this.getLoginUser();

        //获取主持人
        this.getConductors = function(){
            conductorService.pageList({currentPage:1,pageSize:6000},function(data,header){
                if(data.status == 'true'){
                    _this.conductors = data.message;
                    _this.conductor = _this.conductors[0];
                    _this.getRooms(_this.conductor.id);
                    _this.getContacts('reload');
                }else{
                    _this.conductors = [];
                    console.log(data);
                }
            });
        };

        this.selectConductor = function(conductor){
            this.rooms = [];
            this.getRooms(this.conductor.id);
            this.getContacts('reload');
        };

        //获取会议室
        this.rooms =  [];
        this.getRooms = function(conductorId){
            if(!conductorId) return;
            conferenceRoomService.getRoomByConductor(conductorId,function(data){
                if(data.status == 'true'){
                    _this.rooms = data.message;
                }else{
                    _this.rooms = [];
                    console.log(data);
                }
            });
        };

        //获取联系人
        $scope.contactPageObject = {
            currentPage:1,
            pageSize:10,
            totalPage:0,
            pages:[],
            length:5
        };

        this.contractFilter = {};
        this.contacts = [];
        this.checkedContacts = [];
        this.totalContacts = 0;
        this.getContacts = function(type){
            if(type == 'reload'){
                $scope.contactPageObject.currentPage = 1;
            }
            this.contractFilter.currentPage = $scope.contactPageObject.currentPage;
            this.contractFilter.pageSize = $scope.contactPageObject.pageSize;
            this.contractFilter.conductorId = this.conductor.id; //电话簿
            this.loading = true;
            contactService.getContacts(this.contractFilter,function(data,header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.contacts = data.message;
                    console.log(_this.contacts);
                    _this.totalContacts = header('page_total');
                    $scope.contactPageObject.totalPage = header('page_count');
                    $scope.contactPageObject.pages = [];
                    for(var i=1;i<=$scope.contactPageObject.totalPage;i++){
                        $scope.contactPageObject.pages.push(i);
                    }
                    angular.forEach(_this.contacts,function(contact){
                        contact.isChecked = false;
                        if(_this.checkedContacts.indexOf(contact) != -1)
                            contact.isChecked = true;
                    });
                }else{
                    _this.contracts = [];
                    console.log(data);
                }
            });
        };

        $scope.$watch('contactPageObject.currentPage',function(){
            if(_this.conductor){
                _this.getContacts();
            }
            $scope.checkLimit();
        });

        $scope.selectPage = function(page){
            $scope.contactPageObject.currentPage = page;
        };

        $scope.changePage = function(operation){
            if(operation == 'next'){
                $scope.contactPageObject.currentPage = ($scope.contactPageObject.currentPage+1) > $scope.contactPageObject.totalPage ? $scope.contactPageObject.currentPage : ($scope.contactPageObject.currentPage+1);
            } else if(operation == 'prev'){
                $scope.contactPageObject.currentPage = ($scope.contactPageObject.currentPage-1) < 1 ? $scope.contactPageObject.currentPage : ($scope.contactPageObject.currentPage-1);
            }

        };

        $scope.upLimit = 0;
        $scope.downLimit = $scope.contactPageObject.length + 1;
        $scope.checkLimit = function(page){
            var _page = page -1;
            $scope.upLimit = parseInt(_page / $scope.contactPageObject.length) * $scope.contactPageObject.length;
            $scope.downLimit = (parseInt(_page / $scope.contactPageObject.length) + 1) * $scope.contactPageObject.length + 1;
        };

        this.selectContact = function(contact){
            if(contact.isChecked){
                if(this.checkedContacts.indexOf(contact) == -1){
                    this.checkedContacts.push(contact);
                }
            }else{
                if(this.checkedContacts.indexOf(contact) != -1){
                    this.checkedContacts.splice(this.checkedContacts.indexOf(contact),1);
                }
            }
            console.log(this.checkedContacts);
        };

        //发起会议
        this.showRecord = false;
        this.confirmAdd = function(){
            if(this.checkedContacts.length == 0){
                this.message.show = true;
                this.message.text = '请选择联系人后再添加会议';
                return;
            }
            this.showRecord = true;
            this.conference = {};
        };

        this.selectRecord = function(operation){
            if(operation == 'no'){
                this.conference.isRecordEnable = false;
            }else{
                this.conference.isRecordEnable = true;
            }
            this.showRecord = false;
            this.addConference();
        };

        this.addConference = function(){
            this.conference.roomId = this.rooms[0].id;
            this.conference.conductorId = this.conductor.id;
            var phoneArray =  [];
            this.checkedContacts.forEach(function(contact){
                phoneArray.push(contact.phone);
            });
            this.conference.phones = phoneArray;
            console.log(this.conference);
            this.loading = true;
            conferenceService.add(this.conference,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                }else{
                }
            });
        };


        //定义确认弹出框提示问题
        var confirmInfo = {"record":{tips:"是否对此次会议进行录音?"}};

        //操作确认
        $scope.confirmDialogShow = false;
        $scope.confirmOper = function(type, obj){
            _this.recording = obj;
            $scope.confirmTips = "";
            $scope.confirmTips = confirmInfo[type].tips;
            $scope.confirmType = type;
        };

        $scope.cancelConfirm = function(){
            $scope.confirmDialogShow = false;
        };

        //操作提交
        $scope.confirmCommit = function(type){
            $scope.cancelConfirm();
            if(type == "delete"){
                _this.deleteRecording();
            }
        };
    });
});
