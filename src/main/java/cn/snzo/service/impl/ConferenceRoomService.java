package cn.snzo.service.impl;

import cn.snzo.common.CommonRepository;
import cn.snzo.common.NumberGenerator;
import cn.snzo.entity.Conductor;
import cn.snzo.entity.ConferenceRoom;
import cn.snzo.entity.PhoneBook;
import cn.snzo.repository.ConductorRepository;
import cn.snzo.repository.ConferenceRoomRepository;
import cn.snzo.repository.PhoneBookRepository;
import cn.snzo.service.IConferenceRoomService;
import cn.snzo.utils.BeanUtil;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.ConferenceRoomShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Service
public class ConferenceRoomService implements IConferenceRoomService {

    @Autowired
    private ConferenceRoomRepository conferenceRoomRepository;


    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private PhoneBookRepository phoneBookRepository;

    @Override
    @Transactional
    public int add(ConferenceRoomShow conferenceRoomShow) {
        String ivr = conferenceRoomShow.getIvrPassword();
        ConferenceRoom conferenceRoom = conferenceRoomRepository.findByIvrPassword(ivr);
        if (conferenceRoom != null) {
            return 2;
        }

        conferenceRoom = new ConferenceRoom(conferenceRoomShow);
        conferenceRoom.setIsInUse(0);
        conferenceRoom.setNumber(NumberGenerator.randomNumber());

        //主持人不为空
        Integer conductId = conferenceRoomShow.getConductorId();
        if (conductId != null) {
            Conductor conductor = conductorRepository.findOne(conductId);
            if (conductor != null) {
                int check = conferenceRoomRepository.checkConductorIsBind(conductId);
                //该主持人已有会议室
                if (check > 0) {
                    return 3;
                }
                conferenceRoom.setConductorName(conductor.getRealname());
            }
        }
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);
        //新增电话簿
        PhoneBook phoneBook = new PhoneBook();
        phoneBook.setRoomId(conferenceRoom.getId());
        phoneBook.setType(2);
        phoneBookRepository.save(phoneBook);

        return 1;
    }



    @Override
    public int modify(int id, ConferenceRoomShow conferenceRoomShow) {
        ConferenceRoom conferenceRoom = conferenceRoomRepository.findOne(id);
        if (conferenceRoom == null) {
            return 2;
        }

        //会议室正在使用，请待会议结束后修改
        if (conferenceRoom.getIsInUse() == 1) {
            return 3;
        }
        String ivr = conferenceRoomShow.getIvrPassword();
        //修改ivr密码
        if (!conferenceRoom.getIvrPassword().equals(ivr)) {
            ConferenceRoom conferenceRoom1 = conferenceRoomRepository.findByIvrPassword(ivr);
            //密码重复
            if (conferenceRoom1 != null && conferenceRoom1.getId() != id) {
                return 4;
            }
        }

        //修改会议室主持人
        Integer newCondutor = conferenceRoomShow.getConductorId();
        Integer oldCondutor = conferenceRoom.getConductorId();
        if (newCondutor == null) {
            //将主持人设置为空
            if (oldCondutor != null) {
                conferenceRoomShow.setConductorName(null);
            }
        } else {
            //绑定到新主持人
            if (oldCondutor == null || !oldCondutor.equals(newCondutor)) {
                int check = conferenceRoomRepository.checkConductorIsBind(newCondutor);
                //该主持人已经和别的会议室绑定
                if (check > 0) {
                    return 5;
                } else {
                    Conductor conductor = conductorRepository.findOne(newCondutor);
                    conferenceRoomShow.setConductorName(conductor.getRealname());
                }
            }
        }
        conferenceRoom = new ConferenceRoom(conferenceRoomShow);
        conferenceRoom.setId(id);
        conferenceRoomRepository.save(conferenceRoom);
        return 1;
    }

    @Override
    @Transactional
    public int delete(int id) {
        ConferenceRoom conferenceRoom = conferenceRoomRepository.findOne(id);
        if (conferenceRoom == null) {
            return 2;
        }
        //会议室正在使用，无法删除
        if (conferenceRoom.getIsInUse() == 1) {
            return 3;
        }
        conferenceRoomRepository.delete(id);
        return 1;
    }


    @Override
    public Page<ConferenceRoomShow> findPage(String ivr, String number, String name, Integer currentPage, Integer pageSize) {
        Pageable pageable = CommonUtils.createPage(currentPage, pageSize);
        name = CommonUtils.fuzzyString(name);
        number = CommonUtils.fuzzyString(number);
        ivr = CommonUtils.fuzzyString(ivr);
        Map<String, Object> params = new HashMap<>();
        params.put("number", number);
        params.put("name", name);
        params.put("ivr", ivr);
        @SuppressWarnings("unchecked")
        List<ConferenceRoomShow> conferenceRoomShows =
                (List<ConferenceRoomShow>)commonRepository
                        .queryResultToBeanPage(ConferenceRoomRepository.findPage, params, ConferenceRoomShow.class, currentPage, pageSize);
        long count = commonRepository.getCountBy(ConferenceRoomRepository.getCount, params);
        return new PageImpl<>(conferenceRoomShows, pageable, count);
    }

    @Override
    public List<ConferenceRoomShow> findByConductorId(Integer conductorId) {
        Map<String,Object> params = new HashMap<>();
        params.put("conductorId", conductorId);
        List<ConferenceRoomShow> conferenceRoomShows =
                (List<ConferenceRoomShow>)commonRepository
                    .queryResultToBeanList(ConferenceRoomRepository.findListByConductorId,params, ConferenceRoomShow.class);
        return conferenceRoomShows;
    }


    @Override
    public ConferenceRoomShow getOne(int roomId) {
        ConferenceRoom conferenceRoom = conferenceRoomRepository.findOne(roomId);
        ConferenceRoomShow conferenceRoomShow = new ConferenceRoomShow();
        BeanUtil.entityToShow(conferenceRoom, conferenceRoomShow);
        return conferenceRoomShow;
    }

    @Override
    public int modifyStatus(Integer roomId, int status) {
        int ret = conferenceRoomRepository.updateStatus(roomId, status);
        return 1;
    }
}
