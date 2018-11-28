package main.java.io.local.updatefile;

import main.java.io.CommonConstants;

import java.io.*;

/**
 * BufferedReader BufferedWriter修改文件
 */
public class ReadWithBuffer implements UpdateFileTest {

    /**
     * 读取文件
     * 修改匹配的内容
     * 然后写入新文件
     * 最后用新文件替换源文件
     *
     * @param target
     * @param replace
     */
    public void updateFile(String target, String replace) {
        File file = new File(CommonConstants.FILE_PATH);
        File tempFile = new File(CommonConstants.FILE_PATH + ".tmp");
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            System.gc();
            long initBytes = Runtime.getRuntime().freeMemory();

            reader = new BufferedReader(new FileReader(file), 50 * 1024 * 1024);// 用50M的缓冲读取文本文件
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains(target)) {
                    line = line.replaceAll(target, replace);
                }
                //为了不修改文件，每行都要写
                writer.write(line + "\n");
            }

            System.out.println("Use:" + (initBytes - Runtime.getRuntime().freeMemory()) / 1024 + "KB");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        file.delete();
        tempFile.renameTo(file);

    }
}
