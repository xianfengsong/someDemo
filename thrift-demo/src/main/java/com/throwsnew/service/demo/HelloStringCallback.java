package com.throwsnew.service.demo;

import com.throwsnew.thriftdemo.api.HelloService.AsyncClient.helloString_call;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

/**
 * author Xianfeng <br/>
 * date 19-4-11 上午10:49 <br/>
 * Desc: helloString_call的回调方法
 */
public class HelloStringCallback implements AsyncMethodCallback<helloString_call> {

    //todo 不添加volatile 可能读取不到response的内容 说明执行回调的是另一个线程？
    private String response = null;

    String getResponse() {
        return response;
    }


    @Override
    public void onComplete(helloString_call response) {
        try {
            this.response = response.getResult();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}

