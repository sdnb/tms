define([],function(){
    function SystemService($resource){
        this.logFindPageApi = $resource('/api/log/page');
    }

    SystemService.prototype.logFindPage = function(filter,cb){
        this.logFindPageApi.get(filter,function(data,header){
            cb(data);
        });
    };

    return SystemService;
});