package cn.snzo.service;

import cn.snzo.vo.LogShow;
import org.springframework.data.domain.Page;

import java.util.Date;

/**
 * Created by chentao on 2017/7/15 0015.
 */
public interface ILogService {
    Page<LogShow> findPage(Integer operResType, String operator, Date createStart, Date createEnd, String operMethodName, Integer currentPage, Integer pageSize);
}
