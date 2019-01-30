package net.devibot.mainframe.service;

import io.grpc.stub.StreamObserver;
import net.devibot.core.Core;
import net.devibot.core.database.DatabaseManager;
import net.devibot.grpc.entities.DeviGuild;
import net.devibot.grpc.entities.Strike;
import net.devibot.grpc.entities.Translation;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.*;
import net.devibot.mainframe.Mainframe;
import net.devibot.mainframe.agents.SettingsUpdateAgent;
import net.devibot.mainframe.manager.AgentManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                return;
            }

            Document guildData = results.get(0);
            deviGuild = Core.GSON.fromJson(guildData.toJson(), net.devibot.core.entities.DeviGuild.class).toGrpc();
        } catch (Exception e) {
            logger.error("", e);
            deviGuild = new net.devibot.core.entities.DeviGuild(guildId).toGrpc();
        } finally {
            responseObserver.onNext(deviGuild);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getTranslations(TranslationRequest request, StreamObserver<TranslationResponse> responseObserver) {
        String language = request.getLanguage();

        try {
            DatabaseManager databaseManager = DatabaseManager.getInstance();

            List<Translation> grpcTranslations = new ArrayList<>();
            for (Document document : databaseManager.getDatabase().getCollection("translations").find()) {
                grpcTranslations.add(Translation.newBuilder().setId(Integer.parseInt(document.getString("_id"))).setText(document.getString(language) == null ? "none" : document.getString(language)).build());
            }
            responseObserver.onNext(TranslationResponse.newBuilder().addAllTranslations(grpcTranslations).build());
        } catch (Exception e) {
            logger.error("", e);
            responseObserver.onNext(TranslationResponse.newBuilder().build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void requestDeviGuildSettingsSave(DeviGuildSettingsSaveRequest request, StreamObserver<DefaultSuccessResponse> responseObserver) {
        ((SettingsUpdateAgent)mainframe.getAgentManager().getAgents(AgentManager.Type.SETTINGS_UPDATE).get(0))
                .appendQueue(new net.devibot.core.entities.DeviGuild(request.getGuild()));
        responseObserver.onNext(DefaultSuccessResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getStrikes(StrikeRequest request, StreamObserver<StrikeResponse> responseObserver) {
        try {
            String user = request.getUser();
            String guild = request.getGuild();

            Map<String, String> filter = new HashMap<>();
            filter.put("guild", guild);

            List<Document> result = DatabaseManager.getInstance().getDocuments("user", user, filter, "strikes");
            List<Strike> strikes = result.stream().map(res -> Core.GSON.fromJson(res.toJson(), net.devibot.core.entities.Strike.class).toGrpc()).collect(Collectors.toList());

            responseObserver.onNext(StrikeResponse.newBuilder().addAllStrikes(strikes).build());
        } catch (Exception e) {
            logger.error("", e);
            responseObserver.onNext(StrikeResponse.newBuilder().build());
        } finally {
            responseObserver.onCompleted();
        }
    }
}
