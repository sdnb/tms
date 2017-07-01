package cn.snzo.repository;

import cn.snzo.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Query("select c from Contact c where (?1 is null or c.name like ?1)" +
            " and (?2 is null or c.phone like ?2)")
    Page<Contact> findPage(String name, String phone, Pageable p);


//    Contact checkExsits(String phone, Integer bookId);
}
