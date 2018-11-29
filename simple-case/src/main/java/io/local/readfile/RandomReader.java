package io.local.readfile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

/**
 * Created by root on 18-3-2.
 */
public class RandomReader implements ReadFileTest {
    public Long checkSum(String filePath) {
        try {
            RandomAccessFile file=new RandomAccessFile(filePath,"r");
            long length=file.length();
            CRC32 crc32=new CRC32();
            for(long i=0;i<length;i++){
                file.seek(i);
                int c=file.readByte();
                crc32.update(c);
            }
            file.close();
            return crc32.getValue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
