package net.devibot.provider.manager;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.ConnectToMainframeRequest;
import net.devibot.grpc.messages.ConnectToMainframeResponse;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;

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

        String ip = null;

        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.error("", e);
            logger.error("Failed to figure out ip address. Abandoning.");
            System.exit(0);
        }

        ConnectToMainframeRequest.Builder builder = ConnectToMainframeRequest.newBuilder();
        builder.setPort(provider.getConfig().getPort());
        builder.setIp(ip);

        stub.connectionAttempt(builder.build(), new StreamObserver<ConnectToMainframeResponse>() {
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
                if (throwable instanceof StatusRuntimeException && ((StatusRuntimeException) throwable).getStatus().getCode() == Status.Code.UNAVAILABLE) {
                    logger.error("!!! -- !!! Mainframe is offline !!! -- !!!");
                    logger.error("!!! -- !!! Mainframe is offline !!! -- !!!");
                    logger.error("!!! -- !!! Mainframe is offline !!! -- !!!");
                    System.exit(0);
                }

                logger.error("", throwable);
                System.exit(0);
            }

            @Override
            public void onCompleted() { }
        });
    }
}
