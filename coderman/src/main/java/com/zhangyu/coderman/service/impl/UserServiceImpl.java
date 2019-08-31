package com.zhangyu.coderman.service.impl;

import com.zhangyu.coderman.dao.*;
import com.zhangyu.coderman.dto.NewUserDTO;
import com.zhangyu.coderman.modal.QuestionExample;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.modal.UserExample;
import com.zhangyu.coderman.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    @Autowired
    private QuestionMapper questionMapper;

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

    @Override
    public List<NewUserDTO> findNewsUsers(Integer top) {
        List<User> userList=userExtMapper.findNewUserList(top);
        List<NewUserDTO> userDTOS = new ArrayList<>();

        if(userList!=null&&userList.size()>0){
            for (User user : userList) {
                if(user.getName()==null||user.getName()==""){
                    user.setName("无名氏");
                }
            }
            for (User user : userList) {
                NewUserDTO newUserDTO = new NewUserDTO();
                newUserDTO.setFansCount(userExtMapper.getFansCount(user.getId()));
                BeanUtils.copyProperties(user,newUserDTO);
                QuestionExample example = new QuestionExample();
                QuestionExample.Criteria criteria = example.createCriteria();
                criteria.andCreatorEqualTo(user.getId());
                newUserDTO.setQuestionCount(questionMapper.countByExample(example));
                userDTOS.add(newUserDTO);
            }
        }
        return userDTOS;
    }
}
