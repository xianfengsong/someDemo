package com.throwsnew.pattern.factory.factorymethod.room;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午3:59 <br/>
 * Desc: product 1
 */
public class MagicRoom extends Room {

    @Override
    public void connect(Room room) {
        System.out.println("so magic");
    }
}
