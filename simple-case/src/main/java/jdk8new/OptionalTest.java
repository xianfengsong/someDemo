package jdk8new;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * author Xianfeng <br/>
 * date 18-11-1 下午5:47 <br/>
 * Desc:
 */
public class OptionalTest {

    OptionalTest lib;

    public Result init() {
        Result init = new Result();
        init.title = "t";
        init.content = Arrays.asList("c", "cc");
        init.children = Arrays.asList(new Result(), new Result());
        return init;
    }

    public Result methodReturnNull() {
        return null;
    }

    public Optional<Result> methodReturnOption() {
        return Optional.ofNullable(methodReturnNull());
    }

    @Before
    public void build() {
        lib = new OptionalTest();
    }

    @Test(expected = NoSuchElementException.class)
    public void callMethodReturnNull() {
        Optional<Result> result = Optional.ofNullable(lib.methodReturnNull());
        Assert.notNull(result, "result is null!");
        Assert.isTrue(!result.isPresent(), "是空对象");
        //只改变了返回值，没改变result内容
        result.orElse(init());
        Assert.isTrue(!result.isPresent(), "result is change");
        result.get();//throw NoSuchElementException
    }

    @Test(expected = IllegalArgumentException.class)
    public void callMethodReturnOption() {
        Optional<Result> optionalResult = lib.methodReturnOption();
        Assert.notNull(optionalResult, "result is null!");

        Assert.notNull(optionalResult.orElse(init()), "orElse return null");
        System.out.println(optionalResult.orElse(init()).toString());

        optionalResult.orElseThrow(IllegalArgumentException::new);
    }

    @Test
    public void testFilter() {
        Optional<Result> r = Optional.of(init());
        r.filter(result -> "t".equals(result.title))
                .ifPresent(System.out::println);

    }

    @Test
    public void testMap() {
        //map操作进行属性的提取，并不会报空指针异常
        Optional<Result> r = methodReturnOption();
        Optional<String> inner = r.map(Result::getTitle);
        Assert.notNull(inner);
        Assert.isTrue(!inner.isPresent());
    }

    class Result {

        String title;
        List<String> content;
        List<Result> children;
        private Optional<String> inner;

        @Override
        public String toString() {
            return "Result{" +
                    "title='" + title + '\'' +
                    ", content=" + content.stream().collect(Collectors.joining(",")) +
                    ", children=" + children.size() +
                    '}';
        }

        public String getTitle() {
            return title;
        }

        public Optional<String> getInner() {
            return inner;
        }
    }
}
