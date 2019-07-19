package com.throwsnew.pattern.factory.factorymethod.maze;

import com.throwsnew.pattern.factory.factorymethod.room.MagicRoom;
import com.throwsnew.pattern.factory.factorymethod.room.Room;

/**
 * author Xianfeng <br/>
 * date 19-7-18 下午3:59 <br/>
 * Desc:
 */
public class MagicMazeGame extends MazeGame {

    @Override
    protected Room makeRoom() {
        return new MagicRoom();
    }
}
