package com.throwsnew.springbootstudy.mysql.propagation;

/**
 * author Xianfeng <br/>
 * date 19-8-6 下午4:12 <br/>
 * Desc:
 */
public interface TransactionService {

    void required(boolean withException);

    void support(boolean withException);

    void mandatory(boolean withException);

    void requiresNew(boolean withException);

    void notSupport(boolean withException);

    void nested(boolean withException);

    void never(boolean withException);
}
