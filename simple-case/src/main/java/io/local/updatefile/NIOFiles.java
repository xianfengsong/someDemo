package io.local.updatefile;

import io.CommonConstants;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 用1.7的NIO Files修改文件
 */
public class NIOFiles implements UpdateFileTest {
    public void updateFile(String target, String replace) {
        try {
            System.gc();
            long initBytes = Runtime.getRuntime().freeMemory();

            Path path= Paths.get(CommonConstants.FILE_PATH);
            Path tempPath =Paths.get(CommonConstants.FILE_PATH+".tmp");
            Charset uft8=Charset.forName("utf8");
            for(String line: Files.readAllLines(path, uft8)){
                if(line.contains(target)){
                    line=line.replaceAll(target,replace);
                }
                Files.write(tempPath,line.getBytes(uft8));
            }
            Files.move(tempPath,path, StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Use:" + (initBytes - Runtime.getRuntime().freeMemory()) / 1024 + "KB");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
