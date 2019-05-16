package byteoperator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-4-23 下午4:51 <br/>
 * Desc:
 */
public class ByteOperatorTest {

    /**
     * 按照大端方式处理 高位放在内存低地址
     */
    public static final void saveWithBigEnd(final int frameSize, final byte[] buf) {
        buf[0] = (byte) (0xff & (frameSize >> 24));
        buf[1] = (byte) (0xff & (frameSize >> 16));
        buf[2] = (byte) (0xff & (frameSize >> 8));
        buf[3] = (byte) (0xff & (frameSize));
    }

    public static final int readBigEnd(final byte[] buf) {
        return ((buf[0] & 0xff) << 24) |
                ((buf[1] & 0xff) << 16) |
                ((buf[2] & 0xff) << 8) |
                ((buf[3] & 0xff));
    }

    //int值按大端转成byte数组(4个字节)
    @Test
    public void bigEndTest() {

        Integer source = 1024;
        System.out.println(Integer.toBinaryString(source));
        byte[] toBytes = new byte[4];
        saveWithBigEnd(source, toBytes);
        System.out.println(String.format("[%d][%d][%d][%d]", toBytes[0], toBytes[1], toBytes[2], toBytes[3]));
        System.out.println(readBigEnd(toBytes));
    }

    /**
     * java 默认小端存储
     */
    @Test
    public void localEndianness() {

        System.out.println(ByteOrder.nativeOrder());
        Assert.assertEquals("jvm默认是小端字节序", "LITTLE_ENDIAN", ByteOrder.nativeOrder().toString());

    }

    /**
     * java ByteBuffer切换大小端
     */
    @Test
    public void changeEndianness() {
        //1025
        short value = 0b0000_0100_0000_0001;

        ByteBuffer bb = ByteBuffer.wrap(new byte[2]);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.asShortBuffer().put(value);
        System.out.println("value:0000_0100_0000_0001");

        Assert.assertEquals("小端内存低位保存低字节", 0b0000_0001, bb.get(0));
        int index = 0;
        for (byte b : bb.array()) {
            System.out.println(
                    String.format("小端：[%d]\t%8s", index++, Integer.toBinaryString(b)).replace(' ', '0'));
        }
        bb.clear();
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.asShortBuffer().put(value);
        Assert.assertEquals("大端内存低位保存高字节", bb.get(0), 0b0000_0100);
        index = 0;
        for (byte b : bb.array()) {
            System.out.println(
                    String.format("大端：[%d]\t%8s", index++, Integer.toBinaryString(b)).replace(' ', '0'));
        }
    }
}
