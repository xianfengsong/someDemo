package concurrent.forkjoinpool;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * author Xianfeng <br/>
 * date 2020/10/21 下午3:20 <br/>
 * Desc: 使用java8的流 串行/并行处理数据
 */
public class StreamTest {

    /**
     * 使用LongStream可以减少装箱/拆箱操作，LongStream提供的是原始类型long
     */
    private LongStream stream;

    public StreamTest(LongStream stream) {
        this.stream = stream;
    }

    long serialSum() {
        return stream.sum();
    }

    long parallelSum() {
        return stream.parallel().sum();
    }

    List<String> parallelFilter() {
        return stream.parallel().mapToObj(e -> {
            try {
                Thread.sleep(100L);
                if (e % 2 == 0) {
                    return Arrays.asList(String.valueOf(e), ":");
                }
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            return Collections.singletonList(" ");
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    long exceptionParallelSum() {
        return stream.parallel().reduce(0, (left, right) -> {
            System.out.println("sum(" + left + "," + right + ")");
            if (left == 1000) {
                throw new RuntimeException("exception");
            }
            return left + right;
        });
    }

}
