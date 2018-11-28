package com.miui.global.browser.module.ad.interstitial.dao.model;

import java.util.Date;

public class InterstitialAdDO {
    private Long id;

    private Integer type;

    private String region;

    private String appVersion;

    private Integer scale;

    private Integer reload;

    private Integer maxNumber;

    private Integer everyQuery;

    private Integer everyExpose;

    private Integer firstShow;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion == null ? null : appVersion.trim();
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Integer getReload() {
        return reload;
    }

    public void setReload(Integer reload) {
        this.reload = reload;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getEveryQuery() {
        return everyQuery;
    }

    public void setEveryQuery(Integer everyQuery) {
        this.everyQuery = everyQuery;
    }

    public Integer getEveryExpose() {
        return everyExpose;
    }

    public void setEveryExpose(Integer everyExpose) {
        this.everyExpose = everyExpose;
    }

    public Integer getFirstShow() {
        return firstShow;
    }

    public void setFirstShow(Integer firstShow) {
        this.firstShow = firstShow;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}