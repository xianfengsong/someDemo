package com.throwsnew.disruptordemo.getstart;

/**
 * author Xianfeng <br/>
 * date 19-6-13 上午10:51 <br/>
 * Desc: 最简单的事件
 */
public class LongEvent {

    private Long value;

    public void set(long value) {
        this.value = value;
    }

    //清除
    public void clear() {
        this.value = null;
    }
}
