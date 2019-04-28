package com.feiqu.common.enums;

//计算推荐笔记
public enum CalRecommendEnum {
    NEIGHBORHOOD_NUM ("相邻数", 2),
    RECOMMEND_COUNT("推荐数量", 5),
    RECOMMEND_USER("基于用户推荐",1),
    RECOMMEND_ARTICLE("基于笔记推荐",2);



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
    CalRecommendEnum(String desc, Integer value) {
        this.desc = desc;
        this.value = value;
    }
}
