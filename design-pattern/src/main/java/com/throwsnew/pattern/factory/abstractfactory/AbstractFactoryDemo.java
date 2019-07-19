package com.throwsnew.pattern.factory.abstractfactory;

import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午4:32 <br/>
 * Desc:
 * 抽象工厂
 * https://en.wikipedia.org/wiki/Abstract_factory_pattern
 *
 * 目的：
 * 分离对象的使用和创建
 * 如果工厂方法可以创建一类对象，抽象工厂则可以创建有相似主题的多个对象（对象族）
 *
 * 实现：
 * 定义抽象工厂类，约定创建哪些类型的对象
 * 抽象工厂子类负责实例化同一个主题的多个类型的对象
 * 想增加一个主题就增加一个子类
 */
public class AbstractFactoryDemo {

    /**
     * 根据参数创建不同的GUIFactory实现类
     */
    GUIFactory createFactory(String appearance) {
        switch (appearance) {
            //windows gui factory impl
            case "win":
                //lambda 非人类版
                return WinButton::new;

            //mac gui factory impl
            case "osx":
                //可以友好阅读版。。
                return new GUIFactory() {
                    @Override
                    public Button createButton() {
                        System.out.println("create for osx");
                        return new OSXButton();
                    }
                };

            default:
                throw new IllegalArgumentException("unknown " + appearance);
        }
    }

    @Test
    public void useFactory() {
        GUIFactory factory1 = createFactory("win");
        factory1.createButton();
        factory1 = createFactory("osx");
        factory1.createButton();
    }

    /**
     * --- products ----
     */
    interface Button {

        void draw();
    }
    //---- 抽象工厂 -----

    /**
     * 这个注解就算不写，java8也会把他识别为FunctionalInterface
     * 但是必须只能有一个抽象方法，否则编译失败
     */
    @FunctionalInterface
    interface GUIFactory {

        public Button createButton();
//        public void waht(); //编译失败“不是函数接口”
    }

    class WinButton implements Button {

        @Override
        public void draw() {
            System.out.println("WinButton");
        }
    }

    class OSXButton implements Button {

        @Override
        public void draw() {
            System.out.println("OSXButton");
        }
    }
}
