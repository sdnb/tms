define(['../script/tms', 'jquery','../script/service/loginService','../script/service/conferenceService','../script/service/conferenceRoomService','../script/service/conductorService','../script/service/contactService','./pagination'],
    function(module, $, LoginService, ConferenceService, ConferenceRoomService, ConductorService, ContactService){
    module.controller('hyCtrl', function($rootScope, $scope, $location,$resource,$interval,commonService){
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
                        _this.conductor = {};
                        _this.conductor.id = $scope.loginUser.conductorId;
                        _this.getRooms(_this.conductor.id);
                        _this.getConferences(_this.conductor.id);
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
                    if(_this.conductors.length > 0){
                         _this.conductor = _this.conductors[0];
                         _this.getRooms(_this.conductor.id);
                         _this.getContacts('reload');
                         _this.getConferences(_this.conductor.id);
                    }
                }else{
                    _this.conductors = [];
                    console.log(data);
                }
            });
        };

        this.selectConductor = function(){
            if(this.conductor){
                this.rooms = [];
                this.getRooms(this.conductor.id);
                this.getContacts('reload');
                this.getConferences(this.conductor.id);
            }
            this.checkedContacts = [];
        };

        //获取会议室
        this.rooms =  [];
        this.room = null;
        this.getRooms = function(conductorId){
            if(!conductorId) return;
            conferenceRoomService.getRoomByConductor(conductorId,function(data){
                if(data.status == 'true'){
                    _this.rooms = data.message;
                    if(_this.rooms.length > 0){
                        _this.room = _this.rooms[0];
                    }
                }else{
                    _this.rooms = [];
                    console.log(data);
                }
            });
        };

        //获取联系人
        Array.prototype.commonIndexOf = function(key,element){
            for(var i=0;i<this.length;i++){
                if(this[i][key] == element[key]){
                    return i;
                }
            }
            return -1;
        };

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
            this.contractFilter.conductorId = this.conductor.id; //主持人
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
                        if(_this.checkedContacts.commonIndexOf('id',contact) != -1)
                            contact.isChecked = true;
                    });
                    $scope.checkLimit($scope.contactPageObject.currentPage);
                }else{
                    _this.contracts = [];
                    console.log(data);
                }
            });
        };

        $scope.$watch('contactPageObject.currentPage',function(){
            if(_this.conductor != undefined && _this.conductor.id){
                _this.getContacts();
            }
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
                if(this.checkedContacts.commonIndexOf('id',contact) == -1){
                    this.checkedContacts.push(contact);
                }
            }else{
                if(this.checkedContacts.commonIndexOf('id',contact) != -1){
                    this.checkedContacts.splice(this.checkedContacts.commonIndexOf('id',contact),1);
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
            //this.conference.roomId = this.rooms[0].id;
            this.conference.conductorId = this.conductor.id;
            var phoneArray =  [];
            this.checkedContacts.forEach(function(contact){
                phoneArray.push(contact.phone);
            });
            this.conference.phones = phoneArray;
            this.loading = true;
            conferenceService.add(this.conference,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.conference = data.message;
                    /*var timer = $interval(function(){
                        _this.getMembers('reload');
                    },3000,10);*/
                    _this.getRooms(_this.conductor.id);
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                }
            });
        };

        //分页查会议与会人列表
        $scope.conferencePageObject = {
            currentPage:1,
            totalPage:0,
            pageSize:10,
            pages:[]
        };

        this.members = [];
        this.totalMembers = 0;
        this.conferenceFilter = {};
        this.getMembers = function(type){
            console.log('type='+type);
            if(type == 'reload'){
                $scope.conferencePageObject.currentPage = 1;
            }
            this.conferenceFilter.currentPage = $scope.conferencePageObject.currentPage;
            this.conferenceFilter.pageSize = $scope.conferencePageObject.pageSize;
            this.conferenceFilter.confResId = this.confResId;
            if(!this.confResId) {
                console.log("conference="+_this.conference);
                this.confResId = _this.conference.resId;
            }
            conferenceService.getParts(this.conferenceFilter,function(data,header){
                if(data.status == 'true'){
                    _this.members = data.message;
                    _this.totalMembers = header('page_total');
                    $scope.conferencePageObject.totalPage = header('page_count');
                    $scope.conferencePageObject.pages = [];
                    for(var i=1;i<=$scope.conferencePageObject.totalPage;i++){
                        $scope.conferencePageObject.pages.push(i);
                    }
                }else{
                    _this.members = [];
                    _this.totalMembers = 0;
                    console.log(data);
                }
            });
        };
        _this.confResId = null
        $scope.$watch('conferencePageObject.currentPage',function(){
            if(_this.confResId){
                _this.getMembers();
            }
        });

         //获取会议
        this.getConferences = function(conductorId){
            conferenceService.getConferencePage({currentPage:1,pageSize:6000,conductorId:conductorId,status:1},function(data){
                if(data.status == 'true'){
                    _this.conferences = data.message;
                    if(_this.conferences.length > 0){
                        _this.conference = _this.conferences[0];
                        _this.getMembers('reload');
                    }else{
                        _this.conference = null;
                        _this.members = [];
                    }
                }else{
                    _this.conferences = [];
                    _this.conference = null;
                    _this.members = [];
                    console.log(data);
                }
            });
        };

        //停止会议
        this.endConference = function(){
            if(!this.conference){
                this.message.show = true;
                this.message.text = '当前无会议';
                return;
            }
            this.loading = true;
            conferenceService.end(this.conference.id,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getConferences(_this.conductor.id);
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };

        //开始录音
        this.startRecord = function(){
            if(!this.conference){
                this.message.show = true;
                this.message.text = '当前无会议';
                return;
            }
            this.loading = true;
            conferenceService.startRecord(this.conference.resId,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getConferences(_this.conductor.id);
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                }
            });
        };

        //停止录音
        this.endRecord = function(){
            if(!this.conference){
                this.message.show = true;
                this.message.text = '当前无会议';
                return;
            }
            this.loading = true;
            conferenceService.stopReord(this.conference.resId,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getConferences(_this.conductor.id);
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                }
            });
        };

        this.phone = null;
        var phonePattern = commonService.regex('telephone');
        //添加临时电话到会议
        this.addTempCall = function(phone){
            if(!phonePattern.test(phone)){
                this.message.show = true;
                this.message.text = '请填写正确的电话号码';
                return;
            }

            if(!this.conference){
                this.message.show = true;
                this.message.text = '当前无会议';
                return;
            }

            this.addCallShow = {
                confResId: this.conference.resId,
                phone:[phone]
            };

            this.loading = true;
            conferenceService.addTempPhone(this.addCallShow,function(data){
                if(data.status == 'true'){
                    //webSocket自动更新与会人列表
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };


        //添加到会议
        this.addCall = function(phone){
            if(!phonePattern.test(phone)){
                this.message.show = true;
                this.message.text = '请填写正确的电话号码';
                return;
            }

            if(!this.conference){
                this.message.show = true;
                this.message.text = '当前无会议';
                return;
            }

            this.addCallShow = {
                confResId: this.conference.resId,
                phones:[phone]
            };
            this.loading = true;
            conferenceService.addCall(this.addCallShow,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    /*var task = $interval(function(){
                        _this.getMembers('reload');
                    },3000,5);*/
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                }
            });
        };

        //从会议移除
        this.removeCall = function(member){
            this.loading = true;
            conferenceService.exitConf(this.conference.resId,member.resId,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    /*var task = $interval(function(){
                        _this.getMembers('reload');
                    },3000,5);*/
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                }
            });
        };

        //改变与会者的声音收放模式
        this.changeVoiceMode = function(member){
            var mode = member.voiceMode;
            if(mode == 1 || mode == 3){
                mode = 2;
            }else{
                mode = 1;
            }
            this.loading = true;
            conferenceService.forbid(this.conference.resId,member.resId,mode,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getMembers();
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };


        //定义确认弹出框提示问题
        var confirmInfo = {"end":{tips:"是否对此次会议进行录音?"}};

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

        $rootScope.socket = null;
        function initWebSocket(){
            //判断当前浏览器是否支持WebSocket
            if('WebSocket' in window){
                $rootScope.socket = new WebSocket("ws://202.165.191.9:8080/reminder");
            }
            else{
                alert('您的浏览器不支持WebSocket');
            }

            //连接发生错误的回调方法
            $rootScope.socket.onerror = function(){
                console.log("connect error");
            };

            //连接成功建立的回调方法
            $rootScope.socket.onopen = function(event){
                console.log("connect open");
            };

            //接收到消息的回调方法
            $rootScope.socket.onmessage = function(){
                _this.confResId = event.data;
                if(_this.conference != undefined && _this.conference.resId == _this.confResId){
                    _this.getMembers('reload');
                }
            };

            //连接关闭的回调方法
            $rootScope.socket.onclose = function(){
                console.log("connect close");
            };
        }

        //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
        window.onbeforeunload = function(){
            if($rootScope.socket){
                closeWebSocket();
            }
        };


        //关闭连接
        function closeWebSocket(){
            $rootScope.socket.close();
        }

        //发送消息
        function send(){
            var message = "Hello WebSocket";
            socket.send(message);
        }

        initWebSocket();
    });
});
