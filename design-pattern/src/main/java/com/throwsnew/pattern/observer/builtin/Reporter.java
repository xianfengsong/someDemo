package com.throwsnew.pattern.observer.builtin;

import java.util.Observable;
import java.util.Observer;

/**
 * author Xianfeng <br/>
 * date 18-12-21 下午5:48 <br/>
 * Desc:
 */
public class Reporter implements Observer {

    private String concernCategory;

    Reporter(String concernCategory) {
        this.concernCategory = concernCategory;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof News) {
            News news = (News) arg;
            if (concernCategory.equals(news.getCategory())) {
                System.out.print(news.getContent());
            }
        }
    }

}
