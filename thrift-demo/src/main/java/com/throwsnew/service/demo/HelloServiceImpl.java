package com.throwsnew.service.demo;

import com.throwsnew.thriftdemo.api.HelloService;
import org.apache.thrift.TException;

public class HelloServiceImpl implements HelloService.Iface {

    @Override
    public String helloString(String para) throws TException {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("helloString para=" + para);
        return "Hello !" + para;
    }

    @Override
    public int helloInt(int para) throws TException {
        System.out.println("helloInt=" + para);
        return 0;
    }

    @Override
    public boolean helloBoolean(boolean para) throws TException {
        return false;
    }

    @Override
    public void helloVoid() throws TException {
        System.out.println("helloVoid!");
    }

    @Override
    public String helloNull() throws TException {
        return null;
    }
}
