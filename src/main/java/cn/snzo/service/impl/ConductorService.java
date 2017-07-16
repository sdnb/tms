package cn.snzo.service.impl;

import cn.snzo.entity.Account;
import cn.snzo.entity.Conductor;
import cn.snzo.entity.ConferenceRoom;
import cn.snzo.repository.AccountRepository;
import cn.snzo.repository.ConductorRepository;
import cn.snzo.repository.ConferenceRoomRepository;
import cn.snzo.service.IAccountService;
import cn.snzo.service.IConductorService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.AccountShow;
import cn.snzo.vo.ConductorShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
@Service
public class ConductorService implements IConductorService {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private ConferenceRoomRepository conferenceRoomRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public int add(ConductorShow conductorShow) {
        AccountShow accountShow = conductorShow.getAccountShow();
        if (accountShow == null) {
            return 2;
        }

        String phone = conductorShow.getPhone();
        Conductor conductor = conductorRepository.findByPhone(phone);
        if (conductor != null) {
            return 3;
        }
        AccountShow show = accountService.add(accountShow);
        conductor = new Conductor(conductorShow);
        if (show != null) {
            conductor.setAccountId(show.getId());
        }
        conductorRepository.save(conductor);
        return 1;
    }

    @Override
    public Page<ConductorShow> findPage(String name, String phone, Integer pageSize, Integer currentPage) {
        Pageable pageable = CommonUtils.createPage(currentPage, pageSize);
        name = CommonUtils.fuzzyString(name);
        phone = CommonUtils.fuzzyString(phone);
        Page<Conductor> conductors = conductorRepository.findPage(name, phone, pageable);

        List<Conductor> conductorList = conductors.getContent();
        List<ConductorShow> conductorShows = conductorList.stream().map(e -> {
            return new ConductorShow(e);
        }).collect(Collectors.toList());
        return new PageImpl<ConductorShow>(conductorShows, pageable, conductors.getTotalElements());
    }



    @Override
    @Transactional
    public int delete(int id) {
        Conductor conductor = conductorRepository.findOne(id);
        if (conductor == null) {
            return 3;
        }
        //删除主持人所绑定的会议室
        //如果会议室状态为使用中，则无法删除该主持人
        List<ConferenceRoom> conferenceRooms = conferenceRoomRepository.findByConductorId(id);

        if (!conferenceRooms.isEmpty()) {
            ConferenceRoom room = conferenceRooms.get(0);
            if (room.getIsInUse() == 1) {
                return 2;
            } else {
                room.setConductorName(null);
                room.setConductorId(null);
            }
            conferenceRoomRepository.save(room);
        }

        //删除主持人对应的账号
        Account account = accountRepository.findByUsername(conductor.getUsername());
        accountRepository.delete(account);
        conductorRepository.delete(id);
        return 1;
    }


    @Override
    public int modify(int id, ConductorShow conductorShow) {
        Conductor conductor = conductorRepository.findOne(id);
        if (conductor == null) {
            return 2;
        }
        //电话已存在
        String newPhone = conductorShow.getPhone();
        if (!conductor.getPhone().equals(newPhone)) {
            Conductor old = conductorRepository.findByPhone(newPhone);
            if (old != null) {
                return 3;
            }
        }

        conductorRepository.save(new Conductor(conductorShow));
        return 1;
    }

    @Override
    public Conductor getOne(Integer conductorId) {
        Conductor conductor = conductorRepository.findOne(conductorId);
        return conductor;
    }

}
