package main.java.io.local.readfile;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.zip.CRC32;

/**
 * MappedByteBuffer
 */
public class MappedReader implements ReadFileTest{
    public Long checkSum(String filePath) {
        try {
            FileChannel channel=FileChannel.open(Paths.get(filePath));
            CRC32 crc32=new CRC32();
            MappedByteBuffer mappedByteBuffer=channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size());
            while (mappedByteBuffer.hasRemaining()){
                int c=mappedByteBuffer.get();
                crc32.update(c);
            }
            channel.close();
            return crc32.getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
