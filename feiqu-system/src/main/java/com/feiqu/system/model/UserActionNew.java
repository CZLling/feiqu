package com.feiqu.system.model;

import java.util.Date;

public class UserActionNew {
    private Integer id;
    private Integer actionType;
    private Integer articleId;
    private Integer actionUserId;
    private Integer otherUserId;
    private Integer articleLabel;
    private Date createTime;

    public UserActionNew() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(Integer actionUserId) {
        this.actionUserId = actionUserId;
    }

    public Integer getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Integer otherUserId) {
        this.otherUserId = otherUserId;
    }

    public Integer getArticleLabel() {
        return articleLabel;
    }

    public void setArticleLabel(Integer articleLabel) {
        this.articleLabel = articleLabel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
