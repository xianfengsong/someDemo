package main.java.jvm;

import jvm.gc.ReferenceCountGC;

/**
 * jvm 相关程序入口
 */
public class Main {
    public static void main(String[]args){
        gcTest();
    }
    static ReferenceCountGC a=new ReferenceCountGC();
    static ReferenceCountGC b=new ReferenceCountGC();
    public static void gcTest(){

            a.ref=b;
            b.ref=a;
            System.gc();
            while (!Thread.interrupted()){
                a.doSomeThing();
            }
    }
}
