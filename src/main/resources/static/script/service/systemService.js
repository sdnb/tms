define([],function(){
    function SystemService($resource){
        this.logFindPageApi = $resource('/api/log/page'); //分页查询日志
        this.updatePwdApi = $resource('/api/setting/pwd',null,{update:{method:'PUT'}});
        this.getSettingApi = $resource('/api/setting'); //获取系统设置
        this.addSettingApi = $resource('/api/setting'); //新增系统设置
        this.updateSettingApi = $resource('/api/setting/:id',null,{update:{method:'PUT'}});
    }

    SystemService.prototype.logFindPage = function(filter,cb){
        this.logFindPageApi.get(filter,function(data,header){
            cb(data,header);
        },function(errData){
            cb(errData.data.error);
        });
    };

    SystemService.prototype.updatePwd = function(account,cb){
        this.updatePwdApi.update(account,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    SystemService.prototype.getSetting = function(cb){
        this.getSettingApi.get(function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    SystemService.prototype.addSetting = function(setting,cb){
        this.addSettingApi.save(setting,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    SystemService.prototype.updateSetting = function(id,setting,cb){
        this.updateSettingApi.update({id:id},setting,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    return SystemService;
});