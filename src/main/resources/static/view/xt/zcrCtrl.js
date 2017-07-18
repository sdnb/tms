define(['../../script/tms', 'jquery', '../../script/service/conductorService','md5', '../pagination'], function(module, $, ConductorService,MD5){
    module.controller('zcrCtrl', function($scope, $resource, $location, commonService){
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

        var conductorService = new ConductorService($resource);

        this.template = 'listTemplate';
        this.conductor = null;
        this.changeView = function(operation,obj){
            this.isNull = false;
            this.conductor = angular.copy(obj);
            switch(operation){
                case 'list':
                    this.template = 'listTemplate';
                    break;
                case 'add':
                    this.conductor = {};
                    this.conductor.accountShow = {};
                    this.password = null;
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
        this.conductors = [];
        this.getConductors = function(type){
            if(type == 'reload'){
                $scope.pageObject.currentPage = 1;
            }

            this.filter.currentPage = $scope.pageObject.currentPage;
            this.filter.pageSize = $scope.pageObject.pageSize;

            for(var k in this.filter){
                if(!this.filter[k]) this.filter[k] = null;
            }

            this.loading = true;
            conductorService.pageList(this.filter, function(data, header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.conductors = data.message;
                    $scope.pageObject.totalPage = header('page_count');
                    $scope.pageObject.pages = [];
                    for(var i=1;i<=$scope.pageObject.totalPage;i++){
                        $scope.pageObject.pages.push(i);
                    }
                }else{
                    _this.conductors = [];
                    console.log(data);
                }
            });
        };

        $scope.$watch('pageObject.currentPage', function(){
            _this.getConductors();
        });


        //添加会议主持人
        this.realnamePattern = commonService.regex('chinese'); //中文姓名
        this.usernamePattern = commonService.regex('account'); //账户
        this.passwordPattern = commonService.regex('password'); //密码
        this.phonePattern = commonService.regex('telephone'); //电话
        this.saveConductor = function(flag){
            this.isNull = true;
            if(!flag) return;
            this.conductor.accountShow.username = this.conductor.username;
            this.conductor.accountShow.password = MD5(this.password);
            console.log(this.conductor);
            this.loading = true;
            conductorService.save(this.conductor, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.cancel();
                    _this.getConductors('reload');
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };

        //定义确认弹出框提示问题
        var confirmInfo = {"delete":{tips:"请问是否删除该主持人?"}};

        //操作确认
        $scope.confirmDialogShow = false;
        $scope.confirmOper = function(type, obj){
            _this.conductor = obj;
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
                _this.deleteConductor();
            }
        };

        this.deleteConductor = function(){
            this.loading = true;
            conductorService.delete(this.conductor.id, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getConductors();
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };

    });
});
