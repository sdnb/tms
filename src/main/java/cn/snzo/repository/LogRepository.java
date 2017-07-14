package cn.snzo.repository;

import cn.snzo.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by chentao on 2017/7/14 0014.
 */
@Repository
public interface LogRepository  extends JpaRepository<Log, Integer>{
}
