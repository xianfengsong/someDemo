package com.throwsnew.springbootstudy.transaction.propagation;

import org.springframework.transaction.annotation.Propagation;

/**
 * author Xianfeng <br/>
 * date 19-8-6 下午4:21 <br/>
 * Desc:
 */
public interface TestService {

    /**
     * 在事务中调用
     *
     * @param innerTransactionPropagation 被调用的事务方法传播级别
     * @param innerFail 内部事务是否要发生异常
     * @param outerFail 外部事务是否要发生异常
     */
    void doWithTransaction(Propagation innerTransactionPropagation, boolean innerFail, boolean outerFail);

    /**
     * 在非事务中调用
     *
     * @param innerTransactionPropagation 被调用的事务方法传播级别
     * @param innerFail 内部事务是否要发生异常
     * @param outerFail 外部事务是否要发生异常
     */
    void doWithoutTransaction(Propagation innerTransactionPropagation, boolean innerFail, boolean outerFail);

}
