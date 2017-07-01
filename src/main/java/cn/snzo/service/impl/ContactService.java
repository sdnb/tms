package cn.snzo.service.impl;

import cn.snzo.common.CommonRepository;
import cn.snzo.common.CommonUtils;
import cn.snzo.entity.Contact;
import cn.snzo.entity.ContactGroupRelative;
import cn.snzo.repository.ContactGroupRelativeRepository;
import cn.snzo.repository.ContactRepository;
import cn.snzo.service.IContactService;
import cn.snzo.vo.ContactShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Service
public class ContactService implements IContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private CommonRepository commonRepository;

    @Autowired
    private ContactGroupRelativeRepository contactGroupRelativeRepository;

    @Override
    public Page<ContactShow> getPage(String name, String phone, Integer currentPage, Integer pageSize) {
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
        String  phone  = contactShow.getPhone();
        Integer bookId = contactShow.getBookId();
        Integer groupId = contactShow.getGroupId();
        Contact c = new Contact(contactShow);
        c = contactRepository.save(c);
        if (groupId != null) {
            ContactGroupRelative cgr = new ContactGroupRelative();
            cgr.setGroupId(groupId);

        }

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

    @Override
    public List<ContactShow> findByGroup(Integer groupId) {

        List<Contact> contacts = new ArrayList<>();

        List<Integer> contactIds = contactGroupRelativeRepository.findContactIds(groupId);

        if (contactIds != null && !contactIds.isEmpty()) {
            contacts = contactRepository.findByIds(contactIds);
        }

        List<ContactShow> contactShows = new ArrayList<>();
        for (Contact c : contacts) {
            ContactShow s = new ContactShow(c);
            contactShows.add(s);
        }
        return contactShows;
    }

    @Override
    public List<ContactShow> findNotInGroup(Integer bookId) {
        List<ContactShow> contactShows = new ArrayList<>();
        String sql = "select * from t_contact c " +
                " where c.id in (select cb.contact_id from t_contact_book_relative cb where cb.book_id = :bookId)" +
                " and c.id not in (select cb.contact_id from t_contact_group_relative cg where cg.group_id in (" +
                "select xc.id from t_group  xc where xc.book_id = :bookId)) "
                ;
        Map<String, Object> params = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<Contact> contacts = (List<Contact>) commonRepository.queryResultToBeanList(sql, params, ContactShow.class);
        for(Contact c : contacts) {
            ContactShow show = new ContactShow(c);
            contactShows.add(show);
        }
        return contactShows;
    }
}
