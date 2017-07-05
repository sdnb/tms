package cn.snzo.utils;

import cn.snzo.common.Constants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

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

}
