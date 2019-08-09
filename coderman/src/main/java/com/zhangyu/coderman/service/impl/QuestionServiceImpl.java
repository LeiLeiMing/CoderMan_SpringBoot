package com.zhangyu.coderman.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.dao.CommentMapper;
import com.zhangyu.coderman.dao.QuestionExtMapper;
import com.zhangyu.coderman.dao.QuestionMapper;
import com.zhangyu.coderman.dao.UserMapper;
import com.zhangyu.coderman.dto.CommentDTO;
import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.modal.Comment;
import com.zhangyu.coderman.modal.CommentExample;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Override
    public void doPublish(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public PageInfo<Question> getPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> list = questionExtMapper.listQuestionWithUser();
        PageInfo<Question> questionPageInfo = new PageInfo<>(list);
        questionPageInfo.setNavigatePages(5);
        return questionPageInfo;
    }

    @Override
    public PageInfo<Question> findQuestionsByUserId(Integer pageNo, Integer pageSize, Integer id) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> list = questionExtMapper.listQuestionWithUserByUserId(id);
        PageInfo<Question> questionPageInfo = new PageInfo<>(list);
        questionPageInfo.setNavigatePages(5);
        return questionPageInfo;
    }

    @Override
    public Question findQuestionById(Integer id) {
        Question question=questionExtMapper.findQuestionWithUserById(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        return question;
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.updateByPrimaryKeySelective(question);
    }

    @Override
    public void saveOrUpdate(Question question,Integer userid) {
        if(question.getId()==null){
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        }else{
            Question dbQuestion = questionExtMapper.findQuestionWithUserById(question.getId());
            if(dbQuestion!=null&&dbQuestion.getCreator()!=userid){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_IS_YOURS);
            }
            questionMapper.updateByPrimaryKeySelective(question);
        }
    }

    @Override
    public List<Question> relatedQuestions(Question question) {
        List<Question> relatedList=null;
        String tags = question.getTag();
       if(!StringUtils.isEmpty(tags)){
           String sqlRegex = tags.replaceAll(",", "|");
          relatedList =questionExtMapper.selectRelated(sqlRegex,question.getId());
       }
        return relatedList;
    }

    @Override
    public void incViewCount(Question question) {
        questionExtMapper.inCViewCount(question);
    }

    @Override
    public List<CommentDTO> findQuestionComments(Integer id) {
        CommentExample commentExample = new CommentExample();
        commentExample.setOrderByClause("gmt_create desc");
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andParentIdEqualTo(id);
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        List<CommentDTO> commentlist=null;
        if(comments.size()>0){
           commentlist= new ArrayList<>();
            for (Comment comment : comments) {
                CommentDTO commentDTO = new CommentDTO();
                BeanUtils.copyProperties(comment,commentDTO);
                commentDTO.setUser(userMapper.selectByPrimaryKey(comment.getCommentor()));
                commentlist.add(commentDTO);
            }
        }
        return commentlist;
    }

    @Override
    public PageInfo<Question> getPageBySearch(String search, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> list = questionExtMapper.listQuestionWithUserBySearch(search);
        PageInfo<Question> questionPageInfo = new PageInfo<>(list);
        questionPageInfo.setNavigatePages(5);
        return questionPageInfo;
    }

}
