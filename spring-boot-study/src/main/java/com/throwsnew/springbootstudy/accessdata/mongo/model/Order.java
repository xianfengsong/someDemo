package com.throwsnew.springbootstudy.accessdata.mongo.model;

public class Order {

    private String id;
    private String info;
    private Long createTime;

    @Override
    public String toString() {
        return "Order{" +
                "_id='" + id + '\'' +
                ", info='" + info + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
