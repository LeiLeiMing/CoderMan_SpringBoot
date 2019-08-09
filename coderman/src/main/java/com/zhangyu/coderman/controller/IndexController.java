
package com.zhangyu.coderman.controller;

import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.dao.NotificationMapper;
import com.zhangyu.coderman.modal.NotificationExample;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.myenums.CommentStatus;
import com.zhangyu.coderman.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private QuestionService questionService;
    //首页
    @GetMapping("/")
    public String index(HttpServletRequest request,
                        @RequestParam(name = "search",required = false) String search,
                        @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                        @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                        Map<String,Object> map){
        User user = (User) request.getSession().getAttribute("user");
        if(user!=null){
            //未读信息数
            NotificationExample notificationExample = new NotificationExample();
            NotificationExample.Criteria criteria = notificationExample.createCriteria();
            criteria.andReceiverEqualTo((long)user.getId());
            criteria.andStatusEqualTo(CommentStatus.UN_READ.getCode());
            Integer unreadcount= notificationMapper.countByExample(notificationExample);
            request.getSession().setAttribute("unreadcount",unreadcount);
        }

        //获取首页列表数据
        PageInfo<Question> questionPageInfo=questionService.getPageBySearch(search,pageNo,pageSize);
        map.put("search", search);
        map.put("page",questionPageInfo);
        map.put("navLi","find");
        return "index";
    }

}
