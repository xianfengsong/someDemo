package com.throwsnew.pattern.factory.factorymethod.room;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午3:51 <br/>
 * Desc: 相当于product
 */
public abstract class Room {

    /**
     * 和另一个房间相连
     */
    public abstract void connect(Room room);
}
