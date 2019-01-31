package net.devibot.provider.manager;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devibot.core.entities.DeviGuild;
import net.devibot.core.entities.Strike;
import net.devibot.core.entities.User;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.*;
import net.devibot.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    public void getDeviGuild(String id, Consumer<? super DeviGuild> consumer) {
        mainframeStub.getDeviGuild(DeviGuildRequest.newBuilder().setId(id).build(), new StreamObserver<net.devibot.grpc.entities.DeviGuild>() {
            @Override
            public void onNext(net.devibot.grpc.entities.DeviGuild deviGuild) {
                consumer.accept(new DeviGuild(deviGuild));
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.warn("Failed to retrieve guild data. See exception above.");
                consumer.accept(new DeviGuild(id));
            }

            @Override
            public void onCompleted() { }
        });
    }

    public void getTranslationsForLanguage(String language, Consumer<HashMap<Integer, String>> consumer) {
        mainframeStub.getTranslations(TranslationRequest.newBuilder().setLanguage(language).build(), new StreamObserver<TranslationResponse>() {
            @Override
            public void onNext(TranslationResponse translationResponse) {
                HashMap<Integer, String> translations = new HashMap<>();
                translationResponse.getTranslationsList().forEach(translation -> {
                    if (!translation.getText().equals("none") || translation.getText() == null)
                        translations.put(translation.getId(), translation.getText());
                });
                consumer.accept(translations);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.warn("Failed to retrieve translation data. See exception above.");
                consumer.accept(new HashMap<>());
            }

            @Override
            public void onCompleted() { }
        });
    }

    public void requestDeviGuildSettingsSave(DeviGuild deviGuild) {
        mainframeStub.requestDeviGuildSettingsSave(DeviGuildSettingsSaveRequest.newBuilder().setGuild(deviGuild.toGrpc()).build(), new StreamObserver<DefaultSuccessResponse>() {
            @Override
            public void onNext(DefaultSuccessResponse defaultSuccessResponse) { }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.warn("Failed to request guild settings save");
            }

            @Override
            public void onCompleted() { }
        });
    }

    public void getUser(String user, Consumer<? super User> consumer) {
        mainframeStub.getUser(UserRequest.newBuilder().setUser(user).build(), new StreamObserver<net.devibot.grpc.entities.User>() {
            @Override
            public void onNext(net.devibot.grpc.entities.User user) {
                consumer.accept(new User(user));
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.warn("Failed to retrieve strike data. See exception above.");
                consumer.accept(new User());
            }

            @Override
            public void onCompleted() { }
        });
    }
}
