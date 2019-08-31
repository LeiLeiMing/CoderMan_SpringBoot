package com.zhangyu.coderman.dao;
import com.zhangyu.coderman.modal.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@Mapper
public interface UserExtMapper {
    @Select(value = "select * from user order by gmt_create desc limit 0,#{top}")
    List<User> findNewUserList(Integer top);

    @Select(value = " select count(*) from follow where followed_user=#{id}")
    Integer getFansCount(@Param("id") Integer id);

    @Update("update notification set status=0 where receiver=#{id} ")
    void readAllNotification(@Param("id") Integer id);
}
