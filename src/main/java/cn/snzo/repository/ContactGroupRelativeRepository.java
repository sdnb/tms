package cn.snzo.repository;

import cn.snzo.entity.ContactGroupRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Repository
public interface ContactGroupRelativeRepository extends JpaRepository<ContactGroupRelative, Integer> {


    @Modifying
    @Transactional
    @Query("delete from ContactGroupRelative c where c.groupId = ?1")
    void deleteByGroupId(int id);

    @Modifying
    @Transactional
    @Query("delete from ContactGroupRelative c where c.groupId = ?1 and c.contactId = ?2")
    int deleteContact(int groupId, int contactId);


//    @Query("select c.contactId from ContactGroupRelative c where c.groupId=?1")
//    List<Integer> findContactIds(Integer groupId);


    @Modifying
    @Transactional
    @Query("delete from ContactGroupRelative c where c.contactId = ?1")
    int deleteByContactId(int cid);
}
