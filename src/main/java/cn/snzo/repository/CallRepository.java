package cn.snzo.repository;

import cn.snzo.entity.Call;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
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
}
