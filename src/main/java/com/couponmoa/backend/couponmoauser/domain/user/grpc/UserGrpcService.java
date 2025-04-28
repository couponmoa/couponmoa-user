package com.couponmoa.backend.couponmoauser.domain.user.grpc;
import com.couponmoa.backend.couponmoauser.domain.user.entity.User;
import com.couponmoa.backend.couponmoauser.domain.user.repository.UserRepository;
import com.couponmoa.backend.couponmoauser.domain.user.service.v1.UserServiceV1;
import com.couponmoa.grpc.user.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserServiceV1 userService;
    private final UserRepository userRepository;

    @Override
    public void findById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = userService.getUserById(request.getUserId());

        String deletedAt = user.getDeletedAt() != null ? user.getDeletedAt().toString() : ""; // String format

        UserResponse.Builder responseBuilder = UserResponse.newBuilder();
        responseBuilder.setId(user.getId());
        responseBuilder.setEmail(user.getEmail());
        responseBuilder.setNickname(user.getNickname());
        responseBuilder.setUserRole(user.getUserRole().toString());
        responseBuilder.setDeletedAt(deletedAt);

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserEmailList(UserIdsRequest request, StreamObserver<UserEmailsResponse> responseObserver) {
        List<Long> userIdList = request.getUserIdsList();

        List<String> emailList = userRepository.findAllById(userIdList).stream().map(User::getEmail).toList();
        UserEmailsResponse.Builder responseBuilder = UserEmailsResponse.newBuilder();
        responseBuilder.addAllEmails(emailList);

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
