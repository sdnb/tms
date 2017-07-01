package cn.snzo.service.impl;

import cn.snzo.entity.Group;
import cn.snzo.repository.GroupRepository;
import cn.snzo.service.IGroupService;
import cn.snzo.vo.GroupShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Service
public class GroupService implements IGroupService {


    @Autowired
    private GroupRepository groupRepository;

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
    public int delete(int id) {
        groupRepository.delete(id);

        return 0;
    }

    @Override
    public Page<GroupShow> getPage(Integer currentPage, Integer pageSize) {
        return null;
    }

    @Override
    public int addContact(int groupId, int contactId) {
        return 0;
    }

    @Override
    public int removeContact(int groupId, int contactId) {
        return 0;
    }
}
