package com.throwsnew.springbootstudy.mysql.propagation;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

/**
 * author Xianfeng <br/>
 * date 19-8-6 下午5:33 <br/>
 * Desc: 自定义事务管理器，除了打印日志什么都不做
 */
public class MyTransactionManager extends AbstractPlatformTransactionManager {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    @Override
    protected Object doGetTransaction() throws TransactionException {
        return new SimpleTransactionStatus();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        System.out.println(ANSI_RED + definition.getName() + " Begin!" + ANSI_RESET);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        System.out.println(ANSI_RED + "!doCommit!" + ANSI_RESET);

    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        System.out.println(ANSI_RED + "!doRollback!" + ANSI_RESET);
    }
}
