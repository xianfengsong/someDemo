package com.throwsnew.hystrix.study.basic;

import com.throwsnew.hystrix.study.basic.command.CommandHelloWorld;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 18-10-10 下午4:42 <br/>
 * Desc:
 */
public class HelloWorldCommandTest {

    @Test
    public void test() {
        String s = new CommandHelloWorld("Frank").execute();
        Assert.assertEquals(s, "Hello Frank!");
    }

    @Test
    public void testFallback() {
        String r = new CommandHelloWorld("Frank", true).execute();
        Assert.assertEquals(r, "Fallback result");
    }

}
