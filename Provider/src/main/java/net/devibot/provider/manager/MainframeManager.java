package net.devibot.provider.manager;

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

import java.util.concurrent.atomic.AtomicBoolean;

public class MainframeManager {

    private static Logger logger = LoggerFactory.getLogger(MainframeManager.class);

    private Provider provider;

    private MainframeServiceGrpc.MainframeServiceStub mainframeStub;

    public MainframeManager(Provider provider) {
        this.provider = provider;

        mainframeStub = MainframeServiceGrpc.newStub(ManagedChannelBuilder.forAddress(provider.getConfig().getMainframeIp(),
                provider.getConfig().getMainframePort()).usePlaintext().executor(provider.getThreadPool()).build());
    }

    public void initialRequest() {
        try {
            AtomicBoolean block = new AtomicBoolean(true);

            mainframeStub.connectionAttempt(ConnectToMainframeRequest.newBuilder().build(), new StreamObserver<ConnectToMainframeResponse>() {
                @Override
                public void onNext(ConnectToMainframeResponse connectToMainframeResponse) {
                    block.set(false);
                    logger.info("(X) Mainframe initialized successfully");
                    provider.initializeDiscordBot();
                }

                @Override
                public void onError(Throwable throwable) {
                    if (throwable instanceof StatusRuntimeException && ((StatusRuntimeException) throwable).getStatus().getCode() == Status.Code.UNAVAILABLE) {
                        logger.error("!!! --- !!! Mainframe offline or not reachable !!! --- !!!");
                        logger.error("!!! --- !!! Mainframe offline or not reachable !!! --- !!!");
                        logger.error("!!! --- !!! Mainframe offline or not reachable !!! --- !!!");
                    } else logger.error("", throwable);
                    System.exit(0);
                }

                @Override
                public void onCompleted() { }
            });

            //block the thread so we don't close the JVM yet
            while (block.get());
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
