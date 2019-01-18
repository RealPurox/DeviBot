package net.devibot.core.entities.automod;

public class AutoModAntiAdvertisingLink {

    boolean enabled = true;
    private String name = "";
    private String regex = "null";

    public AutoModAntiAdvertisingLink() { }

    public AutoModAntiAdvertisingLink(net.devibot.grpc.entities.AutoModAntiAdvertisingLink entity) {
        this.setEnabled(entity.getEnabled());
        this.setName(entity.getName());
        this.setRegex(entity.getRegex());
    }

    public net.devibot.grpc.entities.AutoModAntiAdvertisingLink toGrpc() {
        return net.devibot.grpc.entities.AutoModAntiAdvertisingLink.newBuilder()
                .setEnabled(this.enabled)
                .setName(this.name)
                .setRegex(this.regex)
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
