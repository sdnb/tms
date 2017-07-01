package cn.snzo.repository;

import cn.snzo.entity.ConferenceRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/6/30 0030.
 */
@Repository
public interface ConferenceRoomRepository extends CrudRepository<ConferenceRoom, Integer> {
    ConferenceRoom findByIvrPassword(String ivr);


    String findPage = "select cr.id, cr.number, cr.is_in_use, cr.is_record_enable, " +
            " cr.ivr_password, cr.max_participant, rcr.conductor_id, rcr.conductor_name" +
            " from t_conference_room cr " +
            " left join t_room_conductor_relative rcr " +
            " on cr.id = rcr.room_id " +
            " where (:number is null or cr.number like :number)" +
            " and (:ivr is null or cr.ivr_password like :ivr)" +
            " and (:name is null or rcr.conductor_name like :name)";

    String getCount = "select count(*)" +
            " from t_conference_room cr left join t_room_conductor_relative rcr " +
            " on cr.id = rcr.room_id " +
            " where (:number is null or cr.number like :number)" +
            " and (:ivr is null or cr.ivr_password like :ivr)" +
            " and (:name is null or rcr.conductor_name like :name)";
}
