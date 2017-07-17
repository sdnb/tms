define([],function(){
    function SystemService($resource){
        this.logFindPageApi = $resource('/api/log/page'); //分页查询日志
    }

    SystemService.prototype.logFindPage = function(filter,cb){
        this.logFindPageApi.get(filter,function(data,header){
            cb(data,header);
        });
    };

    return SystemService;
});