package com.throwsnew.springbootstudy.accessdata.mysql.service;

import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author Xianfeng <br/>
 * date 19-7-31 下午4:05 <br/>
 * Desc:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> listUser() {
        return userMapper.listUser();
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
        //抛出异常
        Integer i = Integer.valueOf("null");
    }

    @Override
    public void delete() {
        userMapper.delete();
    }
}
