package main.java.jvm.gc;

/**
 * 循环引用的对象是否会被回收
 * (还是被回收了，那么什么样的对象是GC root)
 * GC root:
 * 1.虚拟机栈（和本地方法栈）中的引用的对象
 * 2.方法区中类静态属性引用的对象，常量引用的对象
 */
public class ReferenceCountGC{
    public static Object ref=null;

    //1Mb
    private byte[] someData=new byte[1024*1024];
    public void doSomeThing(){}
    public static void test(){
        ReferenceCountGC a=new ReferenceCountGC();
        ReferenceCountGC b=new ReferenceCountGC();
        a.ref=b;
        b.ref=a;
        Thread t=new Thread(new Runner(a));
        t.start();
        while (!t.isAlive()){}
        System.gc();
        while (!Thread.interrupted()){

        }
    }
    static class Runner implements Runnable{
        static ReferenceCountGC ref;
        public Runner(ReferenceCountGC a){
            ref=a;
        }
        @Override
        public void run() {
            try {
                while(!Thread.interrupted()){
                    ref.doSomeThing();
                    Thread.sleep(100L);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
