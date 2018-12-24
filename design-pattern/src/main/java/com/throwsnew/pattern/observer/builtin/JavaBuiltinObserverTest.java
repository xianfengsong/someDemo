package com.throwsnew.pattern.observer.builtin;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

        Reporter sportReporter = new Reporter("sport");
        Reporter hkReporter = new Reporter("Hong Kong");

        News newsOne = new News("sport", "sport news");
        NewsCenter newsCenter = new NewsCenter();

        newsCenter.addObserver(sportReporter);
        newsCenter.addObserver(hkReporter);

        newsCenter.setNews(newsOne);
        Assert.assertEquals("sport news", outContent.toString());
    }

}
