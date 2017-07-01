package cn.snzo.repository;

import cn.snzo.entity.RoomConductorRelative;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/6/30 0030.
 */
@Repository
public interface RoomCondutorRelativeRepository extends CrudRepository<RoomConductorRelative, Integer> {

    @Modifying
    @Query("delete from RoomConductorRelative r where r.conductorId = ?1")
    void deleteByCondutorId(int condutorId);

    List<RoomConductorRelative> findByConductorId(int conductorId);

    List<RoomConductorRelative> findByRoomId(int id);

    @Modifying
    @Query("delete from RoomConductorRelative r where r.roomId = ?1")
    void deleteByRoomId(int id);
}
