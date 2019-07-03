package com.throwsnew.pattern.observer.builtin;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Observer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 18-12-21 下午6:05 <br/>
 * Desc: 测试java内置的观察者模式实现类
 */
public class JavaBuiltinObserverTest {

    /**
     * 为了验证输出内容
     **/
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setStream() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStream() {
        System.setOut(new PrintStream(System.out));
    }

    @Test
    public void test() {
        //体育记者
        Reporter sportReporter = new Reporter("sport");
        //香港记者（跑的更快）
        Reporter hkReporter = new Reporter("Hong Kong");

        News newsOne = new News("sport", "sport news");
        NewsCenter newsCenter = new NewsCenter();

        newsCenter.addObserver(sportReporter);
        newsCenter.addObserver(hkReporter);

        newsCenter.setNews(newsOne);
        Assert.assertEquals("sport news", outContent.toString());
    }

    @Test
    public void block() {
        Reporter reporter = new Reporter("sport");
        //update方法如果是同步执行 会阻塞后面的观察者
        Observer blockObserver = (o, arg) -> {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        NewsCenter newsCenter = new NewsCenter();
        newsCenter.addObserver(blockObserver);
        newsCenter.addObserver(reporter);

        News newsOne = new News("sport", "sport news");
        Long start = System.currentTimeMillis();
        newsCenter.setNews(newsOne);
        Long time = System.currentTimeMillis() - start;
        Assert.assertTrue(time >= 3000L);
    }

}
