package cn.snzo.service;

import cn.snzo.vo.ContactGroupRelativeShow;
import cn.snzo.vo.GroupShow;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public interface IGroupService {

    /**
     * 新增分组
     * @param groupShow
     * @return
     */
    int add(GroupShow groupShow);

    /**
     * 删除分组
     * @param id
     * @return 1 成功  2
     */
    int delete(int id);


    /**
     * 分页查询
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<GroupShow> getPage(String name, Integer currentPage, Integer pageSize);

    /**
     * 查询所有分组
     * @return
     */
    List<GroupShow> getTotal();


    /**
     * 新增联系人
     * @param contactGroupRelativeShow
     * @return
     */
    int addContact(ContactGroupRelativeShow contactGroupRelativeShow);

    /**
     * 移出分组
     * @param groupId
     * @param contactId
     * @return
     */
    int removeContact(int groupId, int contactId);
}
