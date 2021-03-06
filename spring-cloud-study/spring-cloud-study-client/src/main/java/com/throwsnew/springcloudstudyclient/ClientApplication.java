package com.throwsnew.springcloudstudyclient;

import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author Xianfeng <br/>
 * date 19-10-11 下午4:40 <br/>
 * Desc: 基于spring cloud的微服务的客户端
 */
@SpringBootApplication
@RestController
@EnableFeignClients
public class ClientApplication {

    /**
     * DiscoveryClient可以读取eureka上的信息
     */
    private final DiscoveryClient discoveryClient;
    /**
     * 调用其他服务
     */
    private final RestClient restClient;

    @Autowired
    public ClientApplication(DiscoveryClient discoveryClient,
            RestClient restClient) {
        this.discoveryClient = discoveryClient;
        this.restClient = restClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    /**
     * 返回从eureka上获得的信息
     *
     * @return 注册的服务名，服务实例的详细信息等
     */
    @RequestMapping(value = "/discovery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object home() {
        Map<String, Object> msg = new HashMap<>();
        msg.put("services", discoveryClient.getServices());
        msg.put("description", discoveryClient.description());
        msg.put("instances", discoveryClient.getInstances("service-server"));
        return JSON.toJSON(msg);
    }

    @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object hello() {
        Map<String, String> msg = new HashMap<>();
        msg.put("msg from server", restClient.hello());
        return JSON.toJSON(msg);
    }
}
