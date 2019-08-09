package com.throwsnew.springbootstudy.transaction.propagation;

import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * author Xianfeng <br/>
 * date 19-8-6 下午5:08 <br/>
 * Desc:
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    TransactionService innerTransactionService;
    @Autowired
    UserMapper userMapper;

    //调用TransactionService的事务方法，并catch异常
    private void callInnerTransactionService(Propagation propagation, boolean innerFail, boolean outerFail) {
        //不要受内部事务异常影响
        try {
            switch (propagation) {
                case REQUIRED:
                    innerTransactionService.required(innerFail);
                    break;
                case SUPPORTS:
                    innerTransactionService.support(innerFail);
                    break;
                case MANDATORY:
                    innerTransactionService.mandatory(innerFail);
                    break;
                case REQUIRES_NEW:
                    innerTransactionService.requiresNew(innerFail);
                    break;
                case NOT_SUPPORTED:
                    innerTransactionService.notSupport(innerFail);
                    break;
                case NEVER:
                    innerTransactionService.never(innerFail);
                    break;
                case NESTED:
                    innerTransactionService.nested(innerFail);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //为了测试
            if (e instanceof IllegalTransactionStateException) {
                throw e;
            }
        }
        //保证插入user B
        userMapper.insert(new User(2, "B"));
        //让当前方法执行异常
        if (outerFail) {
            throw new RuntimeException("外部事务异常");
        }
    }

    @Transactional
    @Override
    public void doWithTransaction(Propagation innerTransactionPropagation, boolean innerFail, boolean outerFail) {
        callInnerTransactionService(innerTransactionPropagation, innerFail, outerFail);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                System.out.println("外部事务 commit");
            }
        });
    }

    @Override
    public void doWithoutTransaction(Propagation innerTransactionPropagation, boolean innerFail, boolean outerFail) {
        callInnerTransactionService(innerTransactionPropagation, innerFail, outerFail);
    }
}
