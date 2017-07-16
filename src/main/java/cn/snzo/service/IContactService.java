package cn.snzo.service;

import cn.snzo.vo.ContactShow;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public interface IContactService {


    /**
     * 分页查联系人
     * @param name
     * @param phone
     * @param sysBookId
     *@param currentPage
     * @param pageSize   @return
     */
    Page<ContactShow> getPage(Integer groupId, Integer bookId,
                              String name, String phone, Integer bookType, Boolean sysBookId,
                              Integer currentPage, Integer pageSize);


    /**
     * 新增联系人
     * @param contactShow
     * @return
     */
    int add(ContactShow contactShow);


    /**
     * 删除联系人
     * @param cid 联系人id
     * @return
     */
    int delete(int cid);



    /**
     * 修改联系人
     * @param id
     * @param contactShow
     * @return
     */
    int modify(int id, ContactShow contactShow);


//    /**
//     * 根据分组查联系人
//     * @param groupId
//     * @return
//     */
//    List<ContactShow> findByGroup(int groupId);


    /**
     * 查询不在分组内的联系人
     * @param bookId
     * @return
     */
    List<ContactShow> findNotInGroup(int bookId);


    /**
     * 通过excel文件导入联系人
     * @param file
     * @return
     */
    int importFromExcel(MultipartFile file, int bookId) throws IOException;

    Page<ContactShow> findContactByCurrUser(int uid, Integer currentPage, Integer pageSize);
}
