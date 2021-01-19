package serialize;

import java.io.Serializable;

/**
 * author Xianfeng <br/>
 * date 2020/12/23 下午7:24 <br/>
 * Desc: final和序列化无关
 */
public final class Demo implements Serializable {

    //下面两种不会被序列化
    static int hiddenStatic;
    String name;
    Integer count;
    transient String hiddenTrans;

    @Override
    public String toString() {
        return "Demo{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", hiddenTrans='" + hiddenTrans + '\'' +
                '}';
    }
}
