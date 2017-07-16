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
					console.log(_this.conductors);
                    _this.conductor = _this.conductors[0];
                    _this.getContacts('reload');
                }else{
                    _this.conductors = [];
                    console.log(data);
                }
            });
        };

        this.selectConductor = function(conductor){
            this.getContacts('reload');
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
            this.contractFilter.uid = this.conductor.id; //电话簿
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
    });
});
