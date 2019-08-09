package com.zhangyu.coderman.controller;

import com.zhangyu.coderman.dao.CommentExtMapper;
import com.zhangyu.coderman.dao.CommentMapper;
import com.zhangyu.coderman.dao.NotificationMapper;
import com.zhangyu.coderman.dao.QuestionMapper;
import com.zhangyu.coderman.dto.CommentDTO;
import com.zhangyu.coderman.dto.ResultTypeDTO;
import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.modal.Comment;
import com.zhangyu.coderman.modal.Notification;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.myenums.CommentNotificationType;
import com.zhangyu.coderman.myenums.CommentStatus;
import com.zhangyu.coderman.myenums.CommentType;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {


    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    /**
     * 评论和通知
     *
     * @param commentDTO
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/comment")
    public ResultTypeDTO postComment(CommentDTO commentDTO, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            ResultTypeDTO resultTypeDTO = new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
            return resultTypeDTO;
        }
        //验证评论信息不能为空
        if (commentDTO.getContent() == null || "".equals(commentDTO.getContent().trim())) {
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.COMMENT_CANT_EMPTY);
        }
        if (commentDTO.getType() == CommentType.COMMENT_ONE.getVal()) {
            Question dbQuestion = questionMapper.selectByPrimaryKey(commentDTO.getParentId());
            if (dbQuestion == null) {
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //回复问题
            Comment comment = CreateComment(commentDTO, user);
            //评论问题通知
            Notification notification = CreateNotification(dbQuestion.getCreator(), comment, CommentNotificationType.COMMENT_QUESTION.getCode());
            //添加通知
            if (notification.getNotifier() != notification.getReceiver()) {
                notificationMapper.insertSelective(notification);
            }
        }

        if (commentDTO.getType() == CommentType.COMMENT_TWO.getVal()) {
            Comment dbComment = commentMapper.selectByPrimaryKey(commentDTO.getParentId());
            if (dbComment == null) {
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.COMMENT_NOT_FOUNT);
            }
            //回复评论
            Comment comment = CreateComment(commentDTO, user);
            //评论问题通知
            Notification notification = CreateNotification(dbComment.getCommentor(), comment, CommentNotificationType.COMMENT_REPLY.getCode());
            //添加通知
            if (notification.getNotifier() != notification.getReceiver()) {
                notificationMapper.insertSelective(notification);
            }
        }
        return new ResultTypeDTO().okOf();
    }

    /**
     * 创建通知
     *
     * @param :评论的问题或评论
     * @param comment
     * @return
     */
    private Notification CreateNotification(long re, Comment comment, Integer type) {
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(type);
        notification.setNotifier((long) comment.getCommentor());//提示者id
        notification.setReceiver(re);//接收者id
        notification.setOutterId(comment.getParentId());//保存的是评论的父id
        notification.setStatus(CommentStatus.UN_READ.getCode());
        return notification;
    }

    /**
     * 创建回复的评论对象
     *
     * @param commentDTO:前端传过来的数据
     * @param user:当前登入的用户
     */
    private Comment CreateComment(CommentDTO commentDTO, User user) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setParentId(commentDTO.getParentId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setType(commentDTO.getType());
        comment.setCommentor(user.getId());
        commentService.doComment(comment);
        return comment;
    }

    //得到二级评论
    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultTypeDTO comments(@PathVariable("id") String idstr) {
        Integer id;
        try {
            id = Integer.parseInt(idstr);
        } catch (NumberFormatException e) {
            ResultTypeDTO resultTypeDTO = new ResultTypeDTO().errorOf(CustomizeErrorCode.COMMENT_NOT_FOUNT);
            return resultTypeDTO;
        }
        List<CommentDTO> comment2DTOList = commentService.findSecondComments(id);
        return new ResultTypeDTO().okOf().addMsg("comment2s", comment2DTOList);
    }

    //点赞评论
    @ResponseBody
    @RequestMapping("/likeComment")
    public ResultTypeDTO likeComment(@RequestParam("id") Integer id,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            //throw  new CustomizeException(CustomizeErrorCode.USER_NO_LOGIN);
            return new ResultTypeDTO().errorOf(CustomizeErrorCode.USER_NO_LOGIN);
        }
        Comment dbComment = commentMapper.selectByPrimaryKey(id);
        if (dbComment == null) {
            throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUNT);
        } else {
            Integer commentorId = dbComment.getCommentor();//被点赞的的人
            Integer userId = user.getId();
            if(commentorId==userId){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.NOT_LIKE_YOURSELF);//不能给自己点赞
            }
            //点赞增加
            commentExtMapper.incLikeCount(id);
            //通知
            Notification notification = new Notification();
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setType(CommentNotificationType.COMMENT_Like.getCode());
            notification.setNotifier((long)userId);
            notification.setReceiver((long)commentorId);
            notification.setStatus(CommentStatus.UN_READ.getCode());
            notification.setOutterId(dbComment.getId());

            notificationMapper.insertSelective(notification);
        }
        return new ResultTypeDTO().okOf().addMsg("likecount",dbComment.getLikeCount()+1);
    }


}
