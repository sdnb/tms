define([],function(){
    function GroupService($resource){
        this.pageListApi = $resource('/api/group/page'); //分页查询
        this.saveApi = $resource('/api/group'); //新建分组
        this.deleteApi = $resource('/api/group/:id'); //删除分组

        this.addContactApi = $resource('/api/group/contact'); //添加组员
        this.removeContactApi = $resource('/api/group/:gid/contact/:cid'); //移除组员
    }

    GroupService.prototype.pageList = function(group, cb){
        this.pageListApi.get(group, function(data, header){
            cb(data, header);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    GroupService.prototype.save = function(group, cb){
        this.saveApi.save(group,function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    GroupService.prototype.delete = function(id, cb){
        this.deleteApi.delete({id:id}, null, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    GroupService.prototype.addContract = function(groupRelative, cb){
        this.addContactApi.save(groupRelative, function(data){
            cb(data);
        }, function(errData){
            cb(errData);
        });
    };

    GroupService.prototype.removeContract = function(gid, cid , cb){
        this.removeContactApi.delete({gid:gid,cid:cid}, null, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    return GroupService;
});