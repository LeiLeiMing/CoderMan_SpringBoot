
package com.zhangyu.coderman.controller;

import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.dao.*;
import com.zhangyu.coderman.dto.NotificationDTO;
import com.zhangyu.coderman.dto.ResultTypeDTO;
import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.modal.*;
import com.zhangyu.coderman.myenums.CommentNotificationType;
import com.zhangyu.coderman.myenums.CommentStatus;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.myenums.FollowStatus;
import com.zhangyu.coderman.service.NotificationService;
import com.zhangyu.coderman.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable("action") String action, Map<String, Object> map, HttpServletRequest request,
                          @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        if ("questions".equals(action)) {
            PageInfo<Question> myquestionPageInfo = questionService.findQuestionsByUserId(pageNo, pageSize, user.getId());
            //获取通知信息
            map.put("page", myquestionPageInfo);
            map.put("siledername", "我的问题");
            map.put("silderbar", action);
        }
        if ("replies".equals(action)) {
            //获取通知信息
            List<NotificationDTO> notificationDTOPageInfo = notificationService.list(pageNo, pageSize, user.getId());
            map.put("notification", notificationDTOPageInfo);
            map.put("siledername", "我的通知");
            map.put("silderbar", action);
        }
        if("follows".equals(action)){
            List<User> userList = new ArrayList<>();
            //我关注的人
            FollowExample example = new FollowExample();
            FollowExample.Criteria c = example.createCriteria();
            c.andStatusEqualTo(FollowStatus.FOLLOWED.getVal());
            c.andUserIdEqualTo(user.getId());
            List<Follow> follows = followMapper.selectByExample(example);
            if (follows.size() > 0) {
                userList = new ArrayList<>();
                for (Follow follow : follows) {
                    Integer followedUser = follow.getFollowedUser();
                    User fuser = userMapper.selectByPrimaryKey(followedUser);
                    userList.add(fuser);
                }
            }
            map.put("follows",userList);
            map.put("siledername", "我的关注");
            map.put("silderbar", action);

        }
        if (!"follows".equals(action)&& !"questions".equals(action) && !"replies".equals(action)) {
            throw new CustomizeException(CustomizeErrorCode.PAGE_NOT_FOUNT);
        }
        //map.put("unreadcount", unreadcount);
        return "profile";
    }

    /**
     * 查看最新通知
     * @return
     */
    @GetMapping("/read")
    public String getNotificationDetail(@RequestParam("id") Integer id,
                                        @RequestParam("status") Integer isread,
                                        HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        //修改通知为已读
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (isread != CommentStatus.READ.getCode()) {
            notification.setStatus(CommentStatus.READ.getCode());
            notificationMapper.updateByPrimaryKeySelective(notification);
            //未读信息数
            NotificationExample notificationExample = new NotificationExample();
            NotificationExample.Criteria criteria = notificationExample.createCriteria();
            criteria.andReceiverEqualTo((long) user.getId());
            criteria.andStatusEqualTo(CommentStatus.UN_READ.getCode());
            Integer unreadcount = notificationMapper.countByExample(notificationExample);
            request.getSession().setAttribute("unreadcount", unreadcount);
        }

        //找到关联问题的页面
        Integer type = notification.getType();

        if (type == CommentNotificationType.COMMENT_QUESTION.getCode()||type==CommentNotificationType.LIKE_QUESTION.getCode()) {
            if (notification.getOutterId() == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            return "redirect:/question/" + notification.getOutterId();
        } else if (type == CommentNotificationType.COMMENT_REPLY.getCode()||type==CommentNotificationType.COMMENT_Like.getCode()) {
            Comment comment = commentMapper.selectByPrimaryKey(notification.getOutterId());
            if (comment == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            Integer questionId = comment.getParentId();
            return "redirect:/question/" + questionId;
        }
        return "error";
    }

    //删除通知
    @GetMapping("/deletenotification")
    public String deletenotification(@RequestParam("id") Integer id) {
        notificationMapper.deleteByPrimaryKey(id);
        return "redirect:/profile/replies";
    }

    //删除已读
    /**
    @GetMapping("/deleteReaded")
    public String delelteReaded(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            NotificationExample notificationExample = new NotificationExample();
            NotificationExample.Criteria criteria = notificationExample.createCriteria();
            criteria.andReceiverEqualTo((long) user.getId());
            criteria.andStatusEqualTo(CommentStatus.READ.getCode());
            notificationMapper.deleteByExample(notificationExample);
        }
        return "redirect:/profile/replies";
    }
    **/
    //删除已读的通知
    @ResponseBody
    @GetMapping("/AjaxDeleteRead")
    public ResultTypeDTO AjaxDeleteRead(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            NotificationExample notificationExample = new NotificationExample();
            NotificationExample.Criteria criteria = notificationExample.createCriteria();
            criteria.andReceiverEqualTo((long) user.getId());
            criteria.andStatusEqualTo(CommentStatus.READ.getCode());
            notificationMapper.deleteByExample(notificationExample);
        }
        return new ResultTypeDTO().okOf();
    }
    @Autowired
    private UserExtMapper userExtMapper;
    /**
     * 全部已读
     * @return
     */
    @ResponseBody
    @GetMapping("/readAll")
    public ResultTypeDTO readAll(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user!=null){
            try {
                userExtMapper.readAllNotification(user.getId());
                return new ResultTypeDTO().okOf();
            } catch (Exception e) {
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.READ_ALL_FAIL);
            }
        }
        return new ResultTypeDTO().errorOf(CustomizeErrorCode.READ_ALL_FAIL);
    }

}
