package cn.snzo.service.impl;

import cn.snzo.common.CommonUtils;
import cn.snzo.entity.ContactGroupRelative;
import cn.snzo.entity.Group;
import cn.snzo.repository.ContactGroupRelativeRepository;
import cn.snzo.repository.GroupRepository;
import cn.snzo.service.IGroupService;
import cn.snzo.vo.ContactGroupRelativeShow;
import cn.snzo.vo.GroupShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Service
public class GroupService implements IGroupService {


    @Autowired
    private GroupRepository groupRepository;


    @Autowired
    private ContactGroupRelativeRepository contactGroupRelativeRepository;

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
    public Page<GroupShow> getPage( String name, Integer currentPage, Integer pageSize) {
        name = CommonUtils.fuzzyString(name);
        Pageable p = CommonUtils.createPage(currentPage, pageSize);
        Page<Group> groups = groupRepository.findPage(name, p);
        List<Group> groupsList = groups.getContent();
        List<GroupShow> groupsShowList = new ArrayList<>();
        for (Group group : groupsList) {
            GroupShow groupShow = new GroupShow(group);
            groupsShowList.add(groupShow);
        }
        return new PageImpl<GroupShow>(groupsShowList, p, groups.getTotalElements());
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
