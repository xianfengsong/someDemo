package com.throwsnew.pattern.factory.factorymethod.room;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午4:00 <br/>
 * Desc: product 2
 */
public class OrdinaryRoom extends Room {

    @Override
    public void connect(Room room) {
        System.out.println("so ordinary");
    }
}
