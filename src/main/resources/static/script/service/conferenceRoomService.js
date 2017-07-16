define([], function(){
    function ConferenceRoomService($resource){
        this.pageListApi = $resource('/api/conferenceRoom/page');
        this.saveApi = $resource('/api/conferenceRoom');
        this.deleteApi = $resource('/api/conferenceRoom/:id');
        this.updateApi = $resource('/api/conferenceRoom/:id', null, {update:{method:'PUT'}});
        this.getRoomByConductorApi = $resource('/api/conferenceRoom/conductor');
    }

    ConferenceRoomService.prototype.pageList = function(filter, cb){
        this.pageListApi.get(filter, function(data,header){
            cb(data,header);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceRoomService.prototype.save = function(obj, cb){
        this.saveApi.save(obj, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceRoomService.prototype.update = function(id, obj, cb){
        this.updateApi.update({id:id}, obj, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceRoomService.prototype.delete = function(id, cb){
        this.deleteApi.delete({id:id}, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ConferenceRoomService.prototype.getRoomByConductor = function(conductorId,cb){
        this.getRoomByConductorApi.get({conductorId:conductorId},function(data){
            cb(data);
        },function(errData){
            cb(errData.data.error);
        });
    };

    return ConferenceRoomService;
});