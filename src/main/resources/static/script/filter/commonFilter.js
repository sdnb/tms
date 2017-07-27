define(['../tms'], function(module){
    module.filter('operResult',function(){
        return function(input){
            if(input == 0) input = '成功';
            else if(input == 1) input = '失败';
            else if(input == 2) input = '超时';
            return input;
        }
    });

    module.filter('operType',function(){
        return function(input){
            if(input == 0) input = '创建';
            else if(input == 1) input = '操作';
            else if(input == 2) input = '事件';
            return input;
        }
    });

    module.filter('operResType',function(){
        return function(input){
            if(input == 0) input = '会议';
            else if(input == 1) input = '呼叫';
            return input;
        }
    });

    module.filter('voiceMode',function(){
        return function(input){
            if(input == 1) input = '放音+收音';
            else if(input == 2) input = '收音';
            else if(input == 3) input = '放音';
            else if(input == 4) input = '无';
            return input;
        }
    });

    module.filter('conferenceStatus',function(){
        return function(input){
            if(input == 1) input = '会议中';
            else if(input == 2) input = '已结束';
            return input;
        }
    });

    module.filter('direction',function(){
        return function(input){
            if(input == 1) input = '呼入';
            else if(input == 2) input = '呼出';
            return input;
        }
    });

    //与会人状态
    module.filter('status',function(){
        return function(input){
            if(input == 1) input = '振铃';
            else if(input == 2) input = '参会中';
            else if(input == 3) input = '已离会';
            else if(input == 4) input = '未接听';
            return input;
        }
    });
});