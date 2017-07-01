package cn.snzo.entity;

import cn.snzo.common.EntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ThomasC on 2017/6/29 0029.
 */
@MappedSuperclass
@EntityListeners(EntityListener.class)
public class BaseEntity {

    private Integer id;
    private Date    createDate;
    private Date    modifyDate;


    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}
