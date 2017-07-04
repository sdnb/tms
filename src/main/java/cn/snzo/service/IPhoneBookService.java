package cn.snzo.service;

import cn.snzo.entity.PhoneBook;

import java.util.List;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public interface IPhoneBookService {
    List<PhoneBook> findAllByKeys(Integer type, Integer roomId);
}
