define([],function(){
    function RecordingService($resource){
        this.pageListApi = $resource('/api/recording/page');
        this.deleteApi = $resource('/api/recording/:rid');
    }

    RecordingService.prototype.pageList = function(filter, cb){
        this.pageListApi.get(filter, function(data,header){
            cb(data,header);
        },function(errData){
            cb(errData.data.error);
        });
    };

    RecordingService.prototype.delete = function(rid, cb){
        this.deleteApi.delete({rid:rid}, null, function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    return RecordingService;
});