package com.zhangyu.coderman.controller;

import com.zhangyu.coderman.cache.TagsCache;
import com.zhangyu.coderman.dao.UserMapper;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.modal.User;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.myenums.QuestionErrorEnum;
import com.zhangyu.coderman.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag") String tag, HttpServletRequest request,
                            @RequestParam(name = "id",required = false) Integer id,
                            Map<String, Object> map,Model model) {
        User user = (User) request.getSession().getAttribute("user");
        map.put("title",title);
        map.put("description",description);
        map.put("tag",tag);
        if(title==null||"".equals(title)){
            map.put("msg", QuestionErrorEnum.QUESTION_HEAD_CANT_EMPTY);
            //Tags
            List<TagsCache> tagsCache = TagsCache.getTagsCache();
            model.addAttribute("tagsCache",tagsCache);
            return "/publish";
        }
        if(description==null||"".equals(description)){
            map.put("msg", QuestionErrorEnum.QUESTION_DESC_CANT_EMPTY);
            //Tags
            List<TagsCache> tagsCache = TagsCache.getTagsCache();
            model.addAttribute("tagsCache",tagsCache);
            return "/publish";
        }
        if(tag==null||"".equals(tag)){
            map.put("msg", QuestionErrorEnum.QUESTION_TAGS_CANT_EMPTY);
            //Tags
            List<TagsCache> tagsCache = TagsCache.getTagsCache();
            model.addAttribute("tagsCache",tagsCache);
            return "/publish";
        }
        if (user == null) {
            map.put("msg", QuestionErrorEnum.QUESTION_NEED_LOGIN);
            //Tags
            List<TagsCache> tagsCache = TagsCache.getTagsCache();
            model.addAttribute("tagsCache",tagsCache);
            return "/publish";
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        question.setCreator(user.getId());
        question.setId(id);

        questionService.saveOrUpdate(question,user.getId());
        //questionService.doPublish(question);

        return "redirect:/";
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
