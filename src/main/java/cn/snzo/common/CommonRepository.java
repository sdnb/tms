package cn.snzo.common;


import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chentao on 2017/2/25 0025.
 */
@Transactional
@Repository
public class CommonRepository {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * * 查询数据集合
     * @param sql 查询sql sql中的参数用:name格式
     * @param params 查询参数map格式，key对应参数中的:name
     * @param clazz 实体类型为空则直接转换为map格式
     * @return
     */
    public List<?> queryResultToBeanList(String sql,Map<String, Object> params, Class<?> clazz){
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(sql);
        if (params != null) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));

            }
        }
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> result =  query.list();

        if (clazz != null && !result.isEmpty()) {
            return convert(clazz, result);
        }
        return result;
    }


    /**
     * * 查询数据集合(分页）
     * @param sql 查询sql sql中的参数用:name格式
     * @param params 查询参数map格式，key对应参数中的:name
     * @param clazz 实体类型为空则直接转换为map格式
     * @param pageNo 当前页数
     * @param pageSize 每页记录数
     * @return
     */
    public List<?> queryResultToBeanPage(String sql,Map<String, Object> params, Class<?> clazz, Integer pageNo,
                                         Integer pageSize){
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(sql);
        System.out.println(sql);
        pageNo = (pageNo == null || pageNo < 1) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        if (params != null) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }

        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
             .setFirstResult((pageNo-1) * pageSize)
             .setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> result =  query.list();

        if (clazz != null && !result.isEmpty()) {
            return convert(clazz, result);
        }
        return result;
    }



    public <T> Page<T> queryPage(String querySql, String countSql,
                                 Map<String, Object> params,
                                 Class<T> tClass, Pageable pageable) {

        @SuppressWarnings("unchecked")
        List<T> ts =(List<T>) queryResultToBeanPage(querySql, params, tClass, pageable.getPageNumber(), pageable.getPageSize());

        Integer count = getCountBy(countSql,params);

        return new PageImpl<T>(ts, pageable, count);
    }

    private List<Object> convert(Class<?> clazz, List<Map<String, Object>> list) {
        List<Object> result;
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        result = new ArrayList<Object>();
        try {
            PropertyDescriptor[] props = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            for (Map<String, Object> map : list) {
                Object obj = clazz.newInstance();
                for (String key:map.keySet()) {
                    for (PropertyDescriptor prop : props) {
                        try {
                            String  attrName = removeUnderLine(key.toLowerCase());

                            if (!attrName.equals(prop.getName())) {
                                continue;
                            }
                            Method method = prop.getWriteMethod();
                            Object value = map.get(key);
                            if (value != null) {
//                            value = ConvertUtils.convert(value,prop.getPropertyType());
                            }
                            method.invoke(obj,value);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                result.add(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("数据转换错误");
        }
        return result;
    }

     //去掉数据库字段的下划线
    private String removeUnderLine(String attrName) {

        if(attrName.contains("_")) {
            String[] names = attrName.split("_");
            String firstPart = names[0];
            String otherPart = "";
            for (int i = 1; i < names.length; i++) {
                String word = names[i].replaceFirst(names[i].substring(0, 1), names[i].substring(0, 1).toUpperCase());
                otherPart += word;
            }
            attrName = firstPart + otherPart;
        }
        return attrName;
    }

    /**
     * 获取记录条数
     * @param sql
     * @param params
     * @return
     */
    public Integer getCountBy(String sql,Map<String, Object> params){
        Query query =  entityManager.createNativeQuery(sql);
        if (params != null) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        BigInteger bigInteger  = (BigInteger) query.getSingleResult();
        return bigInteger.intValue();
    }

    /**
     * 新增或者删除
     * @param sql
     * @param params
     * @return
     */
    public Integer deleteOrUpDate(String sql,Map<String, Object> params){
        Query query =  entityManager.createNativeQuery(sql);
        if (params != null) {
            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        return query.executeUpdate();
    }
}
