package net.devibot.provider.service;

import io.grpc.stub.StreamObserver;
import net.devibot.grpc.messages.DefaultSuccessResponse;
import net.devibot.grpc.messages.KeepAliveMessage;
import net.devibot.grpc.provider.ProviderServiceGrpc;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcService extends ProviderServiceGrpc.ProviderServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(GrpcService.class);

    private Provider provider;

    public GrpcService(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void keepAlive(KeepAliveMessage request, StreamObserver<DefaultSuccessResponse> responseObserver) {
        logger.info("Received Keep Alive message from mainframe .. responding");
        responseObserver.onNext(DefaultSuccessResponse.newBuilder().setSuccess(true).build());
    }
}
