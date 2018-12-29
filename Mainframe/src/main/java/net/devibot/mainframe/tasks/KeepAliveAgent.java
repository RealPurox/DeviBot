package net.devibot.mainframe.tasks;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.ConnectToMainframeResponse;
import net.devibot.grpc.messages.DefaultSuccessResponse;
import net.devibot.grpc.messages.KeepAliveMessage;
import net.devibot.grpc.provider.ProviderServiceGrpc;
import net.devibot.mainframe.Mainframe;
import net.devibot.mainframe.entities.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KeepAliveAgent implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(KeepAliveAgent.class);

    private Mainframe mainframe;

    public KeepAliveAgent(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    @Override
    public void run() {
        logger.info("Pinging all provider ..");
        List<Provider> providers = mainframe.getProviderManager().getProviderList();

        for (Provider provider : providers) {
            provider.getStub().keepAlive(KeepAliveMessage.newBuilder().build(), new StreamObserver<DefaultSuccessResponse>() {
                @Override
                public void onNext(DefaultSuccessResponse defaultSuccessResponse) {
                    provider.setLastKeepAliveTime();
                    logger.info("Provider #" + provider.getId() + " responded");
                }

                @Override
                public void onError(Throwable throwable) {
                    //todo kill provider, request new provider, start new provider
                    if (throwable instanceof StatusRuntimeException && ((StatusRuntimeException) throwable).getStatus().getCode() == Status.Code.UNAVAILABLE) {
                        logger.error("!!! -- !!! Provider is offline !!! -- !!!");
                    }
                }

                @Override
                public void onCompleted() { }
            });
        }
    }
}
