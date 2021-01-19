package io.niotest.classic;

import static io.CommonConstants.BUFFER_SIZE;
import static io.CommonConstants.DEFAULT_PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author root
 * @date 18-1-29
 */
public class Server implements Runnable {

    private int backLogSize = 0;

    public Server() {
    }

    public Server(int backLogSize) {
        this.backLogSize = backLogSize;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    @Override
    public void run() {
        try {
            if (backLogSize == 0) {
                //default backlog
                backLogSize = 50;
            }
            ServerSocket ss = new ServerSocket(DEFAULT_PORT, backLogSize);

            while (!Thread.interrupted()) {
                Socket newConn = ss.accept();
                new Thread(new Handler(newConn)).start();
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

        @Override
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
            return dataString.getBytes();
        }
    }
}
