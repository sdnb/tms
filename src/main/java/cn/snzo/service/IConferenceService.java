package cn.snzo.service;

import cn.snzo.vo.ConferenceShow;
import org.springframework.data.domain.Page;

/**
 * Created by chentao on 2017/7/15 0015.
 */
public interface IConferenceService {
    Page<ConferenceShow> findPage(Integer id, Integer roomId, Integer status, Integer conductorId, String confResId, Integer currentPage, Integer pageSize);
}
