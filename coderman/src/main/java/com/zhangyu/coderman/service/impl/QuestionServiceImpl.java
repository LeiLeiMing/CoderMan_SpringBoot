package com.zhangyu.coderman.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.cache.HotTagCache;
import com.zhangyu.coderman.dao.CommentMapper;
import com.zhangyu.coderman.dao.QuestionExtMapper;
import com.zhangyu.coderman.dao.QuestionMapper;
import com.zhangyu.coderman.dao.UserMapper;
import com.zhangyu.coderman.dto.CommentDTO;
import com.zhangyu.coderman.dto.QuestionDTO;
import com.zhangyu.coderman.dto.QuestionQueryDTO;
import com.zhangyu.coderman.dto.ResultTypeDTO;
import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.modal.Comment;
import com.zhangyu.coderman.modal.CommentExample;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.myenums.QuestionCategory;
import com.zhangyu.coderman.myenums.QuestionErrorEnum;
import com.zhangyu.coderman.service.QuestionService;
import com.zhangyu.coderman.utils.DateFormateUtil;
import com.zhangyu.coderman.utils.DatesUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private HotTagCache hotTagCache;

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
        questionPageInfo.setNavigatePages(3);
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
    public ResultTypeDTO saveOrUpdate(Question question, Integer userid) {
        if(question.getId()==null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
            return new ResultTypeDTO().okOf().addMsg("result", QuestionErrorEnum.QUESTION_PUBLISH_SUCCESS.getMsg());
        }else{
            Question dbQuestion = questionExtMapper.findQuestionWithUserById(question.getId());
            question.setGmtModified(question.getGmtCreate());
            if(dbQuestion!=null&&dbQuestion.getCreator()!=userid){
                return new ResultTypeDTO().errorOf(CustomizeErrorCode.QUESTION_NOT_IS_YOURS);
            }
            questionMapper.updateByPrimaryKeySelective(question);
            return new ResultTypeDTO().okOf().addMsg("result", QuestionErrorEnum.QUESTION_UPDATE_SUCCESS.getMsg());
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
    public PageInfo<Question> getPageBySearch(QuestionQueryDTO questionQueryDTO, Integer pageNo, Integer pageSize) {
        List<Question> list=new ArrayList<>();
        PageInfo<Question> questionPageInfo;
        PageHelper.startPage(pageNo,pageSize);
        if ("all".equals(questionQueryDTO.getSort())){
            //全部
            list= questionExtMapper.listQuestionWithUserBySearch(questionQueryDTO.getSearch(),questionQueryDTO.getTag(),questionQueryDTO.getCategory());
       }else if ("weekhot".equals(questionQueryDTO.getSort())){
           //本周最热
            long startweektime = DatesUtil.getBeginDayOfWeek().getTime();
            long endweetime=DatesUtil.getEndDayOfWeek().getTime();
            list=questionExtMapper.listQuestionHotByTime(startweektime,endweetime,questionQueryDTO.getTag(),questionQueryDTO.getCategory());
        }else if ("monthhot".equals(questionQueryDTO.getSort())){
            //月最热
            long time = DatesUtil.getBeginDayOfMonth().getTime();
            long endtime=DatesUtil.getEndDayOfMonth().getTime();
            list=questionExtMapper.listQuestionHotByTime(time,endtime, questionQueryDTO.getTag(), questionQueryDTO.getCategory());
        }else if("zerohot".equals(questionQueryDTO.getSort())){
            //0评论
            list=questionExtMapper.listQuestionZeroHot(questionQueryDTO.getTag(),questionQueryDTO.getCategory());
        }
        //时间格式化,typename
        BuildQuestionTime(list);
        questionPageInfo = new PageInfo<>(list);
        questionPageInfo.setNavigatePages(5);
        return questionPageInfo;
    }

    @Override
    public List<QuestionDTO> findNewQuestion(int i) {
        List<QuestionDTO> questions=questionExtMapper.findNewQuestion(i);
        return questions;
    }

    @Override
    public PageInfo<Question> findQuestionsByCategory(Integer pageNo, Integer pageSize, Integer categoryVal) {
        PageHelper.startPage(pageNo,pageSize);
        List<Question> questions = questionExtMapper.listQuestionWithUserBycategory(categoryVal);
        BuildQuestionTime(questions);
        PageInfo<Question> questionPageInfo = new PageInfo<>(questions);
        return questionPageInfo;
    }

    @Override
    public List<QuestionDTO> findRecommendQuestions(int pageno, int pagesize) {
        PageHelper.startPage(pageno,pagesize);
        List<QuestionDTO> questionDTOS= questionExtMapper.findRecommendQuestions();
        return questionDTOS;
    }

    private void BuildQuestionTime(List<Question> questions) {
        for (Question question : questions) {
            Date date = new Date(question.getGmtCreate());
            String dateString = simpleDateFormat.format(date);
            String time = DateFormateUtil.getTime(dateString);
            question.setShowTime(time);
            //
            Integer category = question.getCategory();
            String typename = QuestionCategory.getnameByVal(category);
            question.setTypeName(typename);
        }
    }


}
