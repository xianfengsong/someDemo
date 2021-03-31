package shutdownhook;

import static io.CommonConstants.BUFFER_SIZE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * author Xianfeng <br/>
 * date 2021/3/31 上午10:16 <br/>
 * Desc:
 * 测试kill命令不同参数 或其他场景 是否都能触发shutdownhook？
 * 1.kill pid 可以（默认15，SIGTERM）
 * 2.kill -9 pid 不可以
 * 3.kill -1 pid 可以（SIGHUP）
 * 4.kill -14 pid 不可以（SIGALRM）
 * 5.System.exit(-1) 可以(虚拟机正常退出)
 * 6.OutOfMemoryError 可以(虚拟机正常退出)
 */
public class Task {

    public static void main(String[] args) {
        try {
            SocketAddress address = new InetSocketAddress("localhost", 8877);
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(address);
            //添加shutdownhook
            /**
             * java doc对shutdown hook的要求
             * 1.线程安全，避免死锁
             * 2.快速结束
             * 3.不依赖用户交互或三方服务
             */
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("task shutdown!");
                    try {
                        //不再accept,关闭已有socketchannel
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));

//            DumbObj dumb = new DumbObj(2024, null);
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                //shell通过telnet localhost 8877建立链接，输入任意字符，会收到消息hello world
                new Thread(() -> {
                    try {
                        byte[] inputBuffer = new byte[BUFFER_SIZE];
                        while (socket.getInputStream().read(inputBuffer) != 0) {
                            byte[] outputBuffer = ("hello world").getBytes();
                            socket.getOutputStream().write(outputBuffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
