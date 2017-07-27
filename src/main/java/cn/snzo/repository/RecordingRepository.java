package cn.snzo.repository;

import cn.snzo.entity.Recording;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@Repository
public interface RecordingRepository extends JpaRepository<Recording,Integer>{


    @Query("select r from Recording r where " +
            " (?1 is null or r.filename like ?1) and " +
            " (?2 is null or r.createDate >= ?2) and " +
            " (?3 is null or r.createDate <= ?3) and " +
            " (?4 is null or r.conductorName <= ?4)")
    Page<Recording> findPage(String filename, Date createStart, Date createEnd, String conductorName, Pageable p);

}
