package com.example.serverstreamingdemo.service;

import com.example.proto.message.MessageHistoryRequest;
import com.example.proto.message.MessageResponse;
import com.example.proto.message.MessageServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.List;

@GrpcService
public class StreamService extends MessageServiceGrpc.MessageServiceImplBase {

    Logger logger = LoggerFactory.getLogger(StreamService.class);

    @Override
    public void getMessageHistory(MessageHistoryRequest request, StreamObserver<MessageResponse> responseObserver) {

        logger.info("getMessageHistory");

        String userId = request.getUserId();

        List<String> messages = List.of("Merhaba!", "Nasılsın?", "Görüşürüz!");

        for (int i = 0; i < messages.size(); i++) {
            MessageResponse response = MessageResponse.newBuilder()
                    .setMessageId(String.valueOf(i + 1))
                    .setSender(userId)
                    .setContent(messages.get(i))
                    .setTimestamp(Instant.now().getEpochSecond())
                    .build();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }
}
