package net.devibot.mainframe.grpc;

import io.grpc.stub.StreamObserver;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.ConnectToMainframeRequest;
import net.devibot.grpc.messages.ConnectToMainframeResponse;
import net.devibot.mainframe.Mainframe;

public class GrpcService extends MainframeServiceGrpc.MainframeServiceImplBase{

    private Mainframe mainframe;

    public GrpcService(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    @Override
    public void connectionAttempt(ConnectToMainframeRequest request, StreamObserver<ConnectToMainframeResponse> responseObserver) {
        //todo
        responseObserver.onNext(ConnectToMainframeResponse.newBuilder().setProviderId(5).setSuccess(true).build());
    }

}
