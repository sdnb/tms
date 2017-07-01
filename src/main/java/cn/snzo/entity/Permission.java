package cn.snzo.entity;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
//@Entity
//@Table(name = "t_permission")
public class Permission {

    private String   url;              //资源路径
    private String   name;             //资源名称
    private Integer  parentId;         //父级id 如果无父级则为0
    private Integer  type;             //类型 1 菜单 2 api

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
