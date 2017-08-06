package cn.snzo.service.impl;

import cn.snzo.common.CommonRepository;
import cn.snzo.entity.ContactGroupRelative;
import cn.snzo.entity.Group;
import cn.snzo.repository.ContactGroupRelativeRepository;
import cn.snzo.repository.GroupRepository;
import cn.snzo.service.IGroupService;
import cn.snzo.utils.CommonUtils;
import cn.snzo.vo.ContactGroupRelativeShow;
import cn.snzo.vo.GroupShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Service
public class GroupService implements IGroupService {


    @Autowired
    private GroupRepository groupRepository;


    @Autowired
    private ContactGroupRelativeRepository contactGroupRelativeRepository;


    @Autowired
    private CommonRepository commonRepository;
    @Override
    public int add(GroupShow groupShow) {
        Group groupCheck = groupRepository.findByNameAndBookId(groupShow.getName(), groupShow.getBookId());
        //检查组名是否重复
        if (groupCheck != null) {
            return 2;
        }
        Group group = new Group();
        group.setBookId(groupShow.getBookId());
        group.setName(groupShow.getName());

        groupRepository.save(group);
        return 1;
    }



    @Override
    @Transactional
    public int delete(int id) {
        groupRepository.delete(id);
        contactGroupRelativeRepository.deleteByGroupId(id);
        return 1;
    }

    @Override
    public Page<GroupShow> getPage( String name,Integer conductorId, Integer bookId, Integer currentPage, Integer pageSize) {
        name = CommonUtils.fuzzyString(name);
        Pageable p = CommonUtils.createPage(currentPage, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("conductorId", conductorId);
        params.put("bookId", bookId);
        String querySql = "select xc.* from t_group xc " +
                " inner join t_phone_book pb on pb.id = xc.book_id " +
                " left join t_conference_room cr on cr.id = pb.room_id" +
                " left join t_conductor cd on cd.id = cr.conductor_id" +
                " where (:name is null or xc.name like :name)" +
                " and (:conductorId is null or cd.id = :conductorId)" +
                " and (:bookId is null or xc.book_id = :bookId)" ;
        String countSql = "select count(*) from t_group xc " +
                " inner join t_phone_book pb on pb.id = xc.book_id " +
                " left join t_conference_room cr on cr.id = pb.room_id" +
                " left join t_conductor cd on cd.id = cr.conductor_id" +
                " where (:name is null or xc.name like :name)" +
                " and (:conductorId is null or cd.id = :conductorId)" +
                " and (:bookId is null or xc.book_id = :bookId)" ;
        @SuppressWarnings("unchecked")
        List<Group> groups = (List<Group>) commonRepository.queryResultToBeanList(querySql, params, Group.class);

        List<GroupShow> groupsShowList = new ArrayList<>();
        for (Group group : groups) {
            GroupShow groupShow = new GroupShow(group);
            groupsShowList.add(groupShow);
        }
        int count = commonRepository.getCountBy(countSql, params);
        return new PageImpl<GroupShow>(groupsShowList, p, count);
    }

    @Override
    public List<GroupShow> getTotal() {
        List<Group> groupsList = groupRepository.findTotal();
        List<GroupShow> groupsShowList = new ArrayList<>();
        for(Group group : groupsList){
            GroupShow groupShow = new GroupShow(group);
            groupsShowList.add(groupShow);
        }
        return groupsShowList;
    }

    @Override
    public int addContact(ContactGroupRelativeShow contactGroupRelativeShow) {
        List<Integer> contactIds = contactGroupRelativeShow.getContactIds();
        for (Integer id : contactIds) {
            ContactGroupRelative contactGroupRelative = new ContactGroupRelative();
            contactGroupRelative.setContactId(id);
            contactGroupRelative.setGroupId(contactGroupRelativeShow.getGroupId());
            contactGroupRelativeRepository.save(contactGroupRelative);
        }
        return 1;
    }

    @Override
    public int removeContact(int groupId, int contactId) {
        int ret = contactGroupRelativeRepository.deleteContact(groupId, contactId);
        return ret;
    }
}
