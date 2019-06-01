package com.feiqu.common.enums;

//定义用户行为权重
public enum UserActionEnum {
    BROWSE("浏览",1),
    LIKE("点赞",2),
    COLLECT("收藏",3),
    CANCELCOLLECT("取消收藏",-3);

    private String desc;
    private Integer value;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    UserActionEnum(String desc, Integer value) {
        this.desc = desc;
        this.value = value;
    }
}
