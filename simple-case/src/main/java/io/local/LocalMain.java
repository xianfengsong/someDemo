package io.local;

import io.CommonConstants;
import io.local.readfile.BufferedStreamReader;
import io.local.readfile.FilesReader;
import io.local.readfile.InputStreamReader;
import io.local.readfile.MappedReader;
import io.local.readfile.RandomReader;
import io.local.updatefile.MemoryMapping;
import io.local.updatefile.ReadWithBuffer;

/**
 * 本地io 示例 入口
 */
public class LocalMain {
    //打印汇编指令：-XX:+UnlockDiagnosticVMOptions  -XX:+PrintAssembly   -XX:+TraceClassLoading -XX:+LogCompilation
    public static void main(String[]args){
        //对比不同的读文件
        updateFileTest();
        //对比不同的写文件方式
        readFileTest();


    }
    public static void updateFileTest(){
        String java="java",php="php ";

        System.out.println("测试Buffered Reader/Writer修改文件内容");
        Long s=System.currentTimeMillis();
        ReadWithBuffer readerTest=new ReadWithBuffer();
        readerTest.updateFile(php,java);
        System.out.println("time:"+(System.currentTimeMillis()-s));
//      内存溢出了
//        System.out.println("测试Files修改文件内容");
//        Long s1=System.currentTimeMillis();
//        NIOFiles filesTest=new NIOFiles();
//        filesTest.updateFile(php,java);
//        System.out.println("time:"+(System.currentTimeMillis()-s1));

        System.out.println("测试Map Buffer修改文件内容");
        Long s3=System.currentTimeMillis();
        MemoryMapping mapping=new MemoryMapping();
        mapping.updateFile(java,php);
        System.out.println("time:"+(System.currentTimeMillis()-s3));
        //time:173060
    }
    public static void readFileTest(){

        String filePath= CommonConstants.FILE_PATH;

        System.out.println("普通的 file input stream");
        Long start=System.currentTimeMillis();
        InputStreamReader reader=new InputStreamReader();
        Long checkSum=reader.checkSum(filePath);
        Long end=System.currentTimeMillis();
        System.out.println(Long.toHexString(checkSum)+" time:"+(end-start));

        System.out.println("buffered input stream");
        start=System.currentTimeMillis();
        BufferedStreamReader br=new BufferedStreamReader();
        checkSum=br.checkSum(filePath);
        end=System.currentTimeMillis();
        System.out.println(Long.toHexString(checkSum)+" time:"+(end-start));


        System.out.println("NIO  files buffer input stream");
        start=System.currentTimeMillis();

        FilesReader fr=new FilesReader();
        checkSum=fr.checkSum(filePath);

        end=System.currentTimeMillis();
        System.out.println(Long.toHexString(checkSum)+" time:"+(end-start));



        System.out.println("random access files");
        start=System.currentTimeMillis();

        RandomReader rr=new RandomReader();
        checkSum=rr.checkSum(filePath);

        end=System.currentTimeMillis();
        System.out.println(Long.toHexString(checkSum)+" time:"+(end-start));


        System.out.println("mapped buffer");
        start=System.currentTimeMillis();

        MappedReader mr=new MappedReader();
        checkSum=mr.checkSum(filePath);

        end=System.currentTimeMillis();
        System.out.println(Long.toHexString(checkSum)+" time:"+(end-start));

    }
}

