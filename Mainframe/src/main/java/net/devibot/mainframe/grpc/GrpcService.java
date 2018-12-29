package net.devibot.mainframe.grpc;

import io.grpc.stub.StreamObserver;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.ConnectToMainframeRequest;
import net.devibot.grpc.messages.ConnectToMainframeResponse;
import net.devibot.mainframe.Mainframe;
import net.devibot.mainframe.entities.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcService extends MainframeServiceGrpc.MainframeServiceImplBase{

    private final Logger logger = LoggerFactory.getLogger(GrpcService.class);

    private Mainframe mainframe;

    public GrpcService(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    @Override
    public void connectionAttempt(ConnectToMainframeRequest request, StreamObserver<ConnectToMainframeResponse> responseObserver) {
        try {

            Provider provider = mainframe.getProviderManager().connectProvider(request.getIp(), request.getPort());

            logger.info("Provider opened connection attempt ..");

            if (provider == null) {
                logger.info(".. connection attempt declined.");
                responseObserver.onNext(ConnectToMainframeResponse.newBuilder().setSuccess(false).build());
                return;
            }
            logger.info(".. connection attempt accepted. Provider was assigned to id " + provider.getId());
            responseObserver.onNext(ConnectToMainframeResponse.newBuilder().setProviderId(provider.getId()).setSuccess(true).build());
        } catch (Exception e) {
            logger.error("", e);
            responseObserver.onNext(ConnectToMainframeResponse.newBuilder().setSuccess(false).build());
        } finally {
            responseObserver.onCompleted();

        }
    }

}
