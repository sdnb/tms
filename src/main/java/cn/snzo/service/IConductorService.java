package cn.snzo.service;

import cn.snzo.entity.Conductor;
import cn.snzo.vo.ConductorShow;
import org.springframework.data.domain.Page;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
public interface IConductorService {

    int add(ConductorShow conductorShow);

    Page<ConductorShow> findPage(String name, String phone, Integer pageSize, Integer currentPage);

    int delete(int id);

    int modify(int id, ConductorShow conductorShow);

    Conductor getOne(Integer conductorId);
}
