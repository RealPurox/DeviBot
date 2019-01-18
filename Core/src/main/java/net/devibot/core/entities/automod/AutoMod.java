package net.devibot.core.entities.automod;

import com.google.gson.annotations.SerializedName;

public class AutoMod {

    private boolean enabled = false;
    @SerializedName("anti_invites")
    private AutoModAntiInvites antiInvites = new AutoModAntiInvites();
    @SerializedName("anti_advertising")
    private AutoModAntiAdvertising antiAdvertising = new AutoModAntiAdvertising();
    @SerializedName("anti_spam")
    private AutoModAntiSpam antiSpam = new AutoModAntiSpam();
    @SerializedName("anti_mass_mention")
    private AutoModAntiMassMention antiMassMention = new AutoModAntiMassMention();

    public AutoMod() { }

    public AutoMod(net.devibot.grpc.entities.AutoMod entity) {
        this.setEnabled(entity.getEnabled());
        this.setAntiInvites(new AutoModAntiInvites(entity.getAntiInvites()));
        this.setAntiAdvertising(new AutoModAntiAdvertising(entity.getAntiAdvertising()));
        this.setAntiSpam(new AutoModAntiSpam(entity.getAntiSpam()));
        this.setAntiMassMention(new AutoModAntiMassMention(entity.getAntiMassMention()));
    }

    public net.devibot.grpc.entities.AutoMod toGrpc() {
        return net.devibot.grpc.entities.AutoMod.newBuilder()
                .setEnabled(this.enabled)
                .setAntiInvites(this.antiInvites.toGrpc())
                .setAntiAdvertising(this.antiAdvertising.toGrpc())
                .setAntiSpam(this.antiSpam.toGrpc())
                .setAntiMassMention(this.antiMassMention.toGrpc())
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public AutoModAntiInvites getAntiInvites() {
        return antiInvites;
    }

    public AutoModAntiAdvertising getAntiAdvertising() {
        return antiAdvertising;
    }

    public AutoModAntiSpam getAntiSpam() {
        return antiSpam;
    }

    public AutoModAntiMassMention getAntiMassMention() {
        return antiMassMention;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAntiInvites(AutoModAntiInvites antiInvites) {
        this.antiInvites = antiInvites;
    }

    public void setAntiAdvertising(AutoModAntiAdvertising antiAdvertising) {
        this.antiAdvertising = antiAdvertising;
    }

    public void setAntiSpam(AutoModAntiSpam antiSpam) {
        this.antiSpam = antiSpam;
    }

    public void setAntiMassMention(AutoModAntiMassMention antiMassMention) {
        this.antiMassMention = antiMassMention;
    }
}
