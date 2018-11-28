package main.java.io.local.readfile;

import java.io.*;
import java.util.zip.CRC32;

/**
 * file input stream
 */
public class InputStreamReader implements ReadFileTest {
    public Long checkSum(String filePath) {
        File file=new File(filePath);
        try {
            InputStream in=new FileInputStream(file);
            CRC32 crc32=new CRC32();
            int c;
            while ((c=in.read())!=-1){
                crc32.update(c);
            }
            return crc32.getValue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
