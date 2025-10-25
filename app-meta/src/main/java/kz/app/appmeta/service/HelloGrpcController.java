package kz.app.appmeta.service;

import io.grpc.stub.StreamObserver;
import kz.app.grpc.HelloReply;
import kz.app.grpc.HelloRequest;
import kz.app.grpc.HelloServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HelloGrpcController extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        String message = "Привет, META, " + request.getName() + "!";
        HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
