
package com.zhangyu.coderman.controller;

import com.github.pagehelper.PageInfo;
import com.zhangyu.coderman.cache.HotTagCache;
import com.zhangyu.coderman.dto.NewUserDTO;
import com.zhangyu.coderman.dto.QuestionDTO;
import com.zhangyu.coderman.dto.QuestionQueryDTO;
import com.zhangyu.coderman.dto.ResultTypeDTO;
import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.modal.Question;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import com.zhangyu.coderman.service.QuestionService;
import com.zhangyu.coderman.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    protected static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private HotTagCache hotTagCache;


    @ResponseBody
    @GetMapping("/explore/{action}")
    private String explore(@PathVariable("action") String action,
                           @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize,
                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                           HttpServletRequest request,
                           Map<String, Object> map) {
        String[] split = action.split("-");
        Integer categoryVal = 0;
        try {
            categoryVal = Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.NOT_FOND_CATEGORY);
        }
        PageInfo<Question> questionPageInfo;
        if (categoryVal == 0) {
            request.getSession().setAttribute("category", categoryVal);
            return "forward:/";
        }
        questionPageInfo = questionService.findQuestionsByCategory(pageNo, pageSize, categoryVal);
        //最新用户
        List<NewUserDTO> userList = userService.findNewsUsers(8);
        //热门标签
        List<String> tags = hotTagCache.updateTags();
        //最新问题
        List<QuestionDTO> NewQuestions = questionService.findNewQuestion(6);

        map.put("sort", "all");//全部
        request.getSession().setAttribute("category", categoryVal);
        map.put("newQuestions", NewQuestions);
        map.put("users", userList);
        map.put("page", questionPageInfo);
        map.put("navLi", "find");
        map.put("hotTags", tags);
        return "redirect:/index.html";
    }


    //首页
    @RequestMapping("/")
    public String index(@RequestParam(value = "tag", required = false) String tag,
                        @RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "category", defaultValue = "0") String category,
                        Map<String, Object> map) {
        map.put("tag", tag);
        map.put("search", search);
        map.put("category", category);
        return "index";
    }

    @ResponseBody
    @GetMapping("/loadQuestionList")
    public ResultTypeDTO listQuestion(@RequestParam(name = "sortby", defaultValue = "all") String sort,
                                      @RequestParam(name = "search", required = false) String search,
                                      @RequestParam(name = "tag", required = false) String tag,
                                      @RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "category", defaultValue = "0") String categoryStrVal,
                                      HttpServletRequest request) {
        Integer category = null;
        try {
            category = Integer.parseInt(categoryStrVal);
        } catch (NumberFormatException e) {

        }
        //获取首页列表数据
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setSort(sort);
        questionQueryDTO.setTag(tag);
        questionQueryDTO.setCategory(category);
        PageInfo<Question> questionPageInfo = questionService.getPageBySearch(questionQueryDTO, pageNo, pageSize);
        request.getSession().setAttribute("category", category);//全部
        return new ResultTypeDTO().okOf().addMsg("page", questionPageInfo);
    }


    @ResponseBody
    @GetMapping("/loadRightList")
    public ResultTypeDTO loadRightList() {
        //最新用户
        List<NewUserDTO> userList = userService.findNewsUsers(5);
        //热门标签
        List<String> tags = hotTagCache.updateTags();
        //最新问题
        List<QuestionDTO> NewQuestions = questionService.findNewQuestion(6);
        //问题推荐
        int pageno = 1;
        int pagesize = 6;
        List<QuestionDTO> RecommendQuestions = questionService.findRecommendQuestions(pageno, pagesize);
        return new ResultTypeDTO().okOf().addMsg("userList", userList)
                .addMsg("tags", tags)
                .addMsg("newQuestions", NewQuestions)
                .addMsg("recommend", RecommendQuestions);
    }


}
