define(['../../script/tms', 'jquery', '../../script/service/systemService', '../pagination'], function(module, $, SystemService){
    module.controller('rzCtrl', function($scope, $location, $resource, commonService, paginationService){
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

        var systemService = new SystemService($resource);

        this.logFilter = {};
        $scope.pageObject = {
            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            pages: []
        };

        this.types = [{id:0,name:'会议'},{id:1,name:'呼叫'}];

        this.logs = [];
        this.getLogs = function(type){
            if(type == 'reload'){
                $scope.pageObject.currentPage = 1;
            }

            this.logFilter.currentPage = $scope.pageObject.currentPage;
            this.logFilter.pageSize = $scope.pageObject.pageSize;

            this.loading = true;
            systemService.logFindPage(this.logFilter,function(data,header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.logs = data.message;
                    $scope.pageObject.totalPage = header('page_count');
                    $scope.pageObject.pages = [];
                    for(var i=1;i<=$scope.pageObject.totalPage;i++){
                        $scope.pageObject.pages.push(i);
                    }
                }else{
                    _this.logs = [];
                    console.log(data);
                }
            });
        };

        $scope.$watch('pageObject.currentPage',function(){
            _this.getLogs();
        });

        $scope.$watch('pageObject.totalPage',function(){
            paginationService.showFirstPageContent($scope.pageObject,1);
        });
    });
});
