package com.throwsnew.service.demo;

import org.apache.thrift.async.AsyncMethodCallback;

/**
 * 异步客户端的回调处理类
 * 不指定具体方法，返回通用的response，调用时转型成对应方法
 */
public class CommonCallback implements AsyncMethodCallback {

    //todo 不添加volatile 可能读取不到response的内容 说明执行回调的是另一个线程？
    private Object response = null;

    public Object getResponse() {
        return response;
    }

    @Override
    public void onComplete(Object response) {
        this.response = response;
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
