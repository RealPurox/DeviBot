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

    private int globalIds = 1;

    private Mainframe mainframe;
    private List<Provider> providers = new ArrayList<>();

    public ProviderManager(Mainframe mainframe) {
        this.mainframe = mainframe;
    }

    public Provider registerProvider(String ip, int port) {
        Provider provider = new Provider(ip, port, figureOutProviderId(ip, port), mainframe.getThreadPool());
        this.providers.add(provider);
        return provider;
    }

    private int figureOutProviderId(String ip, int port) {
        int id = -1;

        for (Provider provider : providers) {
            //provider initialized again so give it the id back
            if (provider.getIp().equals(ip) && provider.getPort() == port) {
                id = provider.getId();
            }
        }

        return id == -1 ? globalIds++ : id;
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
                    provider.increaseKeepAliveFailure();
                }

                @Override
                public void onCompleted() { }
            });
        }
    }

}
