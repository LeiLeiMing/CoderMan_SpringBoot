package com.zhangyu.coderman.dao;

import com.zhangyu.coderman.dto.QuestionDTO;
import com.zhangyu.coderman.modal.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface QuestionExtMapper {

    Question findQuestionWithUserById(Integer id);

    List<Question> listQuestionWithUserByUserId(Integer id);

    List<Question> listQuestionWithUser();

    @Select(value = "select * from question where tag regexp #{sqlRegex} and id!=#{id}")
    List<Question> selectRelated(@Param("sqlRegex") String sqlRegex, @Param("id") Integer id);

    @Select(value = "update question set view_count=view_count +1 where id=#{id}")
    void inCViewCount(Question question);

    @Update("update question set comment_count=comment_count+1 where id=#{id}")
    void incCommentCount(@Param("id") Integer parentId);

    List<Question> listQuestionWithUserBySearch(@Param("search") String search, @Param("tag") String tag, @Param("category") Integer category);

    @Update("update question set like_count=like_count+1 where id=#{id}")
    void incLikeCount(Integer id);

    @Select(value = "select * from question order by gmt_create desc limit 0,#{i}")
    List<QuestionDTO> findNewQuestion(int i);

    List<Question> listQuestionHotByTime(@Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("tag") String tag, @Param("category") Integer category);

    List<Question> listQuestionZeroHot(@Param("tag") String tag, @Param("category") Integer category);

    List<Question> listQuestionWithUserBycategory(Integer categoryVal);

    @Select("select * from question order by comment_count desc,like_count desc,view_count desc ")
    List<QuestionDTO> findRecommendQuestions();
//    @Select(value = "select * from question limit #{offset},#{limit}")
//    List<Question> findQuestionPage(@Param("offset") int offset,@Param("limit") int limit);
}
