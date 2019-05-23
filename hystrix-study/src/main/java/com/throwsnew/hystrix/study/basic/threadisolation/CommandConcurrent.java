package com.throwsnew.hystrix.study.basic.threadisolation;

import com.netflix.hystrix.HystrixCommand;

/**
 * author Xianfeng <br/>
 * date 19-5-23 下午6:06 <br/>
 * Desc:
 */
public class CommandConcurrent extends HystrixCommand<String> {

    private int latency;

    public CommandConcurrent(Setter setter, int latency) {
        super(setter);
        this.latency = latency;
    }

    @Override
    protected String run() throws Exception {
        try {
            Thread.sleep(latency);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }


}
