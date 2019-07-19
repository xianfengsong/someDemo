package com.throwsnew.springbootstudy.beanfactory;

import com.throwsnew.springbootstudy.beanfactory.Application.Hello;
import java.io.IOException;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午6:00 <br/>
 * Desc:
 */
@SpringBootTest(classes = Application.class)
public class BeanFactoryTest {

    //肯定找不到这个bean啊
    @Test(expected = org.springframework.beans.factory.NoSuchBeanDefinitionException.class)
    public void testGetFail() {
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.getBean("hello");
    }

    //肯定找到这个bean啊
    @Test
    public void testGetBean() {
        BeanFactory beanFactory = new AnnotationConfigApplicationContext("com.throwsnew.springbootstudy.beanfactory");
        beanFactory.getBean(Hello.class);
    }

    @Test
    public void run() throws IOException {
        Runtime.getRuntime().exec("gedit");
    }

}
