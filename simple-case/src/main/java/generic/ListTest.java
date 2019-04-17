package generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-4-17 下午4:40 <br/>
 * Desc:
 */
public class ListTest {

    @Test
    public void testAddAllByIndex() {
        List<Integer> list = new ArrayList<>(4);
        list.addAll(Arrays.asList(null, null, null, null, null));
        List<Integer> toAdd = Arrays.asList(4, 5);
        list.addAll(3, toAdd);
        System.out.println(list.stream().map(i -> {
            if (i == null) {
                return "null";
            } else {
                return i.toString();
            }
        }).collect(Collectors.joining(",")));
        System.out.println(list.size());
    }

}
