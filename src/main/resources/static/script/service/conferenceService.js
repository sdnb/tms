/**
 * 会议服务
 */
define([],function(){
    function ConferenceService($resource){
        this.addApi = $resource('/api/conference/start'); //发起会议
        this.endApi = $resource('/api/conference/end',null, {update:{method:'PUT'}}); //结束会议
        this.addCallApi = $resource('/api/conference/phones'); //添加呼叫到会议
        this.forbidApi = $resource('/api/conference/call/changeMode',null,{update:{method:'PUT'}});//改变与会者的声音收放模式
        this.exitConfApi = $resource('/api/conference/call/exit',null,{update:{method:'PUT'}});//退出会议
        this.startRecordApi = $resource('/api/conference/record/start',null,{update:{method:'PUT'}});//开始录音
        this.stopReordApi = $resource('/api/conference/record/stop',null,{update:{method:'PUT'}});//停止录音
        this.getPartsApi = $resource('/api/conference/parts'); //得到与会人员列表
        this.getConferencePageApi = $resource('/api/conference/page'); //查询会议列表
    }

    ConferenceService.prototype.add = function(conference,cb){
        this.addApi.save(conference,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceService.prototype.end = function(confId,cb){
        this.endApi.update({confId:confId},null,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceService.prototype.addCall = function(call,cb){
        this.addCallApi.save(call,function(data){
            cb(data);
        },function(errData){
            cb(errData);
        });
    };

    ConferenceService.prototype.forbid = function(confResId,callId,mode,cb){
        this.forbidApi.update({confResId:confResId,callId:callId,mode:mode},null,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceService.prototype.exitConf = function(confResId,callId,cb){
        this.exitConfApi.update({confResId:confResId,callId:callId},null,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceService.prototype.startRecord = function(confResId,cb){
        this.startRecordApi.update({confResId:confResId},null,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceService.prototype.stopReord = function(confResId,cb){
        this.stopReordApi.update({confResId:confResId},null,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceService.prototype.getParts = function(conference,cb){
        this.getPartsApi.get(conference,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceService.prototype.getConferencePage = function(conference,cb){
        this.getConferencePageApi.get(conference,function(data,header){
            cb(data,header);
        },function(errData){
            cb(errData.data.error);
        });
    };

    return ConferenceService;
});