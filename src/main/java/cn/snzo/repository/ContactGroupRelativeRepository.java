package cn.snzo.repository;

import cn.snzo.entity.ContactGroupRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Repository
public interface ContactGroupRelativeRepository extends JpaRepository<ContactGroupRelative, Integer> {


    @Modifying
    @Query("delete from ContactGroupRelative c where c.groupId = ?1")
    void deleteByGroupId(int id);

    @Modifying
    @Query("delete from ContactGroupRelative c where c.groupId = ?1 and c.contactId = ?2")
    void deleteContact(int groupId, int contactId);


    @Query("select c.contactId from ContactGroupRelative c where c.groupId=?1")
    List<Integer> findContactIds(Integer groupId);
}
