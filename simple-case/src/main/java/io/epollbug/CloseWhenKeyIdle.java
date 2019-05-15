package io.epollbug;

/**
 * author Xianfeng <br/>
 * date 19-5-7 下午8:12 <br/>
 * Desc:
 */

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * open jdk 找到的测试epoll空轮训bug的测试用例
 */
public class CloseWhenKeyIdle {

    // indicates if the wakeup has happened
    static volatile boolean wakeupDone = false;

    public static void main(String[] args) throws Exception {

        while (true) {

            // Skip test on pre-2.6 kernels until the poll SelectorProvider
            // is updated
            String osname = System.getProperty("os.name");
            if (osname.equals("Linux")) {
                String[] ver = System.getProperty("os.version").split("\\.", 0);
                if (ver.length >= 2) {
                    int major = Integer.parseInt(ver[0]);
                    int minor = Integer.parseInt(ver[1]);
                    if (major < 2 || (major == 2 && minor < 6)) {
                        System.out.println("Test passing on pre-2.6 kernel");
                        return;
                    }
                }
            }

            // establish loopback connection

            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(0));

            SocketAddress remote = new InetSocketAddress(InetAddress.getLocalHost(),
                    ssc.socket().getLocalPort());

            SocketChannel sc1 = SocketChannel.open(remote);
            SocketChannel sc2 = ssc.accept();

            // register channel for one end with a Selector, interest set = 0

            Selector sel = Selector.open();

            sc1.configureBlocking(false);
            SelectionKey k = sc1.register(sel, 0);
            sel.selectNow();

            // hard close to provoke POLLHUP

            sc2.socket().setSoLinger(true, 0);
            sc2.close();

            // schedule wakeup after a few seconds

            Thread t = new Thread(new Waker(sel, 5000));
            t.setDaemon(true);
            t.start();

            // select should block

            int spinCount = 0;
            boolean failed = false;
            for (; ; ) {
                int n = sel.select();
                if (n > 0) {
                    System.err.println("Channel should not be selected!!!");
                    Thread.dumpStack();
                    failed = true;
                    break;
                }

                // wakeup
                if (wakeupDone) {
                    break;
                }

                // wakeup for no reason - if it happens a few times then we have a
                // problem
                spinCount++;
                if (spinCount >= 3) {
                    System.err.println("Selector appears to be spinning");
                    Thread.dumpStack();
                    failed = true;
                    break;
                }
            }

            sc1.close();
            sel.close();

            if (failed) {
                throw new RuntimeException("Test failed");
            }

            System.out.println("PASS");
        }
    }

    // Wakes up a Selector after a given delay
    static class Waker implements Runnable {

        private Selector sel;
        private long delay;

        Waker(Selector sel, long delay) {
            this.sel = sel;
            this.delay = delay;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delay);
                wakeupDone = true;
                sel.wakeup();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

}
