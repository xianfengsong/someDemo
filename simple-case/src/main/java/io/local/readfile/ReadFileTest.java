package io.local.readfile;

/**
 * 读取文件，返回根据所有文件字节计算的校验和
 */
public interface ReadFileTest {

    Long checkSum(String filePath);
}
