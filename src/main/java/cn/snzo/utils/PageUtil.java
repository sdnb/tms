package cn.snzo.utils;

import cn.snzo.common.Constants;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public class PageUtil {

    public static Pageable createPage(Integer currentPage, Integer pageSize, String sortProperty, Boolean isAsc){
        currentPage = (currentPage == null || currentPage < 1 ) ? Constants.DEFAULT_PAGE_NO : currentPage;
        pageSize = (pageSize == null || pageSize < 1 ) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        String sortProp = "id";
        if(!StringUtils.isEmpty(sortProperty))
            sortProp = sortProperty;
        Sort.Direction direction = Sort.Direction.DESC;
        if(isAsc != null && isAsc )
            direction = Sort.Direction.ASC;
        return new PageRequest(currentPage - 1, pageSize, new Sort(direction, sortProp));
    }

    public static Pageable createPage(Integer currentPage, Integer pageSize){
        return createPage(currentPage, pageSize, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <E, S> Page<S> entityToShowPage(Page<E> e, Pageable pageable) {
        List<E> es = e.getContent();
        List<S> ss = new ArrayList<>();
        for (E one : e) {
            S s = (S) new Object();
            BeanUtil.entityToShow(e, (S)s);
            ss.add(s);
        }
        return new PageImpl<S>(ss, pageable, e.getTotalElements());
    }
}
