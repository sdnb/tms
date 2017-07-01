package cn.snzo.repository;

import cn.snzo.entity.ContactGroupRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/1 0001.
 */
@Repository
public interface ContactGroupRelativeRepository extends JpaRepository<ContactGroupRelative, Integer> {

}
