package net.devibot.provider.manager;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devibot.core.Core;
import net.devibot.core.entities.DeviGuild;
import net.devibot.core.entities.Language;
import net.devibot.core.entities.Translation;
import net.devibot.core.entities.User;
import net.devibot.core.request.Request;
import net.devibot.core.request.RequestBuilder;
import net.devibot.grpc.mainframe.MainframeServiceGrpc;
import net.devibot.grpc.messages.*;
import net.devibot.provider.Provider;
import net.devibot.provider.utils.Translator;
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

    private String ip;
    private int port;
    private int id;

    public MainframeManager(Provider provider) {
        this.provider = provider;

        this.mainframeStub = MainframeServiceGrpc.newStub(ManagedChannelBuilder.forAddress(provider.getConfig().getMainframeIp(),
                provider.getConfig().getMainframePort()).usePlaintext().executor(provider.getThreadPool()).build());

        Request.Response response = new RequestBuilder().setURL("http://checkip.amazonaws.com/").setRequestType(Request.Type.GET).build().executeSync();
        if (response == null) {
            logger.error("Couldn't figure out ip!!");
            System.exit(100);
        }

        this.ip = Core.CONFIG.isDevMode() ? "127.0.0.1" : response.getBody().replace("\n", "");
        this.port = provider.getConfig().getPort();
    }

    public void initialRequest(boolean init) {
        initialRequest(init, null);
    }

    public void initialRequest(boolean init, Consumer<Boolean> success) {
        try {
            AtomicBoolean block = new AtomicBoolean(init);

            mainframeStub.connectionAttempt(ConnectToMainframeRequest.newBuilder().setIp(ip).setPort(provider.getConfig().getPort()).build(), new StreamObserver<ConnectToMainframeResponse>() {
                @Override
                public void onNext(ConnectToMainframeResponse connectToMainframeResponse) {
                    block.set(false);

                    if (init) {
                        if (connectToMainframeResponse.getSuccess()) {
                            logger.info("(X) Mainframe initialized successfully. ");
                            provider.setString("[#" + connectToMainframeResponse.getProviderId() + "/" + ip + ":" + provider.getConfig().getPort() + "]");
                            provider.initializeDiscordBot();
                        } else {
                            logger.error("!!! --- !!! Mainframe denied connection !!! --- !!!");
                            System.exit(0);
                        }
                    } else {
                        if (success != null)
                            success.accept(connectToMainframeResponse.getSuccess());
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (init) {
                        if (throwable instanceof StatusRuntimeException && ((StatusRuntimeException) throwable).getStatus().getCode() == Status.Code.UNAVAILABLE) {
                            logger.error("!!! --- !!! Mainframe offline or not reachable !!! --- !!!");
                        } else logger.error("", throwable);
                        System.exit(0);
                    } else {
                        if (success != null)
                            success.accept(false);
                    }
                }

                @Override
                public void onCompleted() { }
            });

            //block the thread so we don't close the JVM yet
            while (block.get());
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
    }

    public void getAllTranslations(Consumer<List<Translation>> consumer) {
        mainframeStub.getAllTranslations(Empty.newBuilder().build(), new StreamObserver<TranslationResponse>() {
            @Override
            public void onNext(TranslationResponse translationResponse) {
                List<Translation> translations = translationResponse.getTranslationsList().stream()
                        .map(Translation::new)
                        .collect(Collectors.toList());

                consumer.accept(translations);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                System.exit(1);
            }

            @Override
            public void onCompleted() { }
        });
    }

    public void getTranslationsForLanguageOLD(String language, Consumer<HashMap<Integer, String>> consumer) {
        mainframeStub.getTranslationsOLD(TranslationRequest.newBuilder().setLanguage(language).build(), new StreamObserver<TranslationResponseOLD>() {
            @Override
            public void onNext(TranslationResponseOLD translationResponseOLD) {
                HashMap<Integer, String> translations = new HashMap<>();
                translationResponseOLD.getTranslationsList().forEach(translation -> {
                    if (!translation.getText().equals("none") || translation.getText() == null)
                        translations.put(translation.getId(), translation.getText());
                });
                consumer.accept(translations);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warn("Failed to retrieve OLD translation data. See exception above.");
                consumer.accept(new HashMap<>());
            }

            @Override
            public void onCompleted() { }
        });
    }

    public void registerTranslation(String key, String text, int idOld, Consumer<? super Boolean> consumer) {
        List<net.devibot.grpc.entities.Translation> old = new ArrayList<>();

        if (idOld != -1) {
            for (Language trans : Language.values()) {
                if (trans == Language.ENGLISH) continue;
                if (Translator.hasTranslationOLD(trans, idOld))
                    old.add(new Translation(trans.getRegistry(), Translator.getTranslationOLD(trans, idOld)).toGrpc());
                else old.add(new Translation(trans.getRegistry(), "none").toGrpc());
            }
        }

        mainframeStub.registerTranslation(RegisterTranslationRequest.newBuilder().setKey(key).setText(text).addAllOld(old).build(), new StreamObserver<DefaultSuccessResponse>() {
            @Override
            public void onNext(DefaultSuccessResponse defaultSuccessResponse) {
                 consumer.accept(defaultSuccessResponse.getSuccess());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("" , throwable);
                consumer.accept(false);
            }

            @Override
            public void onCompleted() { }
        });
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

    public void saveDeviGuild(DeviGuild deviGuild) {
        mainframeStub.saveDeviGuild(DeviGuildSettingsSaveRequest.newBuilder().setGuild(deviGuild.toGrpc()).build(), new StreamObserver<DefaultSuccessResponse>() {
            @Override
            public void onNext(DefaultSuccessResponse defaultSuccessResponse) {
                //we weren't successful .. try again
                if (!defaultSuccessResponse.getSuccess())
                    saveDeviGuild(deviGuild);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.warn("Failed to request guild settings save");
            }

            @Override
            public void onCompleted() { }
        });
    }

    public void getUser(String userId, Consumer<? super User> consumer) {
        mainframeStub.getUser(UserRequest.newBuilder().setUser(userId).build(), new StreamObserver<net.devibot.grpc.entities.User>() {
            @Override
            public void onNext(net.devibot.grpc.entities.User user) {
                User entityUser = new User(user);

                //no db entry .. let's se if we have the user cached in the ShardManager
                if (entityUser.isError()) {
                    net.dv8tion.jda.core.entities.User jdaUser = provider.getDiscordBot().getShardManager().getUserById(userId);
                    //they're cached inside the ShardManager
                    if (jdaUser != null) {
                        entityUser = new User(jdaUser.getId(), jdaUser.getName(), jdaUser.getDiscriminator());
                    }
                }

                consumer.accept(entityUser);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.warn("Failed to retrieve user data. See exception above.");
                consumer.accept(new User());
            }

            @Override
            public void onCompleted() { }
        });
    }

    public void saveUser(User user) {
        mainframeStub.saveUser(UserDataSaveRequest.newBuilder().setUser(user.toGrpc()).build(), new StreamObserver<DefaultSuccessResponse>() {
            @Override
            public void onNext(DefaultSuccessResponse defaultSuccessResponse) {
                //we weren't successful .. try again
                if (!defaultSuccessResponse.getSuccess())
                    saveUser(user);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("", throwable);
                logger.warn("Failed to request user data save");
            }

            @Override
            public void onCompleted() { }
        });
    }
}
