define([], function(){
    function PhoneBookService($resource){
        this.findAllApi = $resource('/api/phonebook'); //查询所有电话簿
    }

    PhoneBookService.prototype.findAll = function(phoneBook, cb){
        this.findAllApi.get(phoneBook, function(data){
            cb(data);
        }, function(errData){
            cb(errData.data.error);
        });
    };

    return PhoneBookService;
});