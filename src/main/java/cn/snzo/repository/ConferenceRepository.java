package cn.snzo.repository;

import cn.snzo.entity.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by chentao on 2017/7/11 0011.
 */
@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Integer> {
    Conference findByResId(String resId);
}
