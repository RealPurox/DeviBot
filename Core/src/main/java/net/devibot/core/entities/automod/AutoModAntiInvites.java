package net.devibot.core.entities.automod;

public class AutoModAntiInvites {

    private boolean enabled = true;
    private boolean strikes = true;

    public AutoModAntiInvites() { }

    public AutoModAntiInvites(net.devibot.grpc.entities.AutoModAntiInvites entity) {
        this.setEnabled(entity.getEnabled());
        this.setStrikes(entity.getStrikes());
    }

    public net.devibot.grpc.entities.AutoModAntiInvites toGrpc() {
        return net.devibot.grpc.entities.AutoModAntiInvites.newBuilder()
                .setEnabled(this.enabled)
                .setStrikes(this.strikes)
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isStrikes() {
        return strikes;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setStrikes(boolean strikes) {
        this.strikes = strikes;
    }
}
