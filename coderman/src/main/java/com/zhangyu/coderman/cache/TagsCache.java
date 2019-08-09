package com.zhangyu.coderman.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagsCache {

    private String tagTitle;

    private List<String> tags=new ArrayList<>();

    public static List<TagsCache> getTagsCache(){

        List<TagsCache> tagsCaches = new ArrayList<>();

        TagsCache t1 = new TagsCache();
        t1.setTagTitle("编程语言");
        t1.setTags(Arrays.asList("java","css","html","js","c#","php","go","html5","C++","SQL","VB"));


        TagsCache t2 = new TagsCache();
        t2.setTagTitle("框架");
        t2.setTags(Arrays.asList("spring","Spring boot","Struts","JPA","Spring mvc","Hibernate","Bootstrap"));

        TagsCache t3 = new TagsCache();
        t3.setTagTitle("开发工具");
        t3.setTags(Arrays.asList("MYSQL","TOMCAT","Oracle","IDEA","HBuilder","VSCode","PostMan"));

        tagsCaches.add(t1);
        tagsCaches.add(t2);
        tagsCaches.add(t3);
        return tagsCaches;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
