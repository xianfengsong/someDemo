package com.throwsnew.pattern.factory.factorymethod;

import com.throwsnew.pattern.factory.factorymethod.maze.MagicMazeGame;
import com.throwsnew.pattern.factory.factorymethod.maze.MazeGame;
import com.throwsnew.pattern.factory.factorymethod.maze.OrdinaryMazeGame;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午4:03 <br/>
 * Desc:
 *
 * 工厂方法模式
 * https://en.wikipedia.org/wiki/Factory_method_pattern
 *
 * 目的：
 * 分离对象的构造和使用，把ObjectX使用的对象的实例化延迟到ObjectX的子类完成
 * 并且子类可以决定实例化哪个类型的对象
 * 实现：
 * 定义一个抽象方法或者接口 factory method 来完成被引用对象的实例化
 * 然后由子类来实现factory method决定初始化哪类对象
 */
public class FactoryMethodDemo {

    @Test
    public void playMazeGame() {
        //初始化交给MazeGame的实现类完成
        //利用继承的多态创建不同类型的Room
        MazeGame ordinaryGame = new OrdinaryMazeGame();
        MazeGame magicGame = new MagicMazeGame();
    }
}
