package cn.snzo.repository;

import cn.snzo.entity.Call;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


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
            " (?5 is null or c.name like ?5)")
    Page<Call> findPage(String confResId, Integer roomId, String phone, Integer status,
                        String name, Pageable p);


    @Modifying
    @Query("update  Call  c set c.status = ?2 where c.resId = ?1")
    int updateStatus(String callId, int status);


    @Modifying
    @Query("update Call c set c.voiceMode = ?2 where c.resId = ?1")
    int updateVoiceMode(String callId, int voiceMode);
}
