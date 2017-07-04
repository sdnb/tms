define([], function(){
    function ContactService($resource){
        this.pageListApi = $resource('/api/contact/page'); //分页查询
        this.saveApi = $resource('/api/contact'); //新增联系人
        this.updateApi = $resource('/api/contact/:id', null, {update:{method:'PUT'}}); //修改联系人
        this.deleteApi = $resource('/api/contact/:id'); //删除联系人
    }

    ContactService.prototype.pageList = function(contact, cb){
        this.pageListApi.get(contact, function(data, header){
            cb(data, header);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ContactService.prototype.save = function(contact, cb){
        this.saveApi.save(contact, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ContactService.prototype.update = function(id, contact, cb){
        this.updateApi.update({id:id}, contact, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    ContactService.prototype.delete = function(id, cb){
        this.deleteApi.delete({id:id}, null, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    return ContactService;
});