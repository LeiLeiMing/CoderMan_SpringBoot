package com.zhangyu.coderman.service.impl;

import com.github.pagehelper.PageHelper;
import com.zhangyu.coderman.dao.CommentMapper;
import com.zhangyu.coderman.dao.NotificationMapper;
import com.zhangyu.coderman.dao.QuestionMapper;
import com.zhangyu.coderman.dao.UserMapper;
import com.zhangyu.coderman.dto.NotificationDTO;
import com.zhangyu.coderman.modal.*;
import com.zhangyu.coderman.myenums.CommentNotificationType;
import com.zhangyu.coderman.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<NotificationDTO> list(Integer pageNo, Integer pageSize, Integer id) {
        NotificationExample example = new NotificationExample();
        NotificationExample.Criteria criteria = example.createCriteria();
        criteria.andReceiverEqualTo((long) id);
        //不选出自己评论自己的信息
        criteria.andNotifierNotEqualTo((long) id);
        example.setOrderByClause("gmt_create desc");
        PageHelper.startPage(pageNo, pageSize);
        List<Notification> notifications = notificationMapper.selectByExample(example);
        if (notifications.size() > 0) {
            List<NotificationDTO> notificationDTOlist = new ArrayList<>();
            for (Notification notification : notifications) {
                if (notification.getType() == CommentNotificationType.COMMENT_QUESTION.getCode()) {
                    NotificationDTO<Question> notificationDTO = new NotificationDTO();
                    Long notifier = notification.getNotifier();
                    int notfiterId = notifier.intValue();
                    User user = userMapper.selectByPrimaryKey(notfiterId);
                    //封装信息
                    CreateNotificationDTOCommentQuestion(notificationDTOlist, notification, notificationDTO, user);
                } else if (notification.getType() == CommentNotificationType.COMMENT_REPLY.getCode()) {
                    NotificationDTO<Comment> notificationDTO = new NotificationDTO();
                    Long notifier = notification.getNotifier();
                    int notfiterId = notifier.intValue();
                    User user = userMapper.selectByPrimaryKey(notfiterId);
                    //封装信息
                    CreateNotificationDTOCommentReply(notificationDTOlist, notification, notificationDTO, user);
                } else if (notification.getType() == CommentNotificationType.COMMENT_Like.getCode()) {
                    NotificationDTO<Comment> notificationDTO = new NotificationDTO();
                    Long notifier = notification.getNotifier();
                    int notfiterId = notifier.intValue();
                    User user = userMapper.selectByPrimaryKey(notfiterId);
                    CreateNotificationDTOCommentLike(notificationDTOlist, notification, notificationDTO, user);
                } else if (notification.getType() == CommentNotificationType.LIKE_QUESTION.getCode()) {
                    NotificationDTO<Question> notificationDTO = new NotificationDTO();
                    Long notifier = notification.getNotifier();
                    int notfiterId = notifier.intValue();
                    User user = userMapper.selectByPrimaryKey(notfiterId);
                    CreateNotificationDTOQuestionLike(notificationDTOlist, notification, notificationDTO, user);
                } else if (notification.getType() == CommentNotificationType.FOLLOWING.getCode()) {
                    NotificationDTO<User> notificationDTO = new NotificationDTO();
                    Long notifier = notification.getNotifier();
                    int notfiterId = notifier.intValue();
                    User user = userMapper.selectByPrimaryKey(notfiterId);
                    CreateNotificationDTOFollowing(notificationDTOlist, notification, notificationDTO, user);
                }

            }
            return notificationDTOlist;
        }
        return new ArrayList<>();
    }

    /**
     * 封装评论问题的通知
     *
     * @param notificationDTOlist
     * @param notification
     * @param notificationDTO
     * @param user
     */
    private void CreateNotificationDTOCommentReply(List<NotificationDTO> notificationDTOlist, Notification notification, NotificationDTO<Comment> notificationDTO, User user) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setNotifier(user);
        notificationDTO.setStatus(notification.getStatus());
        notificationDTO.setGmtCreate(notification.getGmtCreate());
        notificationDTO.setCommentNotificationType(CommentNotificationType.COMMENT_REPLY);
        notificationDTO.setItem(commentMapper.selectByPrimaryKey(notification.getOutterId()));
        notificationDTOlist.add(notificationDTO);
    }

    /**
     * 封装关注的通知
     *
     * @param notificationDTOlist
     * @param notification
     * @param notificationDTO
     * @param user
     */
    private void CreateNotificationDTOFollowing(List<NotificationDTO> notificationDTOlist, Notification notification, NotificationDTO<User> notificationDTO, User user) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setNotifier(user);
        notificationDTO.setStatus(notification.getStatus());
        notificationDTO.setGmtCreate(notification.getGmtCreate());
        notificationDTO.setCommentNotificationType(CommentNotificationType.FOLLOWING);
        //notificationDTO.setItem(commentMapper.selectByPrimaryKey(notification.getOutterId()));
        notificationDTOlist.add(notificationDTO);
    }

    /**
     * 封装评论点赞的通知
     *
     * @param notificationDTOlist
     * @param notification
     * @param notificationDTO
     * @param user
     */
    private void CreateNotificationDTOCommentLike(List<NotificationDTO> notificationDTOlist, Notification notification, NotificationDTO<Comment> notificationDTO, User user) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setNotifier(user);
        notificationDTO.setGmtCreate(notification.getGmtCreate());
        notificationDTO.setStatus(notification.getStatus());
        notificationDTO.setCommentNotificationType(CommentNotificationType.COMMENT_Like);
        notificationDTO.setItem(commentMapper.selectByPrimaryKey(notification.getOutterId()));
        notificationDTOlist.add(notificationDTO);
    }

    /**
     * 封装回复的通知
     *
     * @param notificationDTOlist
     * @param notification
     * @param notificationDTO
     * @param user
     */
    private void CreateNotificationDTOCommentQuestion(List<NotificationDTO> notificationDTOlist, Notification notification, NotificationDTO<Question> notificationDTO, User user) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setNotifier(user);
        notificationDTO.setGmtCreate(notification.getGmtCreate());
        notificationDTO.setStatus(notification.getStatus());
        notificationDTO.setCommentNotificationType(CommentNotificationType.COMMENT_QUESTION);
        Question question = questionMapper.selectByPrimaryKey(notification.getOutterId());
        notificationDTO.setItem(question);
        notificationDTOlist.add(notificationDTO);
    }

    /**
     * 封装回复的通知
     *
     * @param notificationDTOlist
     * @param notification
     * @param notificationDTO
     * @param user
     */
    private void CreateNotificationDTOQuestionLike(List<NotificationDTO> notificationDTOlist, Notification notification, NotificationDTO<Question> notificationDTO, User user) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setNotifier(user);
        notificationDTO.setGmtCreate(notification.getGmtCreate());
        notificationDTO.setStatus(notification.getStatus());
        notificationDTO.setCommentNotificationType(CommentNotificationType.LIKE_QUESTION);
        Question question = questionMapper.selectByPrimaryKey(notification.getOutterId());
        notificationDTO.setItem(question);
        notificationDTOlist.add(notificationDTO);
    }
}
