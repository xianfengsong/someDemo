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

                System.out.println("client async calls");
                HelloStringCallback callback = new HelloStringCallback();
//              CommonCallback callback=new CommonCallback();
                asyncClient.helloString("baba", callback);
                Object result = callback.getResponse();

                while (result == null) {
                    result = callback.getResponse();
                }
                //使用CommonCallback，需要转型
//                result = ((HelloService.AsyncClient.helloString_call) result).getResult();
                System.out.println("msg:" + result);
            }
        } catch (TException | IOException e) {
            e.printStackTrace();
        }
    }
}
