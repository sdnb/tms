define(['../script/tms', 'jquery', '../script/service/recordingService', './pagination'], function(module, $, RecordingService){
    module.controller('lyCtrl', function($scope, $resource, $location, commonService){
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

        var recordingService = new RecordingService($resource);

        //时间控件样式冲突暂时屏蔽
        this.initDatePicker = function(){
            commonService.changeDatePicker('datetime',['createStart','createEnd']);
        };

        $scope.pageObject= {
            currentPage: 1,
            pageSize: 10,
            totalPage: 0,
            pages: []
        };

        this.recording = null;
        this.recordings = [];
        this.filter = {};

        this.getRecordings = function(type){
            if(type == 'reload'){
                $scope.pageObject.currentPage = 1;
            }

            this.filter.currentPage = $scope.pageObject.currentPage;
            this.filter.pageSize = $scope.pageObject.pageSize;

            for(var k in this.filter){
                if(!this.filter[k]) this.filter[k] = null;
            }

            this.loading = true;
            recordingService.pageList(this.filter,function(data,header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.recordings = data.message;
                    $scope.pageObject.totalPage = header('page_count');
                    $scope.pageObject.pages = [];
                    for(var i=1;i<=$scope.pageObject.totalPage;i++){
                        $scope.pageObject.pages.push(i);
                    }
                    console.log(_this.recordings);
                }else{
                    _this.recordings = []
                    console.log(data);
                }
            });
        };

        $scope.$watch('pageObject.currentPage',function(){
            _this.getRecordings();
        });

        //定义确认弹出框提示问题
        var confirmInfo = {"delete":{tips:"请问是否删除该录音记录?"}};

        //操作确认
        $scope.confirmDialogShow = false;
        $scope.confirmOper = function(type, obj){
            _this.recording = obj;
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
                _this.deleteRecording();
            }
        };

        this.deleteRecording = function(){
            this.loading = true;
            recordingService.delete(this.recording.id, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getRecordings();
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };

        this.download = function(record){
            window.location.href = '/api//recording/download?filename=' + record.filename +
                    '&filepath=' + record.filepath;
        };
    });
});
