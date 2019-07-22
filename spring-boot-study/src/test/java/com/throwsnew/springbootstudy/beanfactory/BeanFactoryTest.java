package com.throwsnew.springbootstudy.beanfactory;

import com.throwsnew.springbootstudy.beanfactory.Application.Hello;
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

    //测试DefaultListableBeanFactory找不到这个bean
    @Test(expected = org.springframework.beans.factory.NoSuchBeanDefinitionException.class)
    public void testGetFail() {
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.getBean("hello");
    }

    //AnnotationConfigApplicationContext肯定找到这个bean
    @Test
    public void testGetBean() {
        BeanFactory beanFactory = new AnnotationConfigApplicationContext("com.throwsnew.springbootstudy.beanfactory");
        beanFactory.getBean(Hello.class);
    }

    //加载一个循环引用的bean
    @Test
    public void testRef() {
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(Application.class);
        beanFactory.getBean(ServiceA.class);
    }
}
