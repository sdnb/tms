package cn.snzo.service.impl;

import cn.snzo.entity.Log;
import cn.snzo.repository.LogRepository;
import cn.snzo.service.ILogService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.LogShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chentao on 2017/7/15 0015.
 */
@Service
public class LogService implements ILogService {

    @Autowired
    private LogRepository logRepository;

    @Override
    public Page<LogShow> findPage(Integer operResType, String operator, Date createStart, Date createEnd, String operMethodName, Integer currentPage, Integer pageSize) {
        Pageable p = CommonUtils.createPage(currentPage, pageSize);
        operator = CommonUtils.fuzzyString(operator);
        operMethodName = CommonUtils.fuzzyString(operMethodName);
        Page<Log> logs = logRepository.findPage(operResType, operator, createStart, createEnd, operMethodName,  p);
        List<LogShow> logShows = new ArrayList<>();
        for (Log log : logs.getContent()) {
            LogShow logShow = new LogShow(log);
            logShows.add(logShow);
        }
        return new PageImpl<LogShow>(logShows, p, logs.getTotalElements());
    }
}
