package main.java.io;

import io.remote.aio.AIOClient;
import io.remote.aio.AIOServer;
import io.remote.clients.NIOClient;
import io.remote.reactor.multiple.MultiReactor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static main.java.io.CommonConstants.CLIENT_MODE;
import static main.java.io.CommonConstants.SERVER_MODE;

/**
 * 网络io demo 入口
 */
public class Main {
    public static void main(String[] args) {
//        reactor(args);
        aio(args);

    }

    /**
     * reactor模式
     */
    private static void reactor(String[] args){
        try {
            String arg = args[0];
            if (SERVER_MODE.equals(arg)) {
                Thread server = new Thread(new MultiReactor(CommonConstants.DEFAULT_PORT));
                server.start();
            } else if (CLIENT_MODE.equals(arg)) {
                ExecutorService executor= Executors.newCachedThreadPool();
                for(int i=0;i<10;i++){
                    executor.execute(new NIOClient());
                }
                executor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * aio模式
     */
    private static void aio(String[] args){
        try {
            String arg = args[0];
            if (SERVER_MODE.equals(arg)) {
                Thread server = new Thread(new AIOServer(CommonConstants.DEFAULT_PORT));
                server.start();
            } else if (CLIENT_MODE.equals(arg)) {
                ExecutorService executor= Executors.newFixedThreadPool(20);
                for(int i=0;i<20;i++){
                    executor.execute(new AIOClient());
                }
                executor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
