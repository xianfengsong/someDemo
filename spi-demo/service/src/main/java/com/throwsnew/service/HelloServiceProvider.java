package com.throwsnew.service;

/**
 * author Xianfeng <br/>
 * date 19-3-27 下午4:11 <br/>
 * Desc:
 */
public interface HelloServiceProvider {

    /**
     * 返回语言对应的HelloService
     */
    HelloService getHelloService(String language);
}
