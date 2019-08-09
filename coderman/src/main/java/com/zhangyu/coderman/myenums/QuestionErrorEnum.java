package com.zhangyu.coderman.myenums;

public enum QuestionErrorEnum {

    QUESTION_CANT_EMPTY("问题不能为空"),
    QUESTION_HEAD_CANT_EMPTY("问题标题不能为空"),
    QUESTION_DESC_CANT_EMPTY("问题描述不能为空"),
    QUESTION_TAGS_CANT_EMPTY("问题标签不能为空"),
    QUESTION_NEED_LOGIN("发表问题需要登入哟~~");

    private String msg;

    QuestionErrorEnum(String msg) {
        this.msg = msg;
    }
    @Override
    public String toString() {
        return this.msg;
    }
}
