package com.throwsnew.springbootstudy.mysql;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import com.throwsnew.springbootstudy.accessdata.mysql.service.UserService;
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

@SpringBootTest(classes = Application.class)
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
            userService.updateUsers(userList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        userList = userService.listUser();

        Assert.assertEquals(2, userList.size());
    }
}
