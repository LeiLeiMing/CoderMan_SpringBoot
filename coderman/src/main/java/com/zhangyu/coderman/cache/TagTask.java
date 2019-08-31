package com.zhangyu.coderman.cache;

import com.github.pagehelper.PageHelper;
import com.zhangyu.coderman.dao.QuestionMapper;
import com.zhangyu.coderman.modal.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class TagTask {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private HotTagCache hotTagCache;

    //30个小时更新一次热门
    @Scheduled(fixedRate = 10000*6*30)
    public void printTest() {
        int pageNo = 1;
        int pageSize = 10;
        List<Question> questions = new ArrayList<>();
        Map<String, Integer> properties = new HashMap<>();
        while (pageNo == 1 || questions.size() == pageSize) {
            //questions = questionExtMapper.findQuestionPage((pageNo - 1) * pageSize, pageSize);
            PageHelper.startPage(pageNo,pageSize);
            questions=questionMapper.selectByExample(null);
            for (Question question : questions) {
                String tagstr = question.getTag();
                String[] tags = tagstr.split(",");
                for (String tag : tags) {
                    Integer integer = properties.get(tag);
                    if (integer != null) {
                        properties.put(tag, integer + 5 + question.getCommentCount());
                    } else {
                        properties.put(tag, 5 + question.getCommentCount());
                    }
                }
            }
            pageNo++;
        }
        hotTagCache.setProperties(properties);
        //日志

        /**
        Map<String, Integer> map = hotTagCache.getProperties();
        for(Map.Entry<String,Integer> entry:map.entrySet()){
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println("标签:"+key+"\t 权重:"+value);
        }
        System.out.println("-------------一次定时任务结束---------------------->");

         **/

    }


}
