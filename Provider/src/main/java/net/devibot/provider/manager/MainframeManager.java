package net.devibot.provider.manager;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.ConnectToMainframeRequest;
import net.devibot.grpc.messages.ConnectToMainframeResponse;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainframeManager {

    private static Logger logger = LoggerFactory.getLogger(MainframeManager.class);

    private Provider provider;

    private ManagedChannel mainframeChannel;

    public MainframeManager(Provider provider) {
        this.provider = provider;

        mainframeChannel = ManagedChannelBuilder.forAddress(provider.getConfig().getMainframeIp(), provider.getConfig().getMainframePort()).usePlaintext().executor(provider.getThreadPool()).build();
    }

    public void initialRequest() {
        MainframeServiceGrpc.MainframeServiceStub stub = MainframeServiceGrpc.newStub(mainframeChannel);

        stub.connectionAttempt(ConnectToMainframeRequest.newBuilder().build(), new StreamObserver<ConnectToMainframeResponse>() {
            @Override
            public void onNext(ConnectToMainframeResponse response) {
                if (!response.getSuccess()) {
                    logger.info("Mainframe refused connection .. Abandoning.");
                    System.exit(0);
                    return;
                }

                logger.info("Mainframe connection succeeded. Provider ID: " + response.getProviderId());
                provider.setId(response.getProviderId());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.error("An error occurred while attempting to connect to mainframe .. Abandoning.");
                System.exit(0);
            }

            @Override
            public void onCompleted() { }
        });
    }
}
