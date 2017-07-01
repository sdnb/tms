package cn.snzo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Entity
@Table(name = "t_group")
public class Group extends BaseEntity{
    private String name;     //名称
    private Integer bookId;  //所在电话簿

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
}
