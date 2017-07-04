package cn.snzo.service.impl;

import cn.snzo.common.CommonRepository;
import cn.snzo.common.CommonUtils;
import cn.snzo.entity.Contact;
import cn.snzo.entity.ContactGroupRelative;
import cn.snzo.exception.ServiceException;
import cn.snzo.repository.ContactGroupRelativeRepository;
import cn.snzo.repository.ContactRepository;
import cn.snzo.service.IContactService;
import cn.snzo.utils.ExcelUtils;
import cn.snzo.vo.ContactShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    public Page<ContactShow> getPage(Integer groupId, Integer bookId, String name, String phone, Integer currentPage, Integer pageSize) {
        Pageable p = CommonUtils.createPage(currentPage, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("name", CommonUtils.fuzzyString(name));
        params.put("phone", CommonUtils.fuzzyString(phone));
        params.put("groupId", groupId);
        params.put("bookId", bookId);
        String pageSql = "select c.id,c.phone,c.name,cg.group_id as group_id," +
                " g.name as group_name,c.book_id " +
                " from t_contact c " +
                " left join t_contact_group_relative cg on" +
                " c.id = cg.contact_id " +
                " left join t_group g on cg.group_id = g.id" +
                " where (:groupId is null or cg.group_id = :groupId) and" +
                " (:bookId is null or c.book_id = :bookId) and" +
                " (:name is null or c.name like :name) and" +
                " (:phone is null or c.phone like :phone)";

        String countSql = "select count(*) " +
                " from t_contact c " +
                " left join t_contact_group_relative cg on" +
                " c.id = cg.contact_id " +
                " left join t_group g on cg.group_id = g.id" +
                " where (:groupId is null or cg.group_id = :groupId) and" +
                " (:bookId is null or c.book_id = :bookId) and" +
                " (:name is null or c.name like :name) and" +
                " (:phone is null or c.phone like :phone)";

        return commonRepository.queryPage(pageSql, countSql, params, ContactShow.class, p);
    }



    @Override
    @Transactional
    public int add(ContactShow contactShow) {
        String  phone  = contactShow.getPhone();
        Integer bookId = contactShow.getBookId();
        List<Contact> checkContacts = contactRepository.checkPhone(phone, bookId);
        //电话重复
        if (!checkContacts.isEmpty()) {
            return 2;
        }

        Contact c = new Contact(contactShow);
        c = contactRepository.save(c);

        //将联系人加入分组
        Integer groupId = contactShow.getGroupId();
        if (groupId != null) {
            ContactGroupRelative cgr = new ContactGroupRelative();
            cgr.setGroupId(groupId);
            cgr.setContactId(c.getId());
            contactGroupRelativeRepository.save(cgr);
        }

        return 1;
    }

    @Override
    @Transactional
    public int delete(int cid) {
        contactRepository.delete(cid);
        contactGroupRelativeRepository.deleteByContactId(cid);
        return 1;
    }


    @Override
    public int modify(int id, ContactShow contactShow) {
        Contact checkContact = contactRepository.findOne(id);
        if (checkContact == null) {
            return 2;
        }

        String phone = contactShow.getPhone();

        //如果修改电话，需要检验电话是否已经存在
        if (!phone.equals(checkContact.getPhone()) ) {
            Contact checkPhone = contactRepository.findByPhone(phone);
            if (checkPhone != null && checkPhone.getId() != id) {
                return 3;
            }
        }
        Contact contact = new Contact(contactShow);
        contact.setId(id);
        contactRepository.save(contact);
        return 1;
    }

//    @Override
//    public List<ContactShow> findByGroup(Integer groupId) {
//
//        List<Contact> contacts = new ArrayList<>();
//
//        List<Integer> contactIds = contactGroupRelativeRepository.findContactIds(groupId);
//
//        if (contactIds != null && !contactIds.isEmpty()) {
//            contacts = contactRepository.findByIds(contactIds);
//        }
//
//        List<ContactShow> contactShows = new ArrayList<>();
//        for (Contact c : contacts) {
//            ContactShow s = new ContactShow(c);
//            contactShows.add(s);
//        }
//        return contactShows;
//    }

    @Override
    public List<ContactShow> findNotInGroup(int bookId) {
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

    @Override
    public int importFromExcel(MultipartFile file, int bookId) throws IOException {
        List<List<Object>> objs = ExcelUtils.readExcel(file);
        List<Contact> contacts = new ArrayList<>();
        objs.remove(0);//去掉表头
        for (List list : objs) {
            Contact contact = new Contact();
            for (Object o : list) {
                contact.setName((String)o);
                contact.setPhone(String.valueOf(o));
            }
            contact.setBookId(bookId);
            contacts.add(contact);
        }
        List<String> phones = contacts.stream().map(e -> e.getPhone()).collect(Collectors.toList());
        List<String> phonesExists = contactRepository.checkPhones(phones, bookId);
        if (!phonesExists.isEmpty()) {
            List<Contact> notExists = contacts.stream().filter(e -> !phonesExists.contains(e.getPhone())).collect(Collectors.toList());
            contactRepository.save(notExists);
            throw new ServiceException("手机号"+phonesExists+"已存在,其他号码已新增成功");
        } else {
            contactRepository.save(contacts);
            return 1;
        }
    }
}
