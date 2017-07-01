package cn.snzo.service;

import cn.snzo.vo.ContactShow;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public interface IContactService {


    /**
     * 分页查联系人
     * @param name
     * @param phone
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<ContactShow> getPage(String name, String phone, Integer currentPage, Integer pageSize);


    int add(ContactShow contactShow);


    int delete(int id);


    int modify(int id, ContactShow contactShow);


    /**
     * 根据分组查联系人
     * @param groupId
     * @return
     */
    List<ContactShow> findByGroup(Integer groupId);


    /**
     * 查询不在分组内的联系人
     * @param bookId
     * @return
     */
    List<ContactShow> findNotInGroup(Integer bookId);

}
