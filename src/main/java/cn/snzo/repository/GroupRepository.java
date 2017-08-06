package cn.snzo.repository;

import cn.snzo.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    Group findByName(String name);

    Group findByNameAndBookId(String name, Integer bookId);


//    @Query(value = "select xc from t_group xc " +
//            " inner join t_phone_book pb on pb.id = xc.book_id " +
//            " inner join t_conference_room cr on cr.id = pb.room_id" +
//            " left join t_conductor cd on cd.room_id = cr.id" +
//            " where (?1 is null or xc.name like ?1)" +
//            " and (?2 is null or cd.id = ?2)" +
//            " and (?3 is null or cx.book_id = ?3)" +
//            " ", nativeQuery = true)
//    Page<Group> findPage(String name, Integer conductorId, Integer bookId, Pageable p);

    @Query("select xc from Group xc")
    List<Group> findTotal();
}
