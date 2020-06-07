package byteoperator;

import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-9-10 下午5:37 <br/>
 * Desc:
 */
public class BitSetTest {

    @Test
    public void testBitSetCreate() {
        int[] array = new int[]{1, 20, 64, 128, 2048};
        int size = 2048;
        BitSet bitSet = new BitSet(size);

        for (int i : array) {
            bitSet.set(i);
        }
        System.out.println(bitSet.toString());
        //位置1上有值
        Assert.assertTrue(bitSet.get(1));
        //位置2上没有值
        Assert.assertFalse(bitSet.get(2));
    }
}
