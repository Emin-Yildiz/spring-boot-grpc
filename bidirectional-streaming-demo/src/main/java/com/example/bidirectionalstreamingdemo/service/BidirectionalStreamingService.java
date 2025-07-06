package com.example.bidirectionalstreamingdemo.service;

import com.example.proto.chat.ChatMessage;
import com.example.proto.chat.ChatServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService
public class BidirectionalStreamingService extends ChatServiceGrpc.ChatServiceImplBase implements ApplicationRunner {

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        return new StreamObserver<>() {

            @Override
            public void onNext(ChatMessage message) {
                System.out.println("Sunucu → mesaj alındı: " + message.getContent());

                ChatMessage reply = ChatMessage.newBuilder()
                        .setSender("Sunucu")
                        .setContent("Merhaba " + message.getSender() + ", mesajın alındı: " + message.getContent())
                        .setTimestamp(System.currentTimeMillis())
                        .build();

                responseObserver.onNext(reply);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Sunucu hata: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
                System.out.println("Sunucu → stream tamamlandı");
            }
        };
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ChatServiceGrpc.ChatServiceStub stub = ChatServiceGrpc.newStub(channel);

        StreamObserver<ChatMessage> requestObserver = stub.chat(new StreamObserver<>() {
            @Override
            public void onNext(ChatMessage message) {
                System.out.println("İstemci → yanıt alındı: " + message.getContent());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("İstemci hata: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("İstemci → stream tamamlandı");
            }
        });

        // 3 adet mesaj gönderelim
        for (int i = 1; i <= 3; i++) {
            ChatMessage message = ChatMessage.newBuilder()
                    .setSender("ClientUser")
                    .setContent("Mesaj " + i)
                    .setTimestamp(System.currentTimeMillis())
                    .build();
            requestObserver.onNext(message);
            Thread.sleep(1000);
        }

        requestObserver.onCompleted();

        // Yanıtları beklemek için zaman tanı
        Thread.sleep(3000);
        channel.shutdown();
    }
}
