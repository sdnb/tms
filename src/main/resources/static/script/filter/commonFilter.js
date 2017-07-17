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
            else if(input == 1) input = '事件';
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
});