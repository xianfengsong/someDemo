package com.throwsnew.pattern.proxy;

/**
 * author Xianfeng <br/>
 * date 19-6-10 上午11:07 <br/>
 * Desc: 被代理的接口
 */
public interface Service {

    String serve();

    default Integer serve(int a, int b) {
        return 0;
    }

    ;
}
