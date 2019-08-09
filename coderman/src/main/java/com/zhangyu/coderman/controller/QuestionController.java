package com.zhangyu.coderman.controller;

import com.zhangyu.coderman.cache.TagsCache;
import com.zhangyu.coderman.dao.CommentMapper;
import com.zhangyu.coderman.dao.NotificationMapper;
import com.zhangyu.coderman.dao.QuestionExtMapper;
import com.zhangyu.coderman.dao.QuestionMapper;
import com.zhangyu.coderman.dto.CommentDTO;
import com.zhangyu.coderman.dto.ResultTypeDTO;
import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.modal.*;
import com.zhangyu.coderman.myenums.CommentNotificationType;
import com.zhangyu.coderman.myenums.CommentStatus;
import com.zhangyu.coderman.myenums.CommentType;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.rmi.runtime.Log;

import javax.management.Query;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    /**
     * 问题详情
     *
     * @param idstr
     * @param map
     * @return
     */
    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") String idstr, Map<String, Object> map) {

        Question question;
        try {
            int i = Integer.parseInt(idstr);
            question = questionService.findQuestionById(i);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        //相关问题
        List<Question> relatedQuestions = questionService.relatedQuestions(question);
        //浏览数增加
        questionService.incViewCount(question);
        //评论信息
        List<CommentDTO> commentDTOS = questionService.findQuestionComments(question.getId());

        map.put("comments", commentDTOS);
        map.put("question", question);
        map.put("relatedQuestions", relatedQuestions);
        return "question";
    }

    /**
     * 删除问题
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/deleteQuestion/{id}")
    public String deleteQuestion(@PathVariable("id") Integer id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.USER_NO_LOGIN);
        }
        Question dbQuestion = questionMapper.selectByPrimaryKey(id);
        if (dbQuestion.getCreator() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_IS_YOURS);
        } else {
            questionMapper.deleteByPrimaryKey(id);
            //删除通知中关联的问题
            NotificationExample example = new NotificationExample();
            NotificationExample.Criteria criteria = example.createCriteria();
            criteria.andOutterIdEqualTo(dbQuestion.getId());
            notificationMapper.deleteByExample(example);
            //删除关联的评论(删除一级评论)
            CommentExample example1 = new CommentExample();
            CommentExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andParentIdEqualTo(dbQuestion.getId());
            criteria1.andTypeEqualTo(CommentType.COMMENT_ONE.getVal());
            commentMapper.deleteByExample(example1);
        }
        return "redirect:/profile/questions";
    }

    //点赞问题
    @ResponseBody
    @GetMapping("/likeQuestion")
    public ResultTypeDTO likeQuestion(@RequestParam("id") Integer id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
          return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }
        Question dbQuestion = questionMapper.selectByPrimaryKey(id);
        if (dbQuestion == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        } else if (user.getId() != dbQuestion.getCreator()) {
            questionExtMapper.incLikeCount(id);
            //通知
            Notification notification = new Notification();
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setType(CommentNotificationType.LIKE_QUESTION.getCode());
            notification.setOutterId(id);
            notification.setNotifier((long) user.getId());
            notification.setReceiver((long) dbQuestion.getCreator());
            notification.setStatus(CommentStatus.UN_READ.getCode());
            notificationMapper.insertSelective(notification);
        }else {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.CANT_LIKE_YOURSELF_QUESTION);
        }
        return new ResultTypeDTO().okOf().addMsg("likequestioncount",dbQuestion.getLikeCount()+1);
    }

}
