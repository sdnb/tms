package cn.snzo.service.impl;

import cn.snzo.entity.Call;
import cn.snzo.repository.CallRepository;
import cn.snzo.service.ICallService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by Administrator on 2017/7/26 0026.
 */
@Service
public class CallService implements ICallService {


    @Autowired
    private CallRepository callRepository;


    @Override
    public Page<Call> findPage(String confResId, Integer roomId, String phone, Integer status, String name, Integer currentPage, Integer pageSize) {
        Pageable p = PageUtil.createPage(currentPage, pageSize);
        phone = CommonUtils.fuzzyString(phone);
        name = CommonUtils.fuzzyString(name);
        List<Call> calls = callRepository.findListByKeys(confResId, roomId, phone, status, name, p.getOffset(), p.getPageSize());
        int count = callRepository.countCallByKeys(confResId, roomId, phone, status, name);
        return new PageImpl<Call>(calls, p, count);
    }

    @Override
    public Integer getCountOnline(String confResId) {
        Integer count = callRepository.getCountByStatus(confResId, 2);
        return count;
    }
}
