package byteoperator;

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
    public static final void encodeFrameSize(final int frameSize, final byte[] buf) {
        buf[0] = (byte) (0xff & (frameSize >> 24));
        buf[1] = (byte) (0xff & (frameSize >> 16));
        buf[2] = (byte) (0xff & (frameSize >> 8));
        buf[3] = (byte) (0xff & (frameSize));
    }

    public static final int decodeFrameSize(final byte[] buf) {
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
        encodeFrameSize(source, toBytes);
        System.out.println(String.format("[%d][%d][%d][%d]", toBytes[0], toBytes[1], toBytes[2], toBytes[3]));
        System.out.println(decodeFrameSize(toBytes));
    }
}
