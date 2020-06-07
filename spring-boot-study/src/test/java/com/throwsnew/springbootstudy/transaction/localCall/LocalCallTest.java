package com.throwsnew.springbootstudy.transaction.localCall;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

/**
 * author Xianfeng <br/>
 * date 19-8-9 下午4:44 <br/>
 * Desc: 测试调用同一个类的事务方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        Application.class,
        ServiceOne.class,
        ServiceTwo.class})
@SpringBootApplication
public class LocalCallTest {

    @Autowired
    ServiceOne serviceOne;
    @Autowired
    ServiceTwo serviceTwo;
    @Autowired
    UserMapper userMapper;

    @Before
    public void clear() {
        userMapper.delete();
    }

    /**
     * 测试：直接调用事务方法，正常回滚
     */
    @Test
    public void callDirectly() {
        try {
            serviceOne.saveButFail(new User());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //回滚了save操作
        Assert.assertTrue(CollectionUtils.isEmpty(userMapper.listUser()));
    }

    /**
     * 测试：同一个类的另一个方法调用事务，事务失效
     */
    @Test
    public void callByLocal() {
        try {
            serviceOne.callSaveButFail(new User());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //save操作没有回滚
        Assert.assertTrue("应该没有回滚", !CollectionUtils.isEmpty(userMapper.listUser()));
    }

    /**
     * 测试：强制通过代理，让同一个类的另一个方法调用事务，事务成功
     */
    @Test
    public void callByLocalUseProxy() {
        try {
            serviceOne.callSaveButFailByForceProxy(new User());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //save操作回滚
        Assert.assertTrue("应该回滚", CollectionUtils.isEmpty(userMapper.listUser()));
    }

    /**
     * 测试：通过另一个类调用事务，正常回滚
     */
    @Test
    public void callByOther() {
        try {
            serviceTwo.callSaveButFail(new User());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //save操作回滚
        Assert.assertTrue("应该回滚", CollectionUtils.isEmpty(userMapper.listUser()));
    }
}
