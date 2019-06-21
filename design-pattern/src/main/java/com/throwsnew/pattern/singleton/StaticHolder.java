package com.throwsnew.pattern.singleton;

import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-6-21 下午5:46 <br/>
 * Desc:
 * 借助jvm对静态类的加载机制实现单例(适用于静态字段)
 * 通过静态类BeanHolder持有对象，延迟初始化，调用BeanHolder时才初始化bean
 */
public class StaticHolder {

    private static Bean initBean() {
        return new Bean(Thread.currentThread().getName());
    }

    private static Bean getBean() {
        return BeanHolder.SINGLETON_BEAN;
    }

    @Test
    public void test() {

        Bean bean = getBean();
        Bean bean1 = getBean();
        Assert.assertEquals("same bean", bean, bean1);
    }

    /**
     * 测试在访问BeanHolder时，持有的bean就被初始化
     */
    @Test
    public void testInitTiming() throws InterruptedException {
        BeanHolder.doNothing();
        Thread t1 = new Thread(() -> {
            Thread.yield();
            Bean bean = getBean();
            System.out.println(bean);
            Assert.assertEquals("主线程初始化的bean", "main", bean.field);
        });
        t1.start();

        t1.join();
    }

    /**
     * 测试并发下 不会重复初始化
     */
    @Test
    public void concurrentTest() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            Thread.yield();
            Bean bean = getBean();
            System.out.println(Thread.currentThread().getName() + "\t" + bean);
        });
        Thread t2 = new Thread(() -> {
            Bean bean = getBean();
            System.out.println(Thread.currentThread().getName() + "\t" + bean);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private static class BeanHolder {

        static final Bean SINGLETON_BEAN = initBean();

        static {
            System.out.println("i load only once");
        }

        private static void doNothing() {
        }

    }
}
