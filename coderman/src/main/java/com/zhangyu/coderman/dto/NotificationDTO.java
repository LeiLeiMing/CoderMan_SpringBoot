package com.zhangyu.coderman.dto;

import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.myenums.CommentNotificationType;

public class NotificationDTO<T> {

    private Integer id;

    //通知人的姓名
    private User notifier;
    //通知的类型
    private CommentNotificationType commentNotificationType;
    //外键信息
    private T item;
    //状态
    private Integer status;

    private long gmtCreate;

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getNotifier() {
        return notifier;
    }

    public void setNotifier(User notifier) {
        this.notifier = notifier;
    }

    public CommentNotificationType getCommentNotificationType() {
        return commentNotificationType;
    }

    public void setCommentNotificationType(CommentNotificationType commentNotificationType) {
        this.commentNotificationType = commentNotificationType;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
