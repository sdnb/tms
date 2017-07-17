package cn.snzo.service;

import cn.snzo.vo.ConferenceRoomShow;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public interface IConferenceRoomService {

    /**
     * 新增会议室
     * @param conferenceRoomShow
     * @return
     */
    int add(ConferenceRoomShow conferenceRoomShow);

    /**
     * 修改会议室
     * @param id
     * @param conferenceRoomShow
     * @return
     */
    int modify(int id, ConferenceRoomShow conferenceRoomShow);


    /**
     * 删除会议室
     * @param id
     * @return
     */
    int delete(int id);


    /**
     * 分页查会议室
     * @param ivr
     * @param number
     * @param name
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<ConferenceRoomShow> findPage(String ivr, String number, String name, Integer currentPage, Integer pageSize);

    /**
     *
     * @param conductorId
     * @return
     */
    List<ConferenceRoomShow> findByConductorId(Integer conductorId);

    ConferenceRoomShow getOne(int roomId);

    /**
     * 修改会议室状态
     * @param roomId
     * @param i
     * @return
     */
    int modifyStatus(Integer roomId, int i);


    /**
     * 查询主持人的会议室
     * @param conductorId
     * @return
     */
    ConferenceRoomShow getRoomByConductor(Integer conductorId);
}
