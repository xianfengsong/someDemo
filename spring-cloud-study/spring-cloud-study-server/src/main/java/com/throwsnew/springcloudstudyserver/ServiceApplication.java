package com.throwsnew.springcloudstudyserver;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author Xianfeng <br/>
 * date 19-10-11 下午7:26 <br/>
 * Desc:
 */
@SpringBootApplication
@RestController
public class ServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceApplication.class).web(WebApplicationType.SERVLET).run(args);
    }

    @RequestMapping("/")
    public String home() {
        return "Hello world";
    }
}
