package cn.snzo.service;

import cn.snzo.entity.Call;
import org.springframework.data.domain.Page;

/**
 * Created by Administrator on 2017/7/26 0026.
 */
public interface ICallService {
    Page<Call> findPage(String confResId, Integer roomId, String phone, Integer status, String name, Integer currentPage, Integer pageSize);

    Integer getCountOnline(String confResId);
}
