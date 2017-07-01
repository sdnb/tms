package cn.snzo.entity;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
//@Entity
//@Table(name = "t_role_permission")
public class RolePermission {
    private Integer roleId;             //角色id
    private Integer permissionId;       //权限id

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}
