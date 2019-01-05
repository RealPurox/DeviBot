package net.devibot.mainframe.service;

import io.grpc.stub.StreamObserver;
import net.devibot.core.Core;
import net.devibot.core.database.DatabaseManager;
import net.devibot.grpc.entities.DeviGuild;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.ConnectToMainframeRequest;
import net.devibot.grpc.messages.ConnectToMainframeResponse;
import net.devibot.grpc.messages.DeviGuildRequest;
import net.devibot.mainframe.Mainframe;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainframeService extends MainframeServiceGrpc.MainframeServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(MainframeService.class);

    private Mainframe mainframe;

    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    public MainframeService(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    //Provider uses this to determine whether mainframe is online or offline
    @Override
    public void connectionAttempt(ConnectToMainframeRequest request, StreamObserver<ConnectToMainframeResponse> responseObserver) {
        logger.info("(X) Provider initialized connection.");
        responseObserver.onNext(ConnectToMainframeResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getDeviGuild(DeviGuildRequest request, StreamObserver<DeviGuild> responseObserver) {
        String guildId = request.getId();
        DeviGuild deviGuild = null;

        try {
            List<Document> results = databaseManager.getDocuments("id", guildId, "guilds");

            if (results.isEmpty()) {
                deviGuild = new net.devibot.core.entities.DeviGuild(guildId).toGrpc();
                logger.info("");
                return;
            }

            Document guildData = results.get(0);
            deviGuild = Core.GSON.fromJson(guildData.toJson(), net.devibot.core.entities.DeviGuild.class).toGrpc();
        } catch (Exception e) {
            logger.error("", e);
            responseObserver.onNext(new net.devibot.core.entities.DeviGuild(guildId).toGrpc());
        } finally {
            responseObserver.onNext(deviGuild);
            responseObserver.onCompleted();
        }
    }
}
