package jdk8new;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-4-24 下午3:18 <br/>
 * Desc: 测试lambda表达式用法
 */
public class LambdaTest {

    private static final int TYPE_A = 1;
    private static final int TYPE_B = 2;

    //-----测试 stream()-----
    @Test
    public void groupBy() {
        Item a = new Item("a", 12, TYPE_A);
        Item b = new Item("b", 11, TYPE_A);
        Item c = new Item("c", 9, TYPE_B);
        List<Item> itemList = Arrays.asList(a, b, c);
        Map<Integer, List<Item>> mapByType = itemList.stream()
                .collect(Collectors.groupingBy(Item::getType));
        Assert.assertEquals("TYPE_A不是两个", mapByType.get(TYPE_A).size(), 2);
        System.out.println(mapByType.get(TYPE_B).get(0).getName());

    }


    private class Item {

        String name;
        Integer value;
        Integer type;

        public Item() {
        }

        public Item(String name, Integer value, Integer type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }

}
