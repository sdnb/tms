package cn.snzo.repository;

import cn.snzo.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    Group findByName(String name);

    Group findByNameAndBookId(String name, Integer bookId);


    @Query("select xc from Group xc where (?1 is null or xc.name like ?1)")
    Page<Group> findPage(String name, Pageable p);
}
