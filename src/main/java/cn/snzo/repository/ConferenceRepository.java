package cn.snzo.repository;

import cn.snzo.entity.Conference;
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
}
