package cn.snzo.vo;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3 0003.
 */
public class ContactGroupRelativeShow {

    @NotNull(message = "组id不能为空")
    private Integer groupId;

    @NotEmpty(message = "请选择联系人")
    private List<Integer> contactIds;  //支持一次添加多个联系人

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<Integer> getContactIds() {
        return contactIds;
    }

    public void setContactIds(List<Integer> contactIds) {
        this.contactIds = contactIds;
    }
}
