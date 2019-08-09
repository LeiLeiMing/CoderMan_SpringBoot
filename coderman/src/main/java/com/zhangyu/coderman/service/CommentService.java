package com.zhangyu.coderman.service;

import com.zhangyu.coderman.dto.CommentDTO;
import com.zhangyu.coderman.modal.Comment;

import java.util.List;

public interface CommentService {

    void doComment(Comment comment);

    List<CommentDTO> findSecondComments(Integer id);
}
