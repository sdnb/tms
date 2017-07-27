package cn.snzo.service;

import cn.snzo.vo.RecordingShow;
import org.springframework.data.domain.Page;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public interface IRecordingService {

    int delete(int rid);

    Page<RecordingShow> getPage(String filename, Date createStart, Date createEnd,
                                Integer currentPage, Integer pageSize, String conductorName);
}
