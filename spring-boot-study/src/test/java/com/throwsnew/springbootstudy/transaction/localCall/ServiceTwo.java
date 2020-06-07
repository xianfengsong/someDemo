package com.throwsnew.springbootstudy.transaction.localCall;

import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author Xianfeng <br/>
 * date 19-8-9 下午4:26 <br/>
 * Desc:
 */
@Service
public class ServiceTwo {

    @Autowired
    ServiceOne serviceOne;
    @Autowired
    UserMapper userMapper;

    public void callSaveButFail(User user) {
        serviceOne.saveButFail(user);
    }

    @Transactional
    public void t() {
        userMapper.delete();
    }
}
