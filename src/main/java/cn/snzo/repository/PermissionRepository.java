package cn.snzo.repository;

import cn.snzo.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/8/2 0002.
 */
public interface PermissionRepository extends JpaRepository<Permission, Integer> {


    @Query("select  p from Permission p, RolePermission rp  where" +
            " p.id = rp.permissionId " +
            " and (?1 is null or rp.roleId = ?1)" +
            " and (?2 is null or p.type = ?2)" +
            " and (?3 is null or p.parentId = ?3)")
    List<Permission> findByKeys(Integer role, Integer type, Integer parentId);
}
