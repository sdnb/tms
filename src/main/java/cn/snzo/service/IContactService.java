package cn.snzo.service;

import cn.snzo.vo.ContactShow;
import org.springframework.data.domain.Page;

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
    Page<ContactShow> getPage(String name, String phone, Integer groupId, Integer bookId,
                              Integer currentPage, Integer pageSize);


    int add(ContactShow contactShow);


    int delete(int id);


    int modify(int id, ContactShow contactShow);



}
