package com.throwsnew.springcloudstudyclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * author Xianfeng <br/>
 * date 19-10-12 上午10:19 <br/>
 * Desc: 使用rest api的方式调用eureka上注册的服务
 * FeignClient name要填被调用的程序的ApplicationName
 */
@FeignClient("service-server")
public interface RestClient {

    @GetMapping("/")
    String hello();
}
