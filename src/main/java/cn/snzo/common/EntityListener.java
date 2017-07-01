package cn.snzo.common;

import cn.snzo.entity.BaseEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * Created by ThomasC on 2017/6/29 0020.
 */
public class EntityListener {

    @PrePersist
    public void prePersist(BaseEntity baseEntity){
        baseEntity.setCreateDate(new Date());
        baseEntity.setModifyDate(new Date());
    }


    @PreUpdate
    public void preUpdate(BaseEntity baseEntity){
        baseEntity.setModifyDate(new Date());
    }
}
