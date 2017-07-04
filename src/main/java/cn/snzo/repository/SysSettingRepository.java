package cn.snzo.repository;

import cn.snzo.entity.SysSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@Repository
public interface SysSettingRepository extends JpaRepository<SysSetting,Integer>{
}
