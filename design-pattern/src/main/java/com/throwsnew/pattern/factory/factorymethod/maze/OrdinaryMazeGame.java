package com.throwsnew.pattern.factory.factorymethod.maze;

import com.throwsnew.pattern.factory.factorymethod.room.OrdinaryRoom;
import com.throwsnew.pattern.factory.factorymethod.room.Room;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午4:01 <br/>
 * Desc:
 */
public class OrdinaryMazeGame extends MazeGame {

    @Override
    protected Room makeRoom() {
        return new OrdinaryRoom();
    }
}
