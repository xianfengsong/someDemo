package io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Created by Administrator on 2018-1-31 0031.
 */
public class Utils {

    static Charset utf8 = Charset.forName("UTF-8");
    public static CharsetDecoder uft8Decoder = utf8.newDecoder();
    public static CharsetEncoder uft8Encoder = utf8.newEncoder();

    private Utils() {
    }
}
