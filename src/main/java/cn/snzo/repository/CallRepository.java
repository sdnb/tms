package cn.snzo.repository;

import cn.snzo.entity.Call;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;


/**
 * Created by Administrator on 2017/7/25 0025.
 */
@Repository
public interface CallRepository extends JpaRepository<Call, Integer>{


    @Query("select c from Call c where " +
            " (?1 is null or c.confResId = ?1) and " +
            " (?2 is null or c.roomId = ?2) and" +
            " (?3 is null or c.phone like ?3) and " +
            " (?4 is null or c.status = ?4) and " +
            " (?5 is null or c.name like ?5) ")
    Page<Call> findPage(String confResId, Integer roomId, String phone, Integer status,
                        String name, Pageable p);


    @Query(value = " select c.* from t_call c" +
            "  inner join " +
            " (select max(id) mid from t_call GROUP BY phone, conf_res_id) t " +
            " on t.mid = c.id " +
            " where " +
            " (?1 is null or c.conf_res_id = ?1) and " +
            " (?2 is null or c.room_id = ?2) and" +
            " (?3 is null or c.phone like ?3) and " +
            " (?4 is null or c.status = ?4) and " +
            " (?5 is null or c.name like ?5) " +
            " order by c.status limit ?6, ?7 "
            , nativeQuery = true)
    List<Call> findListByKeys(String confResId, Integer roomId, String phone, Integer status,
                        String name, int page, int pageSize);


    @Query(value = " select count(*) from t_call c" +
            "  inner join " +
            " (select max(id) mid from t_call group by phone, conf_res_id) t " +
            " on t.mid = c.id " +
            " where " +
            " (?1 is null or c.conf_res_id = ?1) and " +
            " (?2 is null or c.room_id = ?2) and" +
            " (?3 is null or c.phone like ?3) and " +
            " (?4 is null or c.status = ?4) and " +
            " (?5 is null or c.name like ?5) "
            , nativeQuery = true)
    int countCallByKeys(String confResId, Integer roomId, String phone, Integer status,
                        String name);



    @Transactional
    @Modifying
    @Query("update  Call  c set c.status = ?2 where c.resId = ?1")
    int updateStatus(String callId, int status);


    @Transactional
    @Modifying
    @Query("update Call c set c.voiceMode = ?2 where c.resId = ?1")
    int updateVoiceMode(String callId, int voiceMode);

    Call findByResId(String callId);


    @Query("select c.phone from Call c where c.confResId=?1 and c.phone in (?2) and c.status=?3")
    List<String> findPhoneByConfResIdAndPhonesAndStatus(String conferenceId, List<String> phones, int status);


    @Query("select c from Call c where c.confResId=?1 and c.phone in (?2) and c.status=?3")
    List<Call> findCallByConfResIdAndPhoneAndStatus(String conferenceId, String phone, int status);


    @Query("select  c.phone from  Call c where c.phone in (?1) and (c.status=1 or c.status=2)")
    List<String> findCallIsOnByPhone(List<String> phone);

    @Query("select count(c) from Call c where c.status=?2 and c.confResId = ?1")
    Integer getCountByStatus(String confResId, int status);


    @Query("select c from Call c where c.confResId = ?1 and c.status in (?2)")
    List<Call> findCallByConfResIdAndStatus(String resId, List<Integer> status);


    @Query("select c from Call c where c.phone = ?1 and c.confResId is null")
    List<Call> findCallByPhoneAndConfIsNull(String phone);
}
