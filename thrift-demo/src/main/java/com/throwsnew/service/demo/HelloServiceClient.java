package com.throwsnew.service.demo;

import com.throwsnew.thriftdemo.api.HelloService;
import java.io.IOException;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class HelloServiceClient {

    public static void main(String[] args) {
        try {
            String isSync = "false";
            if ("true".equals(isSync)) {
                TTransport transport = new TSocket("localhost", 7911);
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                HelloService.Client client = new HelloService.Client(protocol);
                client.helloVoid();
                transport.close();
            } else {
                TAsyncClientManager clientManager = new TAsyncClientManager();
                TNonblockingTransport transport = new TNonblockingSocket("localhost", 7911);
                TProtocolFactory protocol = new TBinaryProtocol.Factory();
                HelloService.AsyncClient asyncClient = new HelloService.AsyncClient(protocol, clientManager, transport);
                asyncClient.setTimeout(1500L);
                HelloStringCallback callback = new HelloStringCallback();
//              CommonCallback callback=new CommonCallback();
                asyncClient.helloString("baba", callback);
                System.out.println("client async calls");
                //等待触发回调
                Thread.sleep(1000L);


            }
        } catch (TException | IOException e) {
            System.out.println("ERROR");
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
