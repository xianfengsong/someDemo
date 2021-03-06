package io.local.readfile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * BufferedInputStream
 */
public class BufferedStreamReader implements ReadFileTest {

    public Long checkSum(String filePath) {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            CRC32 crc32 = new CRC32();
            int c;
            while ((c = in.read()) != -1) {
                crc32.update(c);
            }
            in.close();
            return crc32.getValue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
