define(['../script/tms', 'jquery', '../script/service/groupService', '../script/service/contactService',
    '../script/service/phoneBookService', './pagination', 'ajaxfileupload'], function(module, $, GroupService, ContactService, PhoneBookService){
    module.controller('dhbCtrl', function($rootScope, $scope, $location, $resource, commonService, paginationService){
        var loginCookie = commonService.getCookie('staff_token');
        if(loginCookie == ''){
            window.location.href = '/login';
        }

        console.log($rootScope.loginUser);

        var _this = this;
        this.isNull = false;
        this.loading = false;
        this.message = {
            show: false,
            text: null
        };

        this.hideMessage = function(){
            this.message.show = false;
        };

        var groupService = new GroupService($resource);
        var contactService = new ContactService($resource);
        var phoneBookService = new PhoneBookService($resource);

        this.type = 1; //1系统电话簿 2会议室电话簿
        this.type = $rootScope.loginUser.role;
        this.operationPanel = { //操作面板
            show: false,
            index: 0
        };
        this.changeType = function(type){
            this.type = type;
            this.operationPanel.show = false;
            this.operationPanel.index = 0;
        };

        this.showPanel = function(index){
            this.operationPanel.show = true;
            this.operationPanel.index = index;
            this.group = null;
            if(index == 2){
                $scope.groupPageObject.currentPage = 1;
                this.getGroups();
            }
        };

        this.template = 'listTemplate';
        this.contact = null;
        this.group = null;
        this.phoneBook = null; //系统电话簿
        this.changeView = function(operation,obj,type){
            this.isNull = false;
            if(type == 'contact'){
                this.contact = angular.copy(obj);
            }else if(type == 'group'){
                this.group = angular.copy(obj);
            }
            switch(operation){
                case 'list': //联系人列表
                    this.template = 'listTemplate';
                    break;
                case 'add': //联系人新增
                    this.contact = {};
                    this.group = null;
					this.totalGroups = [];
					this.getTotalGroups();
                    this.template = 'addTemplate';
                    break;
                case 'update': //联系人更新
					this.getTotalGroups(function(totalGroups){
						_this.group = _this.getGroup(_this.contact.groupId,_this.totalGroups);
					});
                    this.template = 'updateTemplate';
                    break;
                case 'addGroup':
                    this.group = {};
                    this.getPhoneBooks(this.type);
                    this.template = 'addGroupTemplate';
                    break;
                case 'import':
                    this.template = 'importTemplate';
                    break;
            }
        };

        this.cancel = function(){
            this.template = 'listTemplate';
        };

        //-----------------------------分组--------------------------------
        $scope.groupPageObject = {
            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
            pages:[]
        };

        this.groupFilter = {};
        this.groups = [];
        this.getGroups = function(type){
            if(type == 'reload') $scope.groupPageObject.currentPage = 1;
            this.groupFilter.currentPage = $scope.groupPageObject.currentPage;
            this.groupFilter.pageSize = $scope.groupPageObject.pageSize;
            this.groupFilter.conductorId = $rootScope.loginUser.conductorId;
            this.loading = true;
            groupService.pageList(this.groupFilter,function(data,header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.groups = data.message;
                    $scope.groupPageObject.totalPage = header('page_count');
                    $scope.groupPageObject.pages = [];
                    for(var i=1;i<=$scope.groupPageObject.totalPage;i++){
                        $scope.groupPageObject.pages.push(i);
                    }
                }else{
                    _this.groups = [];
                    console.log(data);
                }
            });
        };

        $scope.prev = function(){
            $scope.groupPageObject.currentPage = $scope.groupPageObject.currentPage == 1 ?
                $scope.groupPageObject.currentPage : $scope.groupPageObject.currentPage - 1;
        };

        $scope.next = function(){
            $scope.groupPageObject.currentPage = $scope.groupPageObject.currentPage == $scope.groupPageObject.totalPage ?
                $scope.groupPageObject.currentPage : $scope.groupPageObject.currentPage + 1;
        };

        $scope.$watch('groupPageObject.currentPage',function(){
            _this.getGroups();
        });


        this.addGroup = function(flag){
            this.isNull = true;
            if(!flag) return;
            if(this.type == 1) this.group.bookId = this.phoneBook.id;
            this.loading = true;
            groupService.save(this.group, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.cancel();
                    _this.showPanel(2);
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };


        this.selectGroup = function(group){
            this.group = this.group == group ? null : group;
            this.getContacts('reload');
        };

        this.totalGroups = [];
        this.totalGroupsA = [];
        this.totalGroupsB = [];
        this.getTotalGroups = function(cb){
			this.totalGroups = [];
			this.totalGroupsA = [];
			this.totalGroupsB = [];
            groupService.totalList(function(data){
                if(data.status == 'true'){
                    _this.totalGroups = data.message;
                    angular.forEach(_this.totalGroups,function(group){
                        if(group.bookId==_this.phoneBook.id){
                            _this.totalGroupsA.push(group);
                        }
                        if(group.bookId!=_this.phoneBook.id){
                            _this.totalGroupsB.push(group);
                        }
                    });
					if(typeof cb == 'function'){
						cb(_this.totalGropus);
					}
                }else{
                    _this.totalGropus = [];
                    _this.totalGroupsA = [];
                    _this.totalGroupsB = [];
                }
            });
        };

        this.getGroup = function(id ,array){
            for(var i=0;i<array.length;i++){
                if(array[i].id == id) return array[i];
            }
        };


        //-------------------------------------电话簿---------------------------------------------
        this.phoneBooks = [];
        this.getPhoneBooks = function(type){
            phoneBookService.findAll({type:type},function(data){
               if(data.status == 'true'){
                   _this.phoneBooks = data.message;
                   if(type == 1 && _this.phoneBooks.length == 1){
                       _this.phoneBook = _this.phoneBooks[0];
                       //if(_this.totalGroups.length <= 0) _this.getTotalGroups();
                   }
               } else{
                   _this.phoneBookds = [];
                   console.log(data);
               }
            });
        };

        this.getPhoneBooks(1);

        //-------------------------------------联系人---------------------------------------------
        $scope.pageObject = {
            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            pages: []
        };

        this.filter = {};
        this.contacts = [];
        this.getContacts = function(type){
            if(type == 'reload'){
                $scope.pageObject.currentPage = 1;
            }
            this.filter.currentPage = $scope.pageObject.currentPage;
            this.filter.pageSize = $scope.pageObject.pageSize;
            this.filter.conductorId = $rootScope.loginUser.conductorId;
            this.filter.bookType = _this.type;
            if(this.group) {
                this.filter.groupId = this.group.id;
            }else{
                this.filter.groupId = null;
            }
            for(var k in this.filter){
                if(!this.filter[k]) this.filter[k] = null;
            }

            this.loading = true;
            contactService.pageList(this.filter,function(data,header){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.contacts = data.message;
                    $scope.pageObject.totalPage = header('page_count');
                    $scope.pageObject.pages = [];
                    for(var i=1;i<=$scope.pageObject.totalPage;i++){
                        $scope.pageObject.pages.push(i);
                    }
                }else{
                    _this.contacts = [];
                    console.log(data);
                }

            });
        };

        $scope.$watch('pageObject.currentPage',function(){
            _this.getContacts();
        });

        $scope.$watch('pageObject.totalPage',function(){
            paginationService.showFirstPageContent($scope.pageObject,1);
        });

        this.namePattern = commonService.regex('chinese');
        this.phonePattern = commonService.regex('telephone');

        this.addContact = function(flag){
            this.isNull = true;
            if(!flag) return;
            this.contact.groupId = this.group.id;
            this.contact.groupName = this.group.name;
            this.contact.bookId = this.group.bookId;
            this.loading = true;
            contactService.save(this.contact, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.cancel();
                    _this.group = null;
                    _this.getContacts('reload');
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };

        this.updateContact = function(flag){
            this.isNull = false;
            if(!flag) return;
            this.contact.groupId = this.group.id;
            this.contact.groupName = this.group.name;
            this.contact.bookId = this.group.bookId;
            this.loading = true;
            contactService.update(this.contact.id, this.contact, function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.cancel();
                    _this.group = null;
                    _this.getContacts();
                }
            });
        };

        //右键菜单
        this.rightMenuIndex = 0;
        $scope.isFocus = false;
        $scope.onOver = function(){
            $scope.isFocus = true;
        };

        $scope.onLeave = function(){
            $scope.isFocus = false;
        };

        $(document).click(function(){
            if(!$scope.isFocus){
                $scope.$apply(function(){
                    _this.rightMenuIndex = 0;
                });
            }
        });

        this.onItemRelease = function(event,group){
            if(event.button === 0){
                this.selectGroup(group);
            }else if(event.button === 2){
                this.rightMenuIndex = group.id;
            }
        }


        //定义确认弹出框提示问题
        var confirmInfo = {"delete":{tips:"请问是否删除该联系人?"},"deleteGroup":{tips:"请问是否删除该分组？"}};

        //操作确认
        $scope.confirmDialogShow = false;
        this.operGroup = null;
        $scope.confirmOper = function(type, obj){
            if(type.indexOf("Group") == -1 ){
                _this.contact = obj;
            }else{
                _this.rightMenuIndex = 0;
                _this.operGroup = obj;
            }
            $scope.confirmTips = "";
            $scope.confirmTips = confirmInfo[type].tips;
            $scope.confirmType = type;
            $scope.confirmDialogShow = true;
        };

        $scope.cancelConfirm = function(){
            $scope.confirmDialogShow = false;
        };

        //操作提交
        $scope.confirmCommit = function(type){
            $scope.cancelConfirm();
            if(type == "delete"){
                _this.deleteContact();
            }else if(type == 'deleteGroup'){
                _this.deleteGroup();
            }
        };

        this.deleteContact = function(){
            this.loading = true;
            contactService.delete(this.contact.id,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getContacts();
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };

        this.deleteGroup = function(){
            this.loading = true;
            groupService.delete(this.operGroup.id,function(data){
                _this.loading = false;
                if(data.status == 'true'){
                    _this.getGroups('reload');
                }else{
                    _this.message.show = true;
                    _this.message.text = data.message;
                    console.log(data);
                }
            });
        };


        //--------------------------上传联系人---------------------------------

        $scope.checkFile = function(){
            var docObj = document.getElementById("doc");
            var fileSize = docObj.files[0].size;
            var size = fileSize/1024;
            if(!/\.(xls|xlsx|XLS|XLSX)$/.test(docObj.value)){
                alert("文件类型必须是.xls,xlsx格式");
                docObj.outerHTML = docObj.outerHTML;
                return false;
            }
            if(size>5000){
                alert("文件大小不能超过5MB");
                docObj.outerHTML = docObj.outerHTML;
                return false;
            }
            return true;
        };

        this.uploadFile=function(flag){
            this.isNull = true;
            if(!flag) return;
            var docObj=document.getElementById("doc");
            if(docObj.files[0]!=undefined){
                ajaxFileUpload();
            }else{
                alert("未选择上传文件，请选择后再上传！");
            }
            function ajaxFileUpload() {
                _this.loading = true;
                $.ajaxFileUpload({
                    url: '/api/contact/excel/1',
                    type: 'post',
                    secureuri: false, //是否需要安全协议，一般设置为false
                    fileElementId: 'doc', // 上传文件的id、name属性名
                    dataType: 'json', //返回值类型，一般设置为json、application/json
                    success: function(data, status){
                        $scope.$apply(function(){
                            _this.loading = false;
                            _this.cancel();
                            _this.getContacts('reload');
                        });
                    },
                    error: function(data, status, e){
                        $scope.$apply(function(){
                            _this.loading = false;
                            _this.cancel();
                            _this.getContacts('reload');
                        });
                    }
                });
            }
        };
    });
});
