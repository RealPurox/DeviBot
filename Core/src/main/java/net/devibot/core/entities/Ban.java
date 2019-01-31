package net.devibot.core.entities;

public class Ban {

    private boolean active;
    private String punisher;
    private String reason;
    private long time;

    public Ban() {
        this.active = false;
        this.punisher = "";
        this.reason = "";
        this.time = 0L;
    }

    public Ban(String punisher, String reason) {
        this.active = true;
        this.time = System.currentTimeMillis();
        this.punisher = punisher;
        this.reason = reason;
    }

    public Ban(net.devibot.grpc.entities.Ban ban) {
        this.active = ban.getActive();
        this.punisher = ban.getPunisher();
        this.reason = ban.getReason();
        this.time = ban.getTime();
    }

    public net.devibot.grpc.entities.Ban toGrpc() {
        return net.devibot.grpc.entities.Ban.newBuilder()
                .setActive(this.active)
                .setPunisher(this.punisher)
                .setReason(this.reason)
                .setTime(this.time)
                .build();
    }

}
