package net.devibot.core.entities.automod;

public class AutoModAntiMassMention {

    private boolean enabled = true;
    private boolean strikes = true;
    private int amount = 5;
    private int period = 10;

    public AutoModAntiMassMention() { }

    public AutoModAntiMassMention(net.devibot.grpc.entities.AutoModAntiMassMention entity) {
        this.setEnabled(entity.getEnabled());
        this.setStrikes(entity.getStrikes());
        this.setAmount(entity.getAmount());
        this.setPeriod(entity.getPeriod());
    }

    public net.devibot.grpc.entities.AutoModAntiMassMention toGrpc() {
        return net.devibot.grpc.entities.AutoModAntiMassMention.newBuilder()
                .setEnabled(this.enabled)
                .setStrikes(this.strikes)
                .setAmount(this.amount)
                .setPeriod(this.period)
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isStrikes() {
        return strikes;
    }

    public int getAmount() {
        return amount;
    }

    public int getPeriod() {
        return period;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setStrikes(boolean strikes) {
        this.strikes = strikes;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

}
