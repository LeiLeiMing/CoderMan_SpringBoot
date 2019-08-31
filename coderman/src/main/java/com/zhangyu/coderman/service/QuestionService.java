package com.zhangyu.coderman.service;

import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.dto.CommentDTO;
import com.zhangyu.coderman.dto.QuestionDTO;
import com.zhangyu.coderman.dto.QuestionQueryDTO;
import com.zhangyu.coderman.dto.ResultTypeDTO;
import com.zhangyu.coderman.modal.Question;

import java.util.List;

public interface QuestionService {

    void doPublish(Question question);

    PageInfo<Question> getPage(Integer pageNo, Integer pageSize);

    PageInfo<Question> findQuestionsByUserId(Integer pageNo, Integer pageSize, Integer id);

    Question findQuestionById(Integer id);

    void updateQuestion(Question question);

    ResultTypeDTO saveOrUpdate(Question question, Integer userid);

    List<Question> relatedQuestions(Question question);

    void incViewCount(Question question);

    List<CommentDTO> findQuestionComments(Integer id);

    PageInfo<Question> getPageBySearch(QuestionQueryDTO questionQueryDTO, Integer pageNo, Integer pageSize);


    List<QuestionDTO> findNewQuestion(int i);

    PageInfo<Question> findQuestionsByCategory(Integer pageNo, Integer pageSize, Integer categoryVal);

    List<QuestionDTO> findRecommendQuestions(int pageno, int pagesize);
}
