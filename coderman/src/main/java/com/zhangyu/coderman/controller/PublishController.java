package com.zhangyu.coderman.controller;

import com.zhangyu.coderman.cache.TagsCache;
import com.zhangyu.coderman.dto.ResultTypeDTO;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.myenums.QuestionErrorEnum;
import com.zhangyu.coderman.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish")
    public String publish(Map<String,Object> map, Model model) {
        //Tags
        List<TagsCache> tagsCache = TagsCache.getTagsCache();
        model.addAttribute("tagsCache",tagsCache);
        map.put("navLi","publish");
        return "publish";
    }

    /**
     * 发布问题
     *
     * @param title;问题的标题
     * @param description:问题的描述
     * @param tag:标签
     * @param request
     * @param map
     * @return
     */
    @ResponseBody
    @PostMapping("/publish")
    public ResultTypeDTO doPublish(@RequestParam("title") String title,
                                   @RequestParam("description") String description,
                                   @RequestParam("tag") String tag, HttpServletRequest request,
                                   @RequestParam(name = "id",required = false) Integer id,
                                   @RequestParam(name="category") Integer category,
                                   Map<String, Object> map, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        //验证用户登入
        if (user == null) {
            return new ResultTypeDTO().errorOf(QuestionErrorEnum.QUESTION_NEED_LOGIN);
        }
        if(title==null||"".equals(title.trim())){
            return new ResultTypeDTO().errorOf(QuestionErrorEnum.QUESTION_HEAD_CANT_EMPTY);
        }
        if(category==0){
            return new ResultTypeDTO().errorOf(QuestionErrorEnum.Question_Category_CANT_EMPTY);
        }
        if(description==null||"".equals(description.trim())){
            return new ResultTypeDTO().errorOf(QuestionErrorEnum.QUESTION_DESC_CANT_EMPTY);
        }
        if(tag==null||"".equals(tag.trim())){
           return new ResultTypeDTO().errorOf(QuestionErrorEnum.QUESTION_TAGS_CANT_EMPTY);
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        question.setCategory(category);

        ResultTypeDTO result= null;
        try {
            result = questionService.saveOrUpdate(question,user.getId());
        } catch (Exception e) {

            return  new ResultTypeDTO().errorOf(CustomizeErrorCode.NOT_ADD_OTHER_BQ);
        }
        //questionService.doPublish(question);
       // return "redirect:/";
        return result;
    }

    /**
     * 修改问题
     * @return
     */
    @GetMapping("/publish/{id}")
    public String editQuestion(@PathVariable("id") Integer id,Map<String,Object> map,Model model){
        Question question =questionService.findQuestionById(id);
        List<TagsCache> tagsCache = TagsCache.getTagsCache();
        model.addAttribute("tagsCache",tagsCache);
        map.put("title",question.getTitle());
        map.put("description",question.getDescription());
        map.put("tag",question.getTag());
        map.put("id",id);
        map.put("category",question.getCategory());
        return "publish";
    }

    @PostMapping("/publish/{id}")
    public String doUpdate(@PathVariable("id") Integer id,@RequestParam("title") String title,
                           @RequestParam("description") String description,
                           @RequestParam("tag") String tag){
        Question question=questionService.findQuestionById(id);
        question.setTitle(title);
        question.setDescription(description);
        question.setGmtModified(System.currentTimeMillis());
        question.setTag(tag);
        questionService.updateQuestion(question);
        return "redirect:/";
    }

}
