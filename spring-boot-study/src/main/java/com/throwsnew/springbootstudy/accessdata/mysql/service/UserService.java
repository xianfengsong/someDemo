package com.throwsnew.springbootstudy.accessdata.mysql.service;

import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import java.util.List;

/**
 * author Xianfeng <br/>
 * date 19-7-31 下午4:04 <br/>
 * Desc:
 */
public interface UserService {

    List<User> listUser();

    /**
     * 使用只读事务的list方法
     */
    List<User> listUserReadOnly();

    void insert(List<User> userList);

    void updateUsers(List<User> userList);

    /**
     * 更新并抛出异常
     */
    void updateUsersFail(List<User> userList);

    void delete();

}
