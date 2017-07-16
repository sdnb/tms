package cn.snzo.service.impl;

import cn.snzo.entity.Conference;
import cn.snzo.repository.ConferenceRepository;
import cn.snzo.service.IConferenceService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.utils.PageUtil;
import cn.snzo.vo.ConferenceShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chentao on 2017/7/15 0015.
 */
@Service
public class ConferenceService implements IConferenceService {

    @Autowired
    private ConferenceRepository conferenceRepository;


    @Override
    public Page<ConferenceShow> findPage(Integer id, Integer roomId, Integer status, Integer conductorId, String confResId, Integer currentPage, Integer pageSize) {
        Pageable p = PageUtil.createPage(currentPage, pageSize);
        confResId = CommonUtils.fuzzyString(confResId);

        Page<Conference> conferences = conferenceRepository.findPage(id, roomId, status, conductorId, confResId, p);
        List<Conference> conferencesList = conferences.getContent();
        List<ConferenceShow> conferencesShowList = new ArrayList<>();
        for (Conference conference : conferencesList) {
            ConferenceShow conferenceShow = new ConferenceShow(conference);
            conferencesShowList.add(conferenceShow);
        }
        return new PageImpl<>(conferencesShowList, p, conferences.getTotalElements());
    }
}
