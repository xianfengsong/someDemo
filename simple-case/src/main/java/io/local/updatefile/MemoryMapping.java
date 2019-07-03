package io.local.updatefile;

import io.CommonConstants;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * 用Map  Buffer 修改文件
 */
public class MemoryMapping implements UpdateFileTest {

    /**
     * 通过map buffer加载文件，在buffer中查找换行符位置
     * 得到一行的字符串，然后做单词的查找替换
     * 缺点1 ：一次映射全部文件 内存占用大
     * 缺点2 ：分次映射全部文件 可能会把换行符截断（/r/n）或者把查询的单词截断（配合readLine使用？）
     */
    public void updateFile(String target, String replace) {
        if (target.length() != replace.length()) {
            System.out.println("长度不同 可能导致连锁更新");
            return;
        }

        try {
            RandomAccessFile file = new RandomAccessFile(CommonConstants.FILE_PATH, "rw");
            FileChannel fileChannel = file.getChannel();

            Long free = Runtime.getRuntime().freeMemory();

            //一次性映射全部文件
            MappedByteBuffer mapBuffer = fileChannel.map(
                    FileChannel.MapMode.READ_WRITE,
                    0, fileChannel.size());
            //证明 没有使用堆内内存
            System.out.println("used:" + (Runtime.getRuntime().freeMemory() - free) / 1024 + "KB");

            //如果没加载到内存，执行load方法
//            System.out.println(mapBuffer.isLoaded());
//            mapBuffer.load();
//            System.out.println(mapBuffer.isLoaded());

            while (mapBuffer.hasRemaining()) {
                mapBuffer.mark();
                //换行符位置
                int separatorPosition = search(mapBuffer);
                if (separatorPosition == -1) {
                    System.out.println("没有换行符。。。失败");
                    break;
                }
                //跳回去 读一行
                mapBuffer.reset();
                int length = separatorPosition - mapBuffer.position();
                byte[] lineBytes = new byte[length];
                mapBuffer.get(lineBytes);
                String line = new String(lineBytes, Charset.forName("utf8"));

                if (line.contains(target)) {
                    line = line.replaceAll(target, replace);
                    //跳回去 开始写
                    mapBuffer.reset();
                    mapBuffer.put(line.getBytes(Charset.forName("utf8")));
                }
            }
            //保存到磁盘
            mapBuffer.force();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int search(MappedByteBuffer mapBuffer) {
        //就这一种换行符，别的不管了
        byte lineSeparator1 = (byte) '\n';
        byte lineSeparator2 = (byte) '\r';
        while (mapBuffer.hasRemaining()) {
            byte current = mapBuffer.get();
            if (current == lineSeparator1 || current == lineSeparator2) {
                return mapBuffer.position();
            }
        }
        return -1;
    }

}
