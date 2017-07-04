package cn.snzo.repository;

import cn.snzo.entity.Contact;
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
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Query("select c from Contact c where" +
            " (?1 is null or c.name like ?1)" +
            " and (?2 is null or c.phone like ?2)")
    Page<Contact> findPage(Integer groupId, Integer bookId, String name, String phone, Pageable p);


    @Query("select c from Contact c where c.id in (?1)")
    List<Contact> findByIds(List<Integer> contactIds);


    @Query(value = "select * from t_contact c where c.book_id = ?2 and c.phone = ?1"
            ,nativeQuery = true)
    List<Contact> checkPhone(String phone, Integer bookId);

    Contact findByPhone(String phone);


    @Query(value = "select c.phone from Contact c where c.bookId = ?2 and c.phone in (?1)")
    List<String> checkPhones(List<String> phones, int bookId);



//    Contact checkExsits(String phone, Integer bookId);
}
