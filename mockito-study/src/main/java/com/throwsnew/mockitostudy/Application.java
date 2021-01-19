package com.throwsnew.mockitostudy;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * author Xianfeng <br/>
 * date 19-12-5 下午2:18 <br/>
 * Desc: 演示 mockito api 的基本使用方式
 */
@RunWith(MockitoJUnitRunner.class)
public class Application {

    @Mock
    private Engine engine;
    /**
     * mockito 不能直接注入集合
     */
    @Mock
    private Wheel w1, w2, w3, w4;
    @InjectMocks
    private Car car;

    /**
     * mockito 会记住你对mock对象的交互
     * verify 会检查某个行为是否发生
     */
    @Test
    @SuppressWarnings("unchecked")
    public void verifyAction() {
        List mockList = mock(List.class);
        mockList.add("one");
        mockList.clear();

        verify(mockList).add("one");
        verify(mockList).clear();
    }

    /**
     * 为mock对象生成stub,可以控制mock对象的行为
     */
    @Test(expected = Exception.class)
    @SuppressWarnings("unchecked")
    public void stubbing() {
        LinkedList list = mock(LinkedList.class);
        //生成stub
        when(list.get(0)).thenReturn("first");
        when(list.get(1)).thenThrow(IndexOutOfBoundsException.class);
        //对于返回void的方法，要使用doXXX方法生成存根
        doThrow(new RuntimeException()).when(list).clear();
        doReturn("x").when(list).addFirst("x");

        System.out.println(list.get(0));
        System.out.println(list.get(1));
        //未生成stub,返回空
        System.out.println(list.get(2));
        list.clear();
    }

    /**
     * 使用ArgumentMatcher，提供更丰富的stub方式，支持各种参数
     */
    @Test
    public void argMatcher() {
        LinkedList list = mock(LinkedList.class);
        //list任何索引都返回element
        when(list.get(ArgumentMatchers.anyInt())).thenReturn("element");
        //使用自定义的argMatcher, 只要参数不是null,返回true
        when(list.contains(argThat(Objects::nonNull))).thenReturn(true);
        Assert.assertEquals("element", list.get(11111));
    }
//    @Mock
//    private List<Wheel> wheels;

    /**
     * 使用verify还可以验证方法执行次数
     */
    @Test
    @SuppressWarnings("unchecked")
    public void verifyTimes() {
        LinkedList list = mock(LinkedList.class);
        list.add("1");
        list.add("2");
        list.add("2");
        //验证执行了指定次数
        verify(list, times(3)).add(anyString());
        verify(list, times(2)).add("2");
        verify(list, times(1)).add("1");
        //验证从未执行
        verify(list, never()).add("3");
        //验证执行次数范围
        verify(list, atLeastOnce()).add("1");
        verify(list, atLeast(2)).add("2");
        verify(list, atMost(2)).add("2");
    }

    /**
     * 使用spy默认会执行对象原始方法，除非主动stub
     * 原始方法同样能被验证
     */
    @Test
    public void spyObject() {
        //必须自己初始化对象
        List list = new LinkedList();
        List spy = spy(list);
        //stub之后不会执行原始方法
        when(spy.size()).thenReturn(100);

        //using the spy calls real methods
        spy.add("one");
        spy.add("two");
        doReturn("222").when(spy).get(1);

        //执行原始方法，返回one
        System.out.println(spy.get(0));
        //返回stub
        System.out.println(spy.get(1));

        //size() method was stubbed - 100 is printed
        System.out.println(spy.size());

        //optionally, you can verify
        verify(spy).add("one");
        verify(spy).add("two");
    }

    /**
     * 使用@InjectMocks 为mock的对象注入mock依赖
     * 有三种方式启动注入：
     * 1. 添加注解 @RunWith(MockitoJUnitRunner.class)
     * 2. 添加属性 @Rule public MockitoRule rule = MockitoJUnit.rule();
     * 3. 调用方法 MockitoAnnotations.initMocks(this)
     */
    @Test
    public void autoInject() {
        List<Wheel> wheels = Arrays.asList(w1, w2, w3, w4);
        car.setWheels(wheels);
        car.drive();
        verify(engine).start();
        for (Wheel wh : wheels) {
            verify(wh).rolling();
        }
    }
}

class Engine {

    void start() {
    }
}

class Wheel {

    void rolling() {
    }
}

class Car {

    private Engine engine;
    private List<Wheel> wheels;

    public Car(Engine engine) {
        this.engine = engine;
    }

    public void setWheels(List<Wheel> wheels) {
        this.wheels = wheels;
    }

    void drive() {
        engine.start();
        for (Wheel wh : wheels) {
            wh.rolling();
        }
    }
}
