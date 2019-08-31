package com.zhangyu.coderman.myenums;

public enum CustomizeErrorCode {

    OK("1000","请求成功"),
    NOT_OK("1500","请求失败"),
    USER_NO_LOGIN("2000","兄弟,你还没有登入呢~"),
    QUESTION_NOT_FOUND("2001","该问题不存在或已被移除"),
    SYSTEM_Error("2002","服务器太累了~"),
    QUESTION_NOT_IS_YOURS("2003","走错房间了吧,兄弟~~"),
    PAGE_NOT_FOUNT("2004","兄弟你找的页面不存在,鸭~" ),
    PEOPLE_DOT_HAVE("2005","用户信息不存在" ),
    COMMENT_CANT_EMPTY("2006","评论内容不能为空"),
    COMMENT_NOT_FOUNT("2006","找不到你要回复的评论"),
    NOT_LIKE_YOURSELF("2007", "不能给自己点赞"),
    CANT_LIKE_YOURSELF_QUESTION("2008","不能给自己的问题点赞" ),
    COMMENT_LIKE_TWICE("2009","不能重复点赞" ),
    CANT_LIKE_QUESTION_TWICE("2010","不能重复点赞问题" ),
    USER_NOT_FOUND("2011","该用户不存在" ),
    NOT_FOND_CATEGORY("20012","找不到此类型"),
    NOT_ADD_OTHER_BQ("20013","暂时不支持其他平台的表情" ),
    COMMENT_CONTENT_TO_MANY("20014","评论的内容不能超过30个字" ),
    READ_ALL_FAIL("20015","全部标为已读失败哦");

    private String message;

    private String code;

    CustomizeErrorCode(String code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CustomizeErrorCode{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
