package net.devibot.core.entities.automod;

public class AutoModAntiSpam {

    private boolean enabled = true;
    private boolean strikes = true;
    private int amount = 4;
    private int period = 10;

    public AutoModAntiSpam() { }

    public AutoModAntiSpam(net.devibot.grpc.entities.AutoModAntiSpam entity) {
        this.setEnabled(entity.getEnabled());
        this.setStrikes(entity.getStrikes());
        this.setAmount(entity.getAmount());
        this.setPeriod(entity.getPeriod());
    }

    public net.devibot.grpc.entities.AutoModAntiSpam toGrpc() {
        return net.devibot.grpc.entities.AutoModAntiSpam.newBuilder()
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
