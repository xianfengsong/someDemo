package com.throwsnew.disruptordemo.getstart;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.nio.ByteBuffer;

/**
 * author Xianfeng <br/>
 * date 19-6-13 上午10:50 <br/>
 * Desc:
 * 从官网拷贝的demo：https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started
 * 一个发布事件，一个处理
 */
public class LongEventMain {

    /**
     * 处理事件
     */
    public static void handleEvent(LongEvent event, long sequence, boolean endOfBatch) {
        System.out.println(event);
        Thread.dumpStack();
    }

    /**
     * buffer中的数据变成event
     */
    public static void translate(LongEvent event, long sequence, ByteBuffer buffer) {
        event.set(buffer.getLong(0));
        Thread.dumpStack();
    }

    public static void main(String[] args) throws Exception {
        //ring buffer的大小，必须是2的n次幂
        int bufferSize = 1024;

        //创建Disruptor，参数如下：
        //LongEvent::new - the factory to create events in the ring buffer.
        //bufferSize - the size of the ring buffer.
        //DaemonThreadFactory.INSTANCE - a ThreadFactory to create threads to for processors.
        //ProducerType.SINGLE 单生产者
        //BlockingWaitStrategy 待定策略是用锁和条件阻塞，适用于不要求低延迟和吞吐量的场景
        Disruptor<LongEvent> disruptor = new Disruptor<>(
                LongEvent::new, bufferSize, DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE, new BlockingWaitStrategy());

        disruptor.handleEventsWith(LongEventMain::handleEvent)
                .then(new ClearingEventHandler());
        disruptor.start();

        //获得ringBuffer,来发布事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            ringBuffer.publishEvent(LongEventMain::translate, bb);
            Thread.sleep(10000);
        }
    }

    /**
     * ringbuffer中的event可能存在很长事件，通过在最后添加ClearingEventHandler清理
     */
    public static class ClearingEventHandler<T> implements EventHandler<LongEvent> {

        @Override
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
            // Failing to call clear here will result in the
            // object associated with the event to live until
            // it is overwritten once the ring buffer has wrapped
            // around to the beginning.
            event.clear();
        }
    }
}
