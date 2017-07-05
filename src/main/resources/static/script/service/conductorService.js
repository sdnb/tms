define([], function(){
    function ConductorService($resource){
        this.pageListApi = $resource('/api/conductor/page');
        this.saveApi = $resource('/api/conductor');
        this.deleteApi = $resource('/api/conductor/:id');
    }

    ConductorService.prototype.pageList = function(conductor, cb){
        this.pageListApi.get(conductor,function(data,header){
            cb(data, header);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ConductorService.prototype.save = function(conductor, cb){
        this.saveApi.save(conductor, function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    ConductorService.prototype.delete = function(id, cb){
        this.deleteApi.delete({id:id}, null, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    return ConductorService;
});