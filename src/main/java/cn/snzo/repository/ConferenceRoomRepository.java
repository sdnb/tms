package cn.snzo.repository;

import cn.snzo.entity.ConferenceRoom;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30 0030.
 */
@Repository
@Transactional
public interface ConferenceRoomRepository extends CrudRepository<ConferenceRoom, Integer> {
    ConferenceRoom findByIvrPassword(String ivr);


    String findPage = "select cr.id, cr.number, cr.is_in_use, cr.is_record_enable, " +
            " cr.ivr_password, cr.max_participant, cr.conductor_id, cr.conductor_name" +
            " from t_conference_room cr " +
            " where (:number is null or cr.number like :number)" +
            " and (:ivr is null or cr.ivr_password like :ivr)" +
            " and (:name is null or cr.conductor_name like :name)";

    String findListByConductorId = "select cr.id, cr.number, cr.is_in_use, cr.is_record_enable, " +
            " cr.ivr_password, cr.max_participant, cr.conductor_id, cr.conductor_name" +
            " from t_conference_room cr " +
            " where (:conductorId is null or cr.conductor_id like :conductorId)";

    String getCount = "select count(*)" +
            " from t_conference_room cr" +
            " where (:number is null or cr.number like :number)" +
            " and (:ivr is null or cr.ivr_password like :ivr)" +
            " and (:name is null or cr.conductor_name like :name)";

    @Query("select count(room.id)  from ConferenceRoom room where room.conductorId = ?1 ")
    int checkConductorIsBind(int newCondutor);


    List<ConferenceRoom> findByConductorId(int id);

    @Query("update ConferenceRoom  set isInUse = ?2 where id= ?1")
    @Modifying
    int updateStatus(Integer roomId, int status);

    ConferenceRoom findByNumber(String confRoomNo);

}
