define([],function(){
    function SystemService($resource){
        this.logFindPageApi = $resource('/api/log/page'); //分页查询日志
        this.updatePwdApi = $resource('/api/setting/pwd',null,{update:{method:'PUT'}});
    }

    SystemService.prototype.logFindPage = function(filter,cb){
        this.logFindPageApi.get(filter,function(data,header){
            cb(data,header);
        });
    };

    SystemService.prototype.updatePwd = function(account,cb){
        this.updatePwdApi.update(account,function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    return SystemService;
});