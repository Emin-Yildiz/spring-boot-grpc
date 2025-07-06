package com.example.clientstreamingdemo.servis;

import com.example.proto.upload.CreateUserRequest;
import com.example.proto.upload.UploadStatus;
import com.example.proto.upload.UserUploadServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class ClientStreamingService extends UserUploadServiceGrpc.UserUploadServiceImplBase implements ApplicationRunner {

    Logger logger = LoggerFactory.getLogger(ClientStreamingService.class);

    @Override
    public StreamObserver<CreateUserRequest> uploadUsers(StreamObserver<UploadStatus> responseObserver) {

        List<CreateUserRequest> users = new ArrayList<>();

        return new StreamObserver<>() {
            @Override
            public void onNext(CreateUserRequest value) {
                users.add(value);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Hata: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                UploadStatus status = UploadStatus.newBuilder()
                        .setReceivedCount(users.size())
                        .setMessage("Toplam " + users.size() + " kullanıcı alındı.")
                        .build();

                responseObserver.onNext(status);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void run(ApplicationArguments args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        UserUploadServiceGrpc.UserUploadServiceStub stub = UserUploadServiceGrpc.newStub(channel);

        // Server'dan yanıt döndüğünde burası çalışacak.
        StreamObserver<UploadStatus> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(UploadStatus value) {
                System.out.println("Yanıt: " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Tamamlandı.");
            }
        };

        // Sunucuya istek atarken ise burası çalışacak.
        StreamObserver<CreateUserRequest> requestObserver = stub.uploadUsers(responseObserver);

        requestObserver.onNext(CreateUserRequest.newBuilder().setName("Ayşe").setEmail("ayse@example.com").build());
        requestObserver.onNext(CreateUserRequest.newBuilder().setName("Emin").setEmail("emin@example.com").build());
        requestObserver.onNext(CreateUserRequest.newBuilder().setName("Zeynep").setEmail("zeynep@example.com").build());
        requestObserver.onNext(CreateUserRequest.newBuilder().setName("Ender").setEmail("ender@example.com").build());

        requestObserver.onCompleted();
    }
}
