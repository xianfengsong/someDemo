package com.throwsnew.pattern.observer.builtin;

/**
 * author Xianfeng <br/>
 * date 18-12-21 下午5:46 <br/>
 * Desc:
 */
public class News {

    private String category;
    private String content;

    public News(String category, String content) {
        this.category = category;
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "News{" +
                "category='" + category + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
