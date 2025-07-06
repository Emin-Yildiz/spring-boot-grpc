package com.example.grpcdemo.config;

import com.example.proto.lib.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub(GrpcChannelFactory grpcChannelFactory) {
        return UserServiceGrpc.newBlockingStub(grpcChannelFactory.createChannel("0.0.0.0:9090"));
    }
}
