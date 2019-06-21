package com.throwsnew.pattern.singleton;

/**
 * author Xianfeng <br/>
 * date 19-6-21 下午5:50 <br/>
 * Desc:
 */
public class Bean {

    String field;

    public Bean(String field) {
        this.field = field;
    }

    public Bean(String field, boolean slowlyInit) {
        this.field = field;
        if (slowlyInit) {
            byte[] data = new byte[1024 * 1024 * 64];
        }
    }

    @Override
    public String toString() {
        return "Bean{" +
                "field='" + field + '\'' +
                '}';
    }
}
