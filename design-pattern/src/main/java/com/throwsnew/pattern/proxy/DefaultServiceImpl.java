package com.throwsnew.pattern.proxy;

/**
 * author Xianfeng <br/>
 * date 19-6-10 上午11:09 <br/>
 * Desc:
 */
public class DefaultServiceImpl implements Service {

    @Override
    public String serve() {
        return "default serve you";
    }

    @Override
    public Integer serve(int a, int b) {
        System.out.println("serve");
        return a + b;
    }
}
