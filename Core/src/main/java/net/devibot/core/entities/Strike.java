package net.devibot.core.entities;

import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

public class Strike {

    private String user;
    private String guild;
    private String reason = "Unknown Reason";
    private long time;

    public Strike() {
        this.user = "-1";
        this.guild = "-1";
        this.time = 0L;
    }

    public Strike(@NotNull String user, @NotNull String guild, @Nullable String reason, long time) {
        this.user = user;
        this.guild = guild;
        if (reason != null)
            this.reason = reason;
        this.time = time;
    }

    public Strike(net.devibot.grpc.entities.Strike strike) {
        this.user = strike.getUser();
        this.guild = strike.getGuild();
        this.reason = strike.getReason();
        this.time = strike.getTime();
    }

    public net.devibot.grpc.entities.Strike toGrpc() {
        return net.devibot.grpc.entities.Strike.newBuilder()
                .setUser(user)
                .setGuild(guild)
                .setReason(reason)
                .setTime(time)
                .build();
    }

}
