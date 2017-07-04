package cn.snzo.service.impl;

import cn.snzo.entity.PhoneBook;
import cn.snzo.repository.PhoneBookRepository;
import cn.snzo.service.IPhoneBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@Service
public class PhoneBookService implements IPhoneBookService {


    @Autowired
    private PhoneBookRepository phoneBookRepository;

    @Override
    public List<PhoneBook> findAllByKeys(Integer type, Integer roomId) {
        List<PhoneBook> phoneBooks = phoneBookRepository.findByTypeAndRoomId(type, roomId);
        return phoneBooks;
    }
}
