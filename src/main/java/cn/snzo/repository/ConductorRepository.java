package cn.snzo.repository;

import cn.snzo.entity.Conductor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Integer> {
    Conductor findByPhone(String newPhone);


    @Query("select c from Conductor c where (?1 is null or c.realname like ?1)" +
            " and (?2 is null or c.phone like ?2)")
    Page<Conductor> findPage(String name, String phone, Pageable pageable);
}
