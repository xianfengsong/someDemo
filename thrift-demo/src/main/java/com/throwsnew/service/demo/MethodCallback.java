package com.throwsnew.service.demo;

import org.apache.thrift.async.AsyncMethodCallback;

/**
 * 异步客户端的回调处理类
 */
public class MethodCallback implements AsyncMethodCallback {

    //todo 不添加volatile 可能读取不到response的内容 说明执行回调的是另一个线程？
    Object response = null;

    public Object getResponse() {
        return response;
    }


    @Override
    public void onComplete(Object o) {
        response = o;
        System.out.println("调用结果返回");
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
