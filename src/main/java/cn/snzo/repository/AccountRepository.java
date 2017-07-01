package cn.snzo.repository;

import cn.snzo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/6/30 0030.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
    Account findByUsername(String username);
}
