package cn.snzo.service;

import cn.snzo.vo.GroupShow;
import org.springframework.data.domain.Page;

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
     * @return
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
     * 新增联系人
     * @param groupId
     * @param contactId
     * @return
     */
    int addContact(int groupId, int contactId);


    /**
     * 移出分组
     * @param groupId
     * @param contactId
     * @return
     */
    int removeContact(int groupId, int contactId);
}
