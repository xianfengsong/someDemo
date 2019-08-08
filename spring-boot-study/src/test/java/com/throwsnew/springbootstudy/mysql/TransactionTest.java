package com.throwsnew.springbootstudy.mysql;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import com.throwsnew.springbootstudy.accessdata.mysql.service.UserService;
import com.throwsnew.springbootstudy.mysql.propagation.MyTransactionManager;
import com.throwsnew.springbootstudy.mysql.propagation.TestServiceImpl;
import com.throwsnew.springbootstudy.mysql.propagation.TransactionServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * author Xianfeng <br/>
 * date 19-7-31 下午4:12 <br/>
 * Desc:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TransactionServiceImpl.class, TestServiceImpl.class,
        MyTransactionManager.class})
public class TransactionTest {

    @Autowired
    UserService userService;

    @Before
    public void clear() {
        userService.delete();
    }

    @Test
    public void testRollback() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(10, "10"));
        userList.add(new User(20, "20"));
        userService.insert(userList);

        userList.add(new User(30, "30"));
        try {
            userService.updateUsersFail(userList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        userList = userService.listUser();

        Assert.assertEquals(2, userList.size());
    }

    /**
     * 测试只读事务
     * 读取的数据始终是事务开始时的状态
     */
    @Test
    public void testReadOnly() throws InterruptedException {
        //初始化两条
        List<User> userList = new ArrayList<>();
        userList.add(new User(10, "10"));
        userList.add(new User(20, "20"));
        userService.insert(userList);
        //删除操作
        Thread t = new Thread(() -> userService.delete());
        List<User> usersSnapshot = userService.listUserReadOnly();
        //只读事务执行过程中，执行删除
        t.start();
        t.join();
        //只读事务读取的结果和删除前一致
        Assert.assertEquals(userList.size(), usersSnapshot.size());
        //再次查询，数据已被删除
        Assert.assertEquals("再次查询数据应该已经改变", 0, userService.listUser().size());
    }

}
