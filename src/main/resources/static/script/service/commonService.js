define(['../tms', 'jquery', 'angular'], function(module, $, angular){
    return module.service('commonService', (function(){
        function CommonService($http,$resource,$timeout,$location){
            this.$http = $http;
            this.$resource = $resource;
            this.$timeout = $timeout;
            this.$location = $location;
        }

        //通用正则表达式
        CommonService.prototype.regex = function(type){
            var regex ;
            if(type == 'phone'){
                regex = /^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/; //定义手机phone的正则表达式
            }else if(type == 'password'){
                regex = /^[a-zA-Z].{5,33}$/;//定义密码password的正则表达式
            }else if(type=='date'){
                regex=/^(((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))(\s+(([01]?\d|2[0-3]):[0-5]?\d(:[0-5]?\d)?))?$)/;
            }else if(type=='licenseNum'){
                regex=/^\d{15}$/; //定义企业执照注册号
            }else if(type=='hour'){
                regex=/^([01]\d|2[0-3])\:([0-5]\d)$/;
            }else if(type=='vendorNum'){
                regex=/^.{1,8}$/; //商户编号
            }else if(type == 'vendorName'){
                regex = /^.{1,20}$/; //商户编号
            }else if(type == "carNo" ){
                regex = /^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$/; //车牌号
            }else if(type == "specialType"){
                regex =/^[A-Za-z\d-]+$/;//匹配字母+数字+"-"
            }else if(type=='displacement'){
                regex =/^\d{1,1}(\.\d{1,1})+[A-Z]{1,1}$/;
            }else if(type=='seatNum'){
                regex =/^\d{0,2}$/;
            }else if(type == "mustNumLetter"){
                regex =/^(?![^a-zA-Z]+$)(?!\D+$).{1,17}$/ ;
            }else if(type == 'mustNumber'){
                regex =/^\d*$/;//只能为数字
            }else if(type == 'mustNumber2'){
                regex =/^\d{0,6}$/;//只能为数字且在0-6位
            }else if(type == 'mustNumber3'){
                regex =/^\d{0,8}$/;//只能为数字且在0-8位
            }else if(type=='inputLengthLimit'){
                regex =/^.{1,10}$/;//输入框长度限制
            }else if(type=='date'){
                regex =/^([0-9]{4})-([0-9]{2})-([0-9]{2})$/;//日期格式:YYYY-MM-DD
            }else if(type=="vin"){
                regex = /^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9].{16}$/;
            }else if(type == 'card'){
                regex = /^(\d{15}$|^\d{18}$|^\d{17}(\d|X|x))$/; //定义身份证的正则表达式
            }else if(type == 'hmpass'){
                regex = /^[HMhm]{1}([0-9]{10}|[0-9]{8})$/; //定义港澳通行证的正则表达式
            }else if(type == 'tw'){
                regex = /^(([0-9]{8})|([0-9]{10}))$/; //定义台湾通行证的正则表达式
            }else if(type == 'passport'){
                regex = /^(([a-zA-Z]{5,17})|([a-zA-Z0-9]{5,17}))$/; //定义护照的正则表达式
            }else if(type == 'chinese'){
                regex = /^[\u4e00-\u9fa5]{2,5}$/; //定义真实姓名的正则表达式
            }else if(type == 'email'){
                regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
                //regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;//邮箱正则表达式
            }else if(type == 'money'){  //匹配金额
                regex = /^(\d{1,10}|\d{1,10}\.\d{1,2})$/;
            }else if(type == 'chinaParagraph'){
                regex = /[\u4e00-\u9fa5]{1,500}/;
            }else if(type == 'bankAccount'){  //银行卡账号正则表达式
                regex = /^\d{15,100}$/;
            }else if(type == 'terminalCode'){ //终端号
                regex = /^[a-zA-Z]\d{7,10}$/;
            }else if(type == 'authCode'){  //授权码
                regex = /^\d{6}$/;
            }else if(type == 'telephone'){
                regex = /^(0[0-9]{2,3}(\-)?)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$|(^(13[0-9]|15[0-9]|18[0-9]|17[0-9])\d{8}$)|(^(\d{12}|\d{5}|\d{4}|\d{11}|\d{8}|\d{11})$)/;
            }else if(type == 'postCode'){
                regex = /^[1-9]\d{5}$/;
            }else if(type=="intFloatNUm"){
                regex=/^\d{0,8}(\.\d{0,2})?$/;
            }else if(type == 'vin'){  //车辆Vin码
                regex = /^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9].{16}$/;
            }else if(type == 'aliAccount'){ //支付宝账号
                regex = /^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$|^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+$/;
            }else if(type == 'alipayNo'){ //支付宝交易号
                regex = /^\d{28}$|^\d{32}$/;
            }else if(type == 'mustPositiveNumber'){
                regex =/^[1-9]\d*$/;//只能为正整数
            }else if(type == 'invoiceNum'){
                regex =/^[1-9]\d{7}$/;//发票号
            }else if(type == 'orderId'){ //订单合同编号
                regex = /^\d{7}$/;
            }else if(type == 'positiveInteger'){//正整数
                regex = /^\+?[1-9][0-9]*$/;
            }else if(type == 'birthDate'){
                regex = /^[1-9][0-9]{3}-\d{2}-\d{2}$/;
            }else if(type == 'nonNegativeInteger'){
                regex = /^\d+$/;
            }else if (type=='china'){
                regex = /^[a-zA-Z\u4e00-\u9fa5]$/;//只能为大小写字母和汉字
            }else if (type=='discount'){
                regex=/^(?=0\.[1-9]|[1-9]\.\d).{3}$|^([1-9])$/;
            }else if (type == 'aliPayerName'){ //付款方式为支付宝收款支付人(中文姓名，手机号或邮箱)
                regex = /^[\u4e00-\u9fa5]{2,5}$|^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$|^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+$/;
            }else if(type == 'nonNegativeInteger'){  //非负整数
                regex=/^\d+$/;
            }else if(type=='greaterThan0'){
                regex = /^\+?[1-9]\d*$/; //大于0的正整数
            }else if(type=='greaterThan2'){
                regex = /^[1-9]{1}[0-9]\d*|[3-9]$/; //大于2的正整数
            }else if(type=='payerName'){ //收款支付人（有可能为公司）
                regex = /^[\u4e00-\u9fa5]{2,40}$/;
            }else if(type=='oneDecimal'){  //一位小数
                regex = /^[1-9]\d*([.][1-9])?$/;
            }else if(type=='nonNegativeInteger'){  //非负整数（正整数+0）
                regex = /^\d+$/;
            }else if(type=='twoDecimal'){  //2位小数金额
                regex = /^[1-9]\d*([.][0-9]{1,2})?$/;
            }else if(type == 'shortChinese'){    //50位的中文，可附加数字，字母，标点，但必须有中文
                regex = /[\u4e00-\u9fa5]{1,50}/;
            }else if(type=='handlePrice'){  //两位小数的非负浮点数输入
                regex = /^(([1-9]\d{0,9})|0)(\.\d{1,2})?$/;
            }else if(type=="handleUnit"){ //长度不能超过30
                regex = /^\S{1,30}$/;
            }else if(type=="handleUnit80"){ //长度不能超过100
                regex = /^\S{1,80}$/;
            }else if(type=="handleUnit50"){ //长度不能超过100
                regex = /^\S{1,50}$/;
            }else if(type=="account"){
                regex = /^[a-zA-Z][a-zA-Z_0-9]{1,9}$/;
            }else if(type=="ivr"){
                regex = /^[a-zA-Z0-9]{1,4}$/
            }

            return regex;
        };

        //获取cookie
        CommonService.prototype.getCookie = function(name){
            var arr = document.cookie.split("; ");
            for(var i=0,len=arr.length;i<len;i++){
                var item = arr[i].split("=");
                if(item[0]==name){
                    return item[1];
                }
            }
            return "";
        };

        //创建cookie
        CommonService.prototype.addCookie = function(name,value){
            var exp = new Date();
            exp.setTime(exp.getTime() + 30*60*1000);
            document.cookie= name+"="+value+";path=/;expires="+exp.toGMTString();
        };

        //删除cookie
        CommonService.prototype.deleteCookie = function(name){
            var exp = new Date();
            exp.setTime(exp.getTime() - 1000);
            var cval=this.getCookie(name);
            if(cval!=null){
                document.cookie= name+"="+cval+";expires="+exp.toUTCString()+"; path=/";
            }
        };

        //毫秒转日期
        CommonService.prototype.dateFormat = function(time, format){
            var t = new Date(time);
            var tf = function(i){return (i < 10 ? '0' : '') + i};
            return format.replace(/yyyy|MM|dd|HH|mm|ss/g, function(a){
                switch(a){
                    case 'yyyy':
                        return tf(t.getFullYear());
                        break;
                    case 'MM':
                        return tf(t.getMonth() + 1);
                        break;
                    case 'mm':
                        return tf(t.getMinutes());
                        break;
                    case 'dd':
                        return tf(t.getDate());
                        break;
                    case 'HH':
                        return tf(t.getHours());
                        break;
                    case 'ss':
                        return tf(t.getSeconds());
                        break;
                }
            })
        };

        //获取登录用户信息
        CommonService.prototype.getLoginInfo = function(cb){
            var loginReInfoApi = this.$resource('/api/login/:token');  //返回登录用户的信息
            var token = this.getCookie("token");
            loginReInfoApi.get({token:token},function(data){
                cb(data);
            },function(errData){
                console.log(errData.data.error);
            })
        };


        //加载日期控件
        CommonService.prototype.changeDatePicker = function(type,ids,startDate){
            if(type == "date"){
                angular.forEach(ids,function(id){
                    $('#'+id).datetimepicker({
                        language:  'zh-CN',
                        format: 'yyyy-mm-dd',
                        weekStart: 1,
                        todayBtn:  1,
                        autoclose: 1,
                        todayHighlight: 1,
                        startView: 2,
                        minView: 2,
                        forceParse: 0,
                        startDate: startDate,
                        initialDate: startDate
                    });
                })
            }else if(type == "time"){
                angular.forEach(ids,function(id){
                    $('#'+id).datetimepicker({
                        language:  'zh-CN',
                        format: 'hh:ii',
                        weekStart: 1,
                        todayBtn:  1,
                        autoclose: 1,
                        todayHighlight: 1,
                        startView: 1,
                        minView: 0,
                        maxView: 1,
                        forceParse: 0
                    });
                })
            }else if(type == "datetime"){
                angular.forEach(ids,function(id){
                    $('#'+id).datetimepicker({
                        language:  'zh-CN',
                        format: 'yyyy-mm-dd hh:ii:ss',
                        weekStart: 1,
                        todayBtn:  1,
                        autoclose: 1,
                        todayHighlight: 1,
                        startView: 2,
                        forceParse: 0,
                        showMeridian: 1
                    });
                })
            }else if(type == "dateSelectHour"){
                angular.forEach(ids,function(id){
                    $('#'+id).datetimepicker({
                        language:  'zh-CN',
                        format: 'yyyy-mm-dd hh:ii:ss',
                        weekStart: 1,
                        todayBtn:  1,
                        autoclose: 1,
                        todayHighlight: 1,
                        startView: 2,
                        minView: 1,
                        maxView: 2,
                        forceParse: 0,
                        startDate:new Date(new Date().valueOf() + 60*60*1000*2),
                        showMeridian: 1
                    });
                })
            }else if(type == "dateWithoutYear"){
                angular.forEach(ids,function(id){
                    $('#'+id).datetimepicker({
                        language:  'zh-CN',
                        format: 'mm-dd',
                        weekStart: 1,
                        todayBtn:  1,
                        autoclose: 1,
                        todayHighlight: 1,
                        startView: 2,
                        minView: 2,
                        forceParse: 0,
                        startDate: startDate,
                        initialDate: startDate
                    });
                })
            }else if(type == "dateWithMinute"){
                angular.forEach(ids,function(id){
                    $('#' + id).datetimepicker({
                        language: 'zh-CN',
                        format: 'yyyy-mm-dd hh:ii',
                        weekStart: 1,
                        todayBtn:  1,
                        autoclose: 1,
                        todayHighlight: 1,
                        startView: 2,
                        startDate: startDate,
                        initialDate: startDate
                    });
                });
            }
        };


        return ['$http', '$resource','$timeout','$location', CommonService];
    })());
});