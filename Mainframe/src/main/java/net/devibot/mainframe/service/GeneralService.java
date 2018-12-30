package net.devibot.mainframe.service;

import io.grpc.stub.StreamObserver;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.ConnectToMainframeRequest;
import net.devibot.grpc.messages.ConnectToMainframeResponse;
import net.devibot.mainframe.Mainframe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralService extends MainframeServiceGrpc.MainframeServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(GeneralService.class);

    private Mainframe mainframe;

    public GeneralService(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    //Provider uses this to determine whether mainframe is online or offline
    @Override
    public void connectionAttempt(ConnectToMainframeRequest request, StreamObserver<ConnectToMainframeResponse> responseObserver) {
        logger.info("(X) Provider initialized connection.");
        responseObserver.onNext(ConnectToMainframeResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }
}
