package cn.snzo.repository;

import cn.snzo.entity.Conference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by chentao on 2017/7/11 0011.
 */
@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Integer> {
    Conference findByResId(String resId);

    @Query(value = "select count(*) from t_conference c  inner join t_conference_room r on c.room_id=r.id where c.status = 1 and r.id=?1", nativeQuery = true)
    int checkConfOfRoom(int roomId);


    @Query("select c from Conference c where " +
            " (?1 is null or c.id = ?1)" +
            " and (?2 is null or c.roomId = ?2)" +
            " and (?3 is null or c.status = ?3)" +
            " and (?4 is null or c.conductorId = ?4)" +
            " and (?5 is null or c.resId like ?5)")
    Page<Conference> findPage(Integer id, Integer roomId, Integer status, Integer conductorId, String confResId, Pageable p);


    @Query("select c from Conference c where c.roomId=?1 and c.status=?2")
    Conference findByRoomIdAndStatus(int roomId, int status);
}
