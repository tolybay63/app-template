package kz.app.grpc;

import net.devh.boot.grpc.client.inject.*;
import org.springframework.stereotype.*;

@Service
public class HelloGrpcClient {

    @GrpcClient("local-grpc-server")
    private HelloServiceGrpc.HelloServiceBlockingStub stub;

    public String sendHello(String name) {
        HelloReply response = stub.sayHello(
                HelloRequest.newBuilder().setName(name).build()
        );
        return response.getMessage();
    }
}
