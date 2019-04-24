package generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-4-17 下午4:40 <br/>
 * Desc:
 */
public class ListTest {

    @Test
    public void t() {
        int i = 0;

        System.out.println(i++);
        System.out.println(i);

        int j = 0;
        System.out.println(++j);
        System.out.println(j);
    }

    @Test
    public void testAddAllByIndex() {

        List<Integer> list = new ArrayList<>(4);
        list.addAll(Arrays.asList(null, null, null, null));
        List<Integer> toAdd = Arrays.asList(4, 5);
        list.addAll(3, toAdd);
        toAdd = Arrays.asList(2, 3);
        list.addAll(2, toAdd);
        print(list);
        list.removeIf(Objects::isNull);
        print(list);
        System.out.println(list.size());
        int cardIndex = 0;
        List<Integer> cardContents = new ArrayList<>();
        cardContents.addAll(Arrays.asList(null, null, null, null));
        for (Integer i : list) {
            while (cardIndex < cardContents.size()) {
                if (cardContents.get(cardIndex) == null) {
                    cardContents.set(cardIndex, i);
                    break;
                }
                cardIndex++;
            }
        }
    }

    private void print(List<Integer> list) {
        System.out.println(list.stream().map(i -> {
            if (i == null) {
                return "null";
            } else {
                return i.toString();
            }
        }).collect(Collectors.joining(",")));
    }

}
