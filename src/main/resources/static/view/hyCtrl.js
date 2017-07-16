define(['../script/tms', 'jquery','../script/service/loginService','../script/service/conferenceService','../script/service/conferenceRoomService','../script/service/contactService','./pagination'],
    function(module, $, LoginService, ConferenceService, ConferenceRoomService, ContactService){
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
        var contactService = new ContactService($resource);


        //获取当前会议主持人及对应的会议室电话簿
        this.conductor = {};
        //获取用户信息 判断角色如果是管理员允许选择会议主持人，如果是普通会议主持人不允许更改会议主持人（待API完善）
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

        //获取联系人
        $scope.contactPageObject = {
            currentPage:1,
            pageSize:10,
            totalPage:7,
            pages:[1,2,3,4,5,6,7],
            length:5
        };

        this.contractFilter = {};
        this.contracts = [];
        this.getContacts = function(type){
            if(type == 'reload'){
                $scope.contactPageObject.currentPage = 1;
            }
            this.contractFilter.currentPage = $scope.contactPageObject.pageSize;
            this.contractFilter.pageSize = $scope.contactPageObject.pageSize;
            this.contractFilter.bookId = 1; //电话簿
            this.loading = true;
            contactService.pageList(this.contractFilter,function(data,header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.contacts = data.message;
                    $scope.contactPageObject.totalPage = header('page_count');
                    $scope.contactPageObject.pages = [];
                    for(var i=1;i<=$scope.contactPageObject.totalPage;i++){
                        $scope.contactPageObject.pages.push(i);
                    }
                }else{
                    _this.contracts = [];
                    console.log(data);
                }
            });
        };

        $scope.watch('contactPageObject.currentPage',function(){
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
    });
});
