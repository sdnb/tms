package cn.snzo.repository;

import cn.snzo.entity.PhoneBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Repository
public interface PhoneBookRepository extends JpaRepository<PhoneBook, Integer> {

    @Modifying
    @Query("delete from PhoneBook b where b.roomId = ?1")
    void deleteByRoomId(int roomId);


    @Query("select p from PhoneBook p where (?1 is null or p.type =?1) " +
            " and (?2 is null or p.roomId = ?2)")
    List<PhoneBook> findByTypeAndRoomId(Integer type, Integer roomId);
}
