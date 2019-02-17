package net.devibot.provider.service;

import io.grpc.stub.StreamObserver;
import net.devibot.grpc.messages.DefaultSuccessResponse;
import net.devibot.grpc.messages.Empty;
import net.devibot.grpc.provider.ProviderServiceGrpc;
import net.devibot.provider.Provider;
import net.devibot.provider.agents.KeepAliveAgent;
import net.devibot.provider.manager.AgentManager;

public class ProviderService extends ProviderServiceGrpc.ProviderServiceImplBase {

    private Provider provider;

    public ProviderService(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void keepAlive(Empty request, StreamObserver<DefaultSuccessResponse> responseObserver) {
        ((KeepAliveAgent)provider.getDiscordBot().getAgentManager().getAgent(AgentManager.Type.KEEP_ALIVE)).updateLastKeepAliveTime();
        responseObserver.onNext(DefaultSuccessResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

}
