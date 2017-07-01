package cn.snzo.service.impl;

import cn.snzo.common.CommonUtils;
import cn.snzo.entity.Contact;
import cn.snzo.repository.ContactRepository;
import cn.snzo.service.IContactService;
import cn.snzo.vo.ContactShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Service
public class ContactService implements IContactService {

    @Autowired
    private ContactRepository contactRepository;


    @Override
    public Page<ContactShow> getPage(String name, String phone, Integer groupId, Integer bookId,
                                     Integer currentPage, Integer pageSize) {
        Pageable p = CommonUtils.createPage(currentPage, pageSize);
        name = CommonUtils.fuzzyString(name);
        phone = CommonUtils.fuzzyString(phone);
        Page<Contact> contacts = contactRepository.findPage(name, phone, p);
        List<Contact> contactList = contacts.getContent();
        List<ContactShow> contactShows = new ArrayList<>();
        for (Contact contact : contactList) {
            ContactShow contactShow = new ContactShow(contact);
            contactShows.add(contactShow);
        }
        return new PageImpl<ContactShow>(contactShows, p, contacts.getTotalElements());
    }



    @Override
    public int add(ContactShow contactShow) {
        String phone = contactShow.getPhone();
        Integer bookId = contactShow.getBookId();
//        Contact contact = contactRepository.checkExsits(phone, bookId);

//        //号码已存在
//        if (contact != null) {
//            return 2;
//        }

        return 0;
    }


    @Override
    public int delete(int id) {
        return 0;
    }

    @Override
    public int modify(int id, ContactShow contactShow) {
        return 0;
    }
}
