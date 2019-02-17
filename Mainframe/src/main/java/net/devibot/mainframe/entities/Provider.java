package net.devibot.mainframe.entities;

import io.grpc.ManagedChannelBuilder;
import net.devibot.core.Core;
import net.devibot.core.utils.DiscordWebhook;
import net.devibot.grpc.provider.ProviderServiceGrpc;

import java.util.concurrent.Executor;

public class Provider {

    private ProviderServiceGrpc.ProviderServiceStub stub;

    private String ip;
    private int port;
    private int id;

    private int keepAliveFailure = 0;
    private boolean webhookSent = false;

    public Provider (String ip, int port, int id, Executor executor) {
        this.ip = ip;
        this.port = port;
        this.id = id;

        this.stub = ProviderServiceGrpc.newStub(ManagedChannelBuilder.forAddress(ip, port).usePlaintext().executor(executor).build());
    }

    public ProviderServiceGrpc.ProviderServiceStub getStub() {
        return stub;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

    public int getKeepAliveFailure() {
        return keepAliveFailure;
    }

    public void increaseKeepAliveFailure() {
        this.keepAliveFailure += 1;
        checkKeepAlive();
    }

    public void resetKeepAliveFailure() {
        this.keepAliveFailure = 0;
        checkKeepAlive();
    }

    public void checkKeepAlive() {
        if (keepAliveFailure >= 2 && !webhookSent) { //didn't respond for 30 seconds
            webhookSent = true;
            DiscordWebhook webhook = new DiscordWebhook(Core.CONFIG.getMonitoringRoomWebhook());
            webhook.setContent(":warning:__Something seems to be wrong__:warning:\n" + (Core.CONFIG.isDevMode() ? "<@222753093559910400>\n" : "@everyone\n") + "`mainframe`:\n```Provider " + toString() + " lost communication with Mainframe. Assuming provider crashed or was shut down.```");
            webhook.execute();
        }

        if (keepAliveFailure == 0 && webhookSent) {
            webhookSent = false;
            DiscordWebhook webhook = new DiscordWebhook(Core.CONFIG.getMonitoringRoomWebhook());
            webhook.setContent(":white_check_mark:__Issues have been resolved__ :white_check_mark:\n\n`mainframe`:\n```Mainframe restored communication with provider " + toString() + ".```");
            webhook.execute();
        }
    }

    @Override
    public String toString() {
        return "[#" + this.id + "/" + this.ip + ":" + this.port + "]";
    }
}
