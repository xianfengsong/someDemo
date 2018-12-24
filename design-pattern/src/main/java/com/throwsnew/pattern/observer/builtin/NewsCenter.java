package com.throwsnew.pattern.observer.builtin;

import java.util.Observable;

/**
 * author Xianfeng <br/>
 * date 18-12-21 下午5:44 <br/>
 * Desc:
 * Observable 是线程安全的
 * 用线程安全集合 Vector<Observer>保存观察者对象
 * 用 boolean changed 保存被观察对象的状态，对changed字段的修改都是synchronized方法
 * addObserver方法也是 synchronized 方法
 */
public class NewsCenter extends Observable {

    private News news;

    public void setNews(News news) {
        this.news = news;
        setChanged();
        //同步通知每个观察者，可能被阻塞
        notifyObservers(news);
    }
}
