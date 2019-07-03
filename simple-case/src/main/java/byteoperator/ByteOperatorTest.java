package byteoperator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;

/**
 * author Xianfeng <br/>
 * date 19-4-23 下午4:51 <br/>
 * Desc: 用到位运算符的各种编码算法
 */
public class ByteOperatorTest {


    private byte[] i32buf = new byte[5];

    /**
     * 按照大端方式处理 高位放在内存低地址
     */
    private static void saveWithBigEnd(final int frameSize, final byte[] buf) {
        buf[0] = (byte) (0xff & (frameSize >> 24));
        buf[1] = (byte) (0xff & (frameSize >> 16));
        buf[2] = (byte) (0xff & (frameSize >> 8));
        buf[3] = (byte) (0xff & (frameSize));
    }

    private static int readBigEnd(final byte[] buf) {
        return ((buf[0] & 0xff) << 24) |
                ((buf[1] & 0xff) << 16) |
                ((buf[2] & 0xff) << 8) |
                ((buf[3] & 0xff));
    }

    /**
     * 测试thrift的变长整形编码方式
     * org.apache.thrift.protocol.TCompactProtocol#writeVarint32(int)
     */
    @Test
    public void testThriftVarint() {
        writeVarint32(1025);
    }

    private void writeVarint32(int n) {
        int idx = 0;
        while (true) {
            //0111_1111取反 1000_0000
            if ((n & ~0x7F) == 0) {
                i32buf[idx++] = (byte) n;
                break;
            } else {
                //与0111_1111 或1000_0000
                i32buf[idx++] = (byte) ((n & 0x7F) | 0x80);
                n >>>= 7;
            }
        }
        System.out
                .println(String.format("[%d][%d][%d][%d][%d]", i32buf[0], i32buf[1], i32buf[2], i32buf[3], i32buf[4]));
    }

    //测试 int值按大端转成byte数组(4个字节)
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
     * 测试 java默认使用的小端存储
     */
    @Test
    public void localEndianness() {

        System.out.println(ByteOrder.nativeOrder());
        Assert.assertEquals("jvm默认是小端字节序", "LITTLE_ENDIAN", ByteOrder.nativeOrder().toString());

    }

    /**
     * 测试：java ByteBuffer切换大小端
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
