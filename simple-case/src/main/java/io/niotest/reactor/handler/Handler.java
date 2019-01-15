package io.niotest.reactor.handler;

/**
 * author Xianfeng <br/>
 * date 19-1-15 下午8:28 <br/>
 * Desc:
 */
public interface Handler {

    /**
     * 处理当前连接事件
     */
    void run();
}
