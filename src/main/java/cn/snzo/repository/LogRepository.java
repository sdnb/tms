package cn.snzo.repository;

import cn.snzo.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by chentao on 2017/7/14 0014.
 */
@Repository
public interface LogRepository  extends JpaRepository<Log, Integer>{

    @Query("select l from Log l where " +
            " (?1 is null or l.operResType = ?1) and " +
            " (?2 is null or l.operator like ?2) and " +
            " (?3 is null or l.createDate >= ?3) and" +
            " (?4 is null or l.createDate <= ?4) and" +
            " (?5 is null or l.operMethodName like ?5) ")
    Page<Log> findPage(Integer operResType, String operator, Date createStart,
                       Date createEnd, String operMethodName, Pageable p);
}
