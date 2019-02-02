package net.devibot.mainframe.service;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import io.grpc.stub.StreamObserver;
import net.devibot.core.Core;
import net.devibot.core.database.DatabaseManager;
import net.devibot.grpc.entities.DeviGuild;
import net.devibot.grpc.entities.Translation;
import net.devibot.grpc.entities.User;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.*;
import net.devibot.mainframe.Mainframe;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
                return;
            }

            Document guildData = results.get(0);

            String json = JSON.serialize(guildData);
            deviGuild = Core.GSON.fromJson(json, net.devibot.core.entities.DeviGuild.class).toGrpc();
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
    public void saveDeviGuild(DeviGuildSettingsSaveRequest request, StreamObserver<DefaultSuccessResponse> responseObserver) {
        net.devibot.core.entities.DeviGuild deviGuild = new net.devibot.core.entities.DeviGuild(request.getGuild());
        Document document = new Document(new JSONObject(Core.GSON.toJson(deviGuild)).toMap());
        UpdateResult result = databaseManager.saveToDatabase("guilds", document, deviGuild.getId());

        responseObserver.onNext(DefaultSuccessResponse.newBuilder().setSuccess(result.wasAcknowledged()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(UserRequest request, StreamObserver<User> responseObserver) {
        try {
            String userId = request.getUser();

            //general data & ban
            Document data = DatabaseManager.getInstance().getDocument(userId, "users");

            //strikes
            List<Document> strikeDocs = DatabaseManager.getInstance().getDocuments("user", userId, "strikes");
            data.put("strikes", strikeDocs);


            String json = JSON.serialize(data);
            responseObserver.onNext(Core.GSON.fromJson(json, net.devibot.core.entities.User.class).toGrpc());
        } catch (Exception e) {
            logger.error("", e);
            responseObserver.onNext(User.newBuilder().build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void saveUser(UserDataSaveRequest request, StreamObserver<DefaultSuccessResponse> responseObserver) {
        net.devibot.core.entities.User user = new net.devibot.core.entities.User(request.getUser());
        Document document = new Document(new JSONObject(Core.GSON.toJson(user)).toMap());
        UpdateResult result = databaseManager.saveToDatabase("users", document, user.getId());

        responseObserver.onNext(DefaultSuccessResponse.newBuilder().setSuccess(result.wasAcknowledged()).build());
        responseObserver.onCompleted();
    }
}
