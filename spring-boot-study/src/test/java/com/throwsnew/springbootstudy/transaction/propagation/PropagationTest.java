package com.throwsnew.springbootstudy.transaction.propagation;

import com.throwsnew.springbootstudy.accessdata.Application;
import com.throwsnew.springbootstudy.accessdata.mysql.mapper.UserMapper;
import com.throwsnew.springbootstudy.accessdata.mysql.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;

/**
 * author Xianfeng <br/>
 * date 19-8-6 下午5:55 <br/>
 * Desc: 测试spring事务的传播类型
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        Application.class,
        TransactionServiceImpl.class,
        TestServiceImpl.class})
@SpringBootApplication
public class PropagationTest {

    @Autowired
    TestService testService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    TransactionService transactionService;

    @Before
    public void clear() {
        userMapper.delete();
    }

    //外事务是否回滚
    private void outerRollback(boolean rollback) {
        boolean inserted = false;
        for (User u : userMapper.listUser()) {
            System.out.println("outerRollback \t" + u);
            if ("B".equals(u.getName())) {
                inserted = true;
            }
        }
        if (rollback) {
            Assert.assertTrue("外部事务应该没有插入数据", inserted == false);
        } else {
            Assert.assertTrue("外部事务应该插入数据", inserted == true);
        }
    }

    //内事务是否回滚
    private void innerRollback(boolean rollback) {
        boolean inserted = false;
        for (User u : userMapper.listUser()) {
            System.out.println("innerRollback \t" + u);
            if ("A".equals(u.getName())) {
                inserted = true;
            }
        }
        if (rollback) {
            Assert.assertTrue("内部事务应该没有插入数据", inserted == false);
        } else {
            Assert.assertTrue("内部事务应该插入数据", inserted == true);
        }
    }
    //-------测试事务传播级别------

    /**
     * MANDATORY 必须在事务内调用
     * 测试:会发生异常
     */
    @Test(expected = org.springframework.transaction.IllegalTransactionStateException.class)
    public void testMandatory() {
        testService.doWithoutTransaction(Propagation.MANDATORY, false, false);
    }

    //-------- REQUIRED级别  --------
    // 如果不在事务中执行，就使用自己的事务，如果在事务中，就用外部事务用一个事务

    /**
     * 测试REQUIRED：外部没有事务，内部事务异常，可以正常回滚
     */
    @Test
    public void testRequireWithoutTransaction() {
        try {
            testService.doWithoutTransaction(Propagation.REQUIRED, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(true);
    }

    /**
     * 测试REQUIRED：外部事务异常，内部事务也被回滚
     */
    @Test
    public void testRequireWithOuterTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.REQUIRED, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outerRollback(true);
        innerRollback(true);
    }

    /**
     * 测试REQUIRED
     * 如果内部事务执行异常并回滚，外部事务就算捕获异常也会回滚
     * 并且发生 UnexpectedRollbackException 异常
     */
    @Test
    public void testRequireWithInnerTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.REQUIRED, true, false);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof UnexpectedRollbackException);
            e.printStackTrace();
        }
        innerRollback(true);
        outerRollback(true);
    }

    //-------- REQUIRES_NEW级别  --------
    //如果已经在事务中则挂起（只对Jta事务)，并且也不用外部事务，用自己的事务

    /**
     * REQUIRES_NEW
     * 测试：和REQUIRES不同，内部事务rollback,不会影响外部事务commit & 外部事务不会挂起
     */
    @Test
    public void testRequireNewWithInnerTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.REQUIRES_NEW, true, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        innerRollback(true);
        outerRollback(false);
    }

    /**
     * REQUIRES_NEW
     * 测试：外部事务异常，内部事务不会被回滚
     */
    @Test
    public void testRequireNewWithOuterTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.REQUIRES_NEW, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outerRollback(true);
        innerRollback(false);
    }

    /**
     * REQUIRES_NEW
     * 测试：不在事务中运行时，内部事务使用自己的事务，异常会回滚
     */
    @Test
    public void testRequireNewWithoutTransaction() {
        try {
            testService.doWithoutTransaction(Propagation.REQUIRES_NEW, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(true);
        outerRollback(false);
    }

    //-------- NESTED  --------
    //如果在事务中，以“嵌套”的方式执行，内部事务回滚不影响外部事务，但是外部事务回滚会影响内部事务
    //一般只被DataSourceTransactionManager支持，并且是使用 JDBC 3.0 driver 时

    /**
     * NESTED
     * 测试 外部事务异常回滚，内部事务也被回滚 和REQUIRED一样啊
     */
    @Test
    public void testNestedWithOuterTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.NESTED, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outerRollback(true);
        innerRollback(true);
    }

    /**
     * NESTED
     * 测试 内部事务异常回滚，外部事务不会被回滚 和REQUIRED_NEW一样啊
     */
    @Test
    public void testNestedWithInnerTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.NESTED, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(true);
        outerRollback(false);
    }

    /**
     * NESTED
     * 测试：不在事务中运行时，内部事务使用自己的事务，异常会回滚
     */
    @Test
    public void testNestedWithoutTransaction() {
        try {
            testService.doWithoutTransaction(Propagation.NESTED, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(true);
    }

    //-------- SUPPORTS级别  --------
    //如果当前没有事务，就以非事务方式运行，如果在事务中，就用外部事务

    /**
     * 测试SUPPORTS
     * 测试： 因为当前没有事务，内部嵌套的事务不生效， 没有回滚
     */
    @Test
    public void testSupportWithoutTransaction() {
        try {
            testService.doWithoutTransaction(Propagation.SUPPORTS, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(false);
    }

    /**
     * SUPPORTS
     * 测试 : 内部事务异常回滚，外部事务会被回滚 和REQUIRED一样啊
     */
    @Test
    public void testSupportsWithInnerTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.SUPPORTS, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(true);
        outerRollback(true);
    }

    /**
     * 测试SUPPORTS
     * 测试： 因为当前有事务，外部事务回滚，内部嵌套的事务也被回滚
     */
    @Test
    public void testSupportWithOuterTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.SUPPORTS, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outerRollback(true);
        innerRollback(true);
    }

    //-------- NOT_SUPPORTED  --------
    //永远以非事务方式运行，如果当前存在事务，则把当前事务挂起(只对于JTA事务)。感觉NOT_SUPPORTED没啥用

    /**
     * NOT_SUPPORTED
     * 在事务中运行，内部事务异常也不会回滚，外部事务catch了异常，也不会回滚
     * 更不会挂起
     */
    @Test
    public void testNotSupportWithInnerTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.NOT_SUPPORTED, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(false);
        outerRollback(false);
    }

    /**
     * NOT_SUPPORTED
     * 在事务中运行，外部事务异常回滚，但不会回滚内部事务
     */
    @Test
    public void testNotSupportWithOuterTransactionFail() {
        try {
            testService.doWithTransaction(Propagation.NOT_SUPPORTED, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerRollback(false);
        outerRollback(true);
    }

    //-------- NEVER  --------
    //永远以非事务方式运行，如果在事务中就报错 IllegalTransactionStateException
    @Test(expected = IllegalTransactionStateException.class)
    public void testNeverWithTransaction() {
        testService.doWithTransaction(Propagation.NEVER, false, false);
    }


}
