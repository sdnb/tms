package cn.snzo.vo;

import cn.snzo.entity.Group;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
public class GroupShow {
    private Integer id;

    @NotBlank(message = "组名不能为空")
    private String  name;     //名称


    private Integer bookId;  //所在电话簿

    private String confRoomNo;  //会议室编号
    public GroupShow() {
    }

    public GroupShow(Group group) {
        Assert.notNull(group);
        BeanUtils.copyProperties(group, this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getConfRoomNo() {
        return confRoomNo;
    }

    public void setConfRoomNo(String confRoomNo) {
        this.confRoomNo = confRoomNo;
    }
}
