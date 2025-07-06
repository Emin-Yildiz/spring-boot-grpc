package com.example.grpcdemo.user;

import com.example.proto.lib.CreateUserRequest;
import com.example.proto.lib.UpdateUserRequest;
import com.example.proto.lib.UserResponse;
import com.example.proto.lib.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;
import java.util.logging.Logger;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public UserService(UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub) {
        this.userServiceBlockingStub = userServiceBlockingStub;
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        logger.info("İstek Geldi Laaa");
        UserResponse userResponse = UserResponse.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName(request.getName())
                .build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
        logger.info("İstek Geldi Laaa");
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        super.updateUser(request, responseObserver);
    }

    public void test(){
        UserResponse response = userServiceBlockingStub.createUser(CreateUserRequest.newBuilder().setName("test").build());
        response.getName();
    }
}
