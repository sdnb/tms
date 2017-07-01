package cn.snzo.service.impl;

import cn.snzo.common.CommonRepository;
import cn.snzo.common.CommonUtils;
import cn.snzo.common.NumberGenerator;
import cn.snzo.entity.Conductor;
import cn.snzo.entity.ConferenceRoom;
import cn.snzo.entity.PhoneBook;
import cn.snzo.entity.RoomConductorRelative;
import cn.snzo.repository.ConductorRepository;
import cn.snzo.repository.ConferenceRoomRepository;
import cn.snzo.repository.PhoneBookRepository;
import cn.snzo.repository.RoomCondutorRelativeRepository;
import cn.snzo.service.IConferenceRoomService;
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
    private RoomCondutorRelativeRepository roomCondutorRelativeRepository;

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
        conferenceRoom = conferenceRoomRepository.save(conferenceRoom);
        //主持人不为空
        Integer conductId = conferenceRoomShow.getConductorId();
        if (conductId != null) {
            Conductor conductor = conductorRepository.findOne(conductId);
            if (conductor != null) {
                RoomConductorRelative roomConductorRelative = new RoomConductorRelative();
                roomConductorRelative.setConductorId(conductId);
                roomConductorRelative.setConductorName(conductor.getRealname());
                roomConductorRelative.setRoomId(conferenceRoom.getId());
                roomCondutorRelativeRepository.save(roomConductorRelative);
            }
        }

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

        List<RoomConductorRelative> roomConductorRelatives = roomCondutorRelativeRepository.findByRoomId(id);
        if (!roomConductorRelatives.isEmpty()) {
            RoomConductorRelative roomConductorRelative = roomConductorRelatives.get(0);
            //修改会议室主持人
            if (!roomConductorRelative.getConductorId().equals(conferenceRoomShow.getConductorId())) {
                roomConductorRelative.setConductorId(conferenceRoomShow.getConductorId());
                roomCondutorRelativeRepository.save(roomConductorRelative);
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
        roomCondutorRelativeRepository.deleteByRoomId(id);
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



}
