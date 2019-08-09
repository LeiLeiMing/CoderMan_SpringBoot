package com.zhangyu.coderman.controller;

import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.dao.UserMapper;
import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class PeopleController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;


    @GetMapping("/people/{id}")
    public String people(@PathVariable("id") String id, Map<String,Object> map,
                         @RequestParam(name = "pageSize",defaultValue = "12") Integer pageSize,
                         @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,HttpServletRequest request
                         ){
        User loginUser = (User) request.getSession().getAttribute("user");
        Integer i;
        try {
            i = Integer.parseInt(id);
        } catch (NumberFormatException e) {
           throw  new CustomizeException(CustomizeErrorCode.PEOPLE_DOT_HAVE);
        }
        if(loginUser!=null&&loginUser.getId().toString().equals(id)){
         return "redirect:/profile/questions";
        }
        //他的问题
        PageInfo<Question> myquestionPageInfo=null;
        User user = userMapper.selectByPrimaryKey(i);
        if(user!=null&&user.getId()!=null){
            myquestionPageInfo=questionService.findQuestionsByUserId(pageNo,pageSize,user.getId());
        }else {
            throw new CustomizeException(CustomizeErrorCode.PEOPLE_DOT_HAVE);
        }
        map.put("people",user);
        map.put("page",myquestionPageInfo);
        return "people";
    }


}
