package concurrent.tools;

import concurrent.Test;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.concurrent.BrokenBarrierException;

/**
 * Context
 */
public class ThreadClassLoaderTest implements Test {

    @Override
    public void test() throws BrokenBarrierException, InterruptedException {
        try {
            ClassLoader main = this.getClass().getClassLoader();
            MyClassLoader anotherLoader = new MyClassLoader();

            Class clazz = anotherLoader.loadClass("concurrent.tools.ThreadClassLoaderTest$A");
            for (Constructor c : clazz.getConstructors()) {
                c.setAccessible(true);
            }
            //todo 实例化失败
            final A a = (A) clazz.newInstance();

            System.out.println("Custom Loader:" + anotherLoader);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    ClassLoader thread = Thread.currentThread().getContextClassLoader();
                    try {
                        a.doSome();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("context " + thread);
                }
            });
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果不是静态内部类，反射构造实例时还要依赖外部类实例对象
     */
    static class A {

        public static void doSome() {
            try {
                Class clazz = Class.forName("concurrent.tools.ThreadClassLoaderTest");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class MyClassLoader extends ClassLoader {

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            //避免被父classloader加载
            if (name.contains("ThreadClassLoaderTest$A")) {
                return findClass(name);
            }
            return super.loadClass(name, resolve);
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            System.out.println("load()");
            Class clazz = null;
            try {
                String path = "/home/song/someDemo/someDemo/target/classes/" + name.replace(".", "/") + ".class";
                FileInputStream r = new FileInputStream(path);
                int size = (int) r.getChannel().size();
                byte[] data = new byte[size];
                r.read(data);
                clazz = defineClass(name, data, 0, data.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clazz;
        }
    }
}

