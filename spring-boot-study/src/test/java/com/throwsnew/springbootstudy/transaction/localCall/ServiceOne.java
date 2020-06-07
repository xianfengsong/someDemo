package com.throwsnew.springbootstudy.transaction.localCall;

import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author Xianfeng <br/>
 * date 19-8-9 下午4:26 <br/>
 * Desc:
 */
@Service
public class ServiceOne {

    @Autowired
    UserMapper userMapper;
    @Autowired
    private ApplicationContext applicationContext;

    @Transactional()
    public void saveButFail(User user) {
        userMapper.insert(user);
        throw new RuntimeException("");
    }

    public void callSaveButFail(User user) {
        saveButFail(user);
    }

    /**
     * 调用saveButFail前先创建个代理，用代理调用
     */
    public void callSaveButFailByForceProxy(User user) {
        //就是要用代理。。。。
        this.applicationContext.getBean(ServiceOne.class).saveButFail(user);
    }
}
