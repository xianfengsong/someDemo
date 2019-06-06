package com.throwsnew.service.demo;

import com.throwsnew.thriftdemo.api.HelloService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

public class HelloServiceServer {

    public static void main(String[] args) {
        try {
            String isBlock = "false";
            if ("false".equals(isBlock)) {
                //使用非阻塞套接字
                TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(7911);
                HelloService.Processor processor = new HelloService.Processor(new HelloServiceImpl());
                TServer server = new TNonblockingServer(processor, serverTransport);
                System.out.println("Start async server on port 7911...");
                server.serve();
            } else {
                // 设置服务端口为 7911
                TServerSocket serverTransport = new TServerSocket(7911);
                // 设置协议工厂为 TBinaryProtocol.Factory
                TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory();
                // 关联处理器与 Hello 服务的实现
                TProcessor processor = new HelloService.Processor(new HelloServiceImpl());
                TServer server = new TSimpleServer(processor, serverTransport,
                        new TTransportFactory(), proFactory);
                System.out.println("Start server on port 7911...");
                server.serve();
            }

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
