package com.zhangyu.coderman.service.impl;

import com.zhangyu.coderman.dao.UserMapper;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.modal.UserExample;
import com.zhangyu.coderman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public void save(User user) {
        userMapper.insert(user);
    }

    @Override
    public User findUserByToken(String token) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(userExample);
        User user=null;
        if(users.size()>0){
            user=users.get(0);
        }
        return user;
    }

    @Override
    public void SaveOrUpdate(User user) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        User dbUser=null;
        if(users.size()>0){
          dbUser =users.get(0);
        }
        if(dbUser==null){
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(System.currentTimeMillis());
            //userMapper.save(user);
            userMapper.insert(user);
        }else {
            //更新用户(修改时间)
            user.setGmtCreate(dbUser.getGmtCreate());
            user.setGmtModified(System.currentTimeMillis());
            //userMapper.UpdateUser(user);
            user.setId(dbUser.getId());
           userMapper.updateByPrimaryKey(user);
        }
    }
}
