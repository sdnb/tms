package cn.snzo.common;

import org.slf4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Created by chentao on 2017/3/6 0006.
 */
public class BaseController {

    /**
     * 打印错误日志
     * @param e
     * @param logger
     */
    protected void logErrInfo(Exception e, Logger logger){
        StringBuilder sbErr = new StringBuilder();
        for(StackTraceElement ste : e.getStackTrace()){
            sbErr.append("\n\t");
            sbErr.append(ste.toString());
        }
        logger.error("异常信息：" + sbErr.toString());
    }

//    /**
//     * 从cookie中得到用户信息
//     * @param tokenType
//     * @param token
//     * @param tokenService
//     * @return
//     */
//    protected String getTokenName(TokenType tokenType, String token, ITokenService tokenService){
//        if (tokenType.equals(TokenType.admin)){
//            StaffInfo staffInfo = (StaffInfo) tokenService.loadToken(token);
//            if(staffInfo == null){
//                return Constants.EMPTY_STRING;
//            }
//            return staffInfo.getUsername();
//        }else if(tokenType.equals(TokenType.user)) {
//            LoginInfo loginInfo = (LoginInfo) tokenService.loadToken(token);
//            if(loginInfo == null)
//                return Constants.EMPTY_STRING;
//            String name = loginInfo.getRealname();
//            if (name == null)
//                name = loginInfo.getPhone();
//            return name;
//        }
//        return Constants.EMPTY_STRING;
//    }

    /**
     * 返回失败信息
     * @param message
     * @return
     */
    protected ObjectResult failureRes(Object message){
        return new ObjectResult("false", message);
    }


    /**
     * 返回成功信息
     * @param message
     * @return
     */
    protected ObjectResult successRes(Object message){
        return new ObjectResult("true", message);
    }


    /**
     * 参数检查
     * @param bindingResult
     * @return
     */
    protected ObjectResult argCheck(BindingResult bindingResult){
        if ( bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder builder = new StringBuilder();
            for (ObjectError error : errors){
                builder.append(error.getDefaultMessage());
                builder.append(",");
            }
            String errMsg = builder.toString();
            return failureRes(errMsg.substring(0, errMsg.length() - 1));
        }
        return null;
    }
}
