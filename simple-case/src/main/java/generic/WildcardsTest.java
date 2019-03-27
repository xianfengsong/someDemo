package generic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;

/**
 * author Xianfeng <br/>
 * date 18-10-30 下午5:02 <br/>
 * Desc:
 */
public class WildcardsTest {

    /**
     * 使用通配符 设置类型上限
     * 不可以写入
     *
     * @param list 任何Number类或子类
     */
    private static double sumOfList1(List<? extends Number> list) {
        double sum = 0.0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        //编译失败
//        list.add(list.get(0));
        //可以保存null
        list.add(null);
        //可以清空
        list.clear();
        return sum;
    }

    /**
     * 使用泛型标识符 可以设置多个限定条件(类 & 接口1 & 接口2)
     */
    private static <T extends Number & Comparable> double sumOfList2(List<T> list) {
        double sum = 0.0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        return sum;
    }

    /**
     * 使用通配符在配和super(下界)时 参数可以读&写
     * 但是，不能写入Integer父类对象
     *
     * @param list 任何Integer父类或Integer类
     */
    static int writeParam1(List<? super Integer> list) {
        list.add(new Integer(0));
        //编译失败
//        list.add((Number)0);
        //读取时都需要转型，Object不需要
        Object o = list.get(0);
        Integer one = (Integer) list.get(0);
        Number number = (Number) list.get(0);
        System.out.println(number);
        return list.size();
    }

    /**
     * 使用泛型标识符 参数可以读&写
     */
    static <T extends Number> int writeParam2(List<T> list, T add) {
        list.add(add);
        return list.size();
    }


    public static void main(String[] args) {
        List<Integer> il = Arrays.asList(1, 2, 3);
        List<Double> dl = Arrays.asList(0.5, 2.5, 3.0);
        List<AtomicLong> al = Arrays.asList(new AtomicLong(1), new AtomicLong(2));
        System.out.println("sum1 = " + sumOfList1(il));
        System.out.println("sum1 = " + sumOfList1(dl));
        System.out.println("sum1 = " + sumOfList1(al));
        System.out.println("sum2 = " + sumOfList2(il));
        System.out.println("sum2 = " + sumOfList2(dl));
        //编译失败：AtomicLong继承了Number 但没有实现Comparable
//        System.out.println("sum2 = " + sumOfList2(al));
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        Assert.assertEquals(2, writeParam1(integers));
        Assert.assertEquals(3, writeParam2(integers, 0));
        //Object是Integer父类 可以调用
        List<Object> objects = new ArrayList<>();
        objects.add(new String(""));
        Assert.assertEquals(1, writeParam1(objects));
    }

}
