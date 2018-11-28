package main.java.io.remote.classic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static main.java.io.CommonConstants.BUFFER_SIZE;
import static main.java.io.CommonConstants.DEFAULT_PORT;

/**
 * @author root
 * @date 18-1-29
 */
public class Server implements Runnable {

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(DEFAULT_PORT);
            while (!Thread.interrupted()) {
                new Thread(new Handler(ss.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Handler implements Runnable {
        final Socket socket;

        Handler(Socket s) {
            socket = s;
        }

        public void run() {
            try {
                byte[] inputBuffer = new byte[BUFFER_SIZE];
                socket.getInputStream().read(inputBuffer);
                byte[] outputBuffer = process(inputBuffer);
                socket.getOutputStream().write(outputBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private byte[] process(byte[] data) {
            String dataString = new String(data);
            dataString = "resp:" + dataString;
            return dataString.getBytes();
        }
    }
}
