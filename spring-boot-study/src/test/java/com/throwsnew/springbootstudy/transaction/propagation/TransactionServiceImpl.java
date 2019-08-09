package com.throwsnew.springbootstudy.transaction.propagation;

import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * author Xianfeng <br/>
 * date 19-8-6 下午4:15 <br/>
 * Desc:
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    UserMapper userMapper;

    private void doInsert(boolean withException) {
        userMapper.insert(new User(1, "A"));
        if (withException) {
            throw new RuntimeException("内部事务异常");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, timeout = 100)
    public void required(boolean withException) {
        doInsert(withException);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void support(boolean withException) {
        doInsert(withException);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void mandatory(boolean withException) {
        doInsert(withException);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNew(boolean withException) {
        doInsert(withException);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void notSupport(boolean withException) {
        doInsert(withException);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void nested(boolean withException) {
        doInsert(withException);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void never(boolean withException) {
        doInsert(withException);
    }
}
