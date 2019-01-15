package io.niotest.reactor.handler;

/**
 * author Xianfeng <br/>
 * date 19-1-15 下午8:23 <br/>
 * Desc:
 */
public interface State {

    void handle(IOHandler ioHandler);
}
