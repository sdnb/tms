package cn.snzo.controller;

import cn.snzo.common.BaseController;
import cn.snzo.common.Constants;
import cn.snzo.common.ObjectResult;
import cn.snzo.service.ILogService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.LogShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by chentao on 2017/7/15 0015.
 */
@RestController
@RequestMapping("/api")
public class LogController extends BaseController{


    private static Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private ILogService logService;


    /**
     * 分页查询日志
     * @param operResType 操作类型
     * @param operator 操作人
     * @param createStart 创建时间开始
     * @param createEnd 创建时间结束
     * @param currentPage
     * @param pageSize
     * @param response
     * @return
     */
    @RequestMapping(value = "/log/page", method = RequestMethod.GET)
    public ObjectResult findPage(@RequestParam(value = "operResType", required = false) Integer operResType,
                                 @RequestParam(value = "operator", required = false) String operator,
                                 @RequestParam(value = "operMethodName", required = false) String operMethodName,
                                 @DateTimeFormat(pattern = Constants.FORMATE_yyyyMMddHHmmss)
                                 @RequestParam(name = "createStart", required = false) Date createStart,
                                 @DateTimeFormat(pattern = Constants.FORMATE_yyyyMMddHHmmss)
                                 @RequestParam(name = "createEnd", required = false) Date createEnd,
                                 @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                 @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                 HttpServletResponse response) {
        try {
            Page<LogShow> logShowPage = logService.findPage(operResType, operator, createStart, createEnd, operMethodName, currentPage, pageSize);
            CommonUtils.setResponseHeaders(logShowPage.getTotalElements(), logShowPage.getTotalPages(), logShowPage.getNumber(), response);
            return successRes(logShowPage.getContent());
        } catch (Exception e) {
            logErrInfo(e, logger);
            return failureRes("获取日志列表异常");
        }
    }
}
