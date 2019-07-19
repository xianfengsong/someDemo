package com.throwsnew.pattern.factory.factorymethod.maze;

import com.throwsnew.pattern.factory.factorymethod.room.Room;
import java.util.ArrayList;
import java.util.List;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午3:51 <br/>
 * Desc:
 * 迷宫游戏 需要引用对象Room,Room的初始化由工厂方法makeRoom完成
 */
public abstract class MazeGame {

    private final List<Room> rooms = new ArrayList<>();

    /**
     * 这个构造函数是一个模板方法，定义了所有game的初始化模板
     */
    public MazeGame() {
        Room room1 = makeRoom();
        Room room2 = makeRoom();
        room1.connect(room2);
        rooms.add(room1);
        rooms.add(room2);
    }

    /**
     * 工厂方法，把Room的实例化操作给子类完成
     *
     * @return room
     */
    abstract protected Room makeRoom();
}
