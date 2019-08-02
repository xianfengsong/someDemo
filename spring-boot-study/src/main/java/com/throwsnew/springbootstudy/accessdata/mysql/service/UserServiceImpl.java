package com.throwsnew.springbootstudy.accessdata.mysql.service;

import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * author Xianfeng <br/>
 * date 19-7-31 下午4:05 <br/>
 * Desc:
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> listUser() {
        return userMapper.listUser();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listUserReadOnly() {
        //暂停3秒 再次查询结果不变
        List<User> usersBefore = userMapper.listUser();
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<User> usersAfter = userMapper.listUser();
        Assert.isTrue(usersAfter.size() == usersBefore.size());
        Assert.isTrue(usersAfter.get(0).getId().equals(usersBefore.get(0).getId()));
        return usersAfter;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void insert(List<User> userList) {
        for (User u : userList) {
            userMapper.insert(u);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateUsers(List<User> userList) {
        userMapper.delete();
        for (User user : userList) {
            userMapper.insert(user);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateUsersFail(List<User> userList) {
        userMapper.delete();
        for (User user : userList) {
            userMapper.insert(user);
        }
        //抛出异常
        Integer i = Integer.valueOf("null");
    }

    @Override
    public void delete() {
        userMapper.delete();
    }
}
