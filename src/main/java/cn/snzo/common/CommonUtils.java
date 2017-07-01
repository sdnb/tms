package cn.snzo.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/6/29 0029.
 */
public class CommonUtils {
    public static void setResponseHeaders(Long totalElements, Integer totalPages, Integer pageNo, HttpServletResponse response){
        response.addHeader("page_total",totalElements+"");
        response.addHeader("page_count",totalPages+"");
        response.addHeader("page_no",(pageNo+1)+"");
    }


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

    public static String fuzzyString(String field){
        field = field == null ? null : "%" + field + "%";
        return field;
    }

    public static Cookie buildCookie(String key, String value, int maxAge, String path){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        return cookie;
    }
}
