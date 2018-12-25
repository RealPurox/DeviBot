package net.devibot.provider.service;

import io.grpc.stub.StreamObserver;
import net.devibot.grpc.providerservice.ConnectToMainframeRequest;
import net.devibot.grpc.providerservice.ConnectToMainframeResponse;
import net.devibot.grpc.providerservice.ProviderServiceGrpc;
import net.devibot.provider.Provider;

public class GrpcService extends ProviderServiceGrpc.ProviderServiceImplBase {

    private Provider provider;

    public GrpcService(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void connectToMainframe(ConnectToMainframeRequest request, StreamObserver<ConnectToMainframeResponse> responseObserver) {

    }
}
