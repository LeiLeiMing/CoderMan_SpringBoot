package com.zhangyu.coderman.service;

import com.zhangyu.coderman.modal.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void save(User user);

    User findUserByToken(String token);

    void SaveOrUpdate(User user);

}
