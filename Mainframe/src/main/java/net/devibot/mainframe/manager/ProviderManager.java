package net.devibot.mainframe.manager;

import io.grpc.stub.StreamObserver;
import net.devibot.grpc.messages.DefaultSuccessResponse;
import net.devibot.grpc.messages.Empty;
import net.devibot.mainframe.Mainframe;
import net.devibot.mainframe.entities.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProviderManager {

    private static Logger logger = LoggerFactory.getLogger(ProviderManager.class);

    private Mainframe mainframe;
    private List<Provider> providers = new ArrayList<>();

    public ProviderManager(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    public Provider registerProvider(String ip, int port) {
        Provider provider = new Provider(ip, port, mainframe.getThreadPool());
        this.providers.add(provider);
        return provider;
    }

    public void sendKeepAliveMessage() {
        for (Provider provider : providers) {
            provider.getStub().keepAlive(Empty.newBuilder().build(), new StreamObserver<DefaultSuccessResponse>() {
                @Override
                public void onNext(DefaultSuccessResponse defaultSuccessResponse) {
                    boolean success = defaultSuccessResponse.getSuccess();

                    if (success)
                        provider.resetKeepAliveFailure();
                    else
                        provider.increaseKeepAliveFailure();
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    provider.increaseKeepAliveFailure();
                }

                @Override
                public void onCompleted() { }
            });
        }
    }

}
