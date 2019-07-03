package io.local.readfile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CRC32;

/**
 * NIO files Buffered stream
 */
public class FilesReader implements ReadFileTest {

    public Long checkSum(String filePath) {
        Path path = Paths.get(filePath);
        try {
            InputStream in = new BufferedInputStream(Files.newInputStream(path));
            CRC32 crc32 = new CRC32();
            int c;
            while ((c = in.read()) != -1) {
                crc32.update(c);
            }
            in.close();
            return crc32.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
