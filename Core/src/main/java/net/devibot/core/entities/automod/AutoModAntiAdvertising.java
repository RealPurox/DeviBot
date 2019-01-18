package net.devibot.core.entities.automod;

import net.devibot.core.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoModAntiAdvertising {

    private boolean enabled = true;
    private boolean strikes = true;
    private List<AutoModAntiAdvertisingLink> links = new ArrayList<>();

    public AutoModAntiAdvertising() { }

    public AutoModAntiAdvertising(net.devibot.grpc.entities.AutoModAntiAdvertising entity) {
        this.setEnabled(entity.getEnabled());
        this.setStrikes(entity.getStrikes());
        this.setLinks(entity.getLinksList().stream().map(AutoModAntiAdvertisingLink::new).collect(Collectors.toList()));
    }

    public net.devibot.grpc.entities.AutoModAntiAdvertising toGrpc() {
        return net.devibot.grpc.entities.AutoModAntiAdvertising.newBuilder()
                .setEnabled(this.enabled)
                .setStrikes(this.strikes)
                .addAllLinks(links.stream()
                        .map(link -> net.devibot.grpc.entities.AutoModAntiAdvertisingLink.newBuilder()
                                .setEnabled(link.isEnabled())
                                .setName(link.getName())
                                .setRegex(link.getRegex())
                                .build()).collect(Collectors.toList()))
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isStrikes() {
        return strikes;
    }

    public List<AutoModAntiAdvertisingLink> getLinks() {
        return links;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setStrikes(boolean strikes) {
        this.strikes = strikes;
    }

    public void setLinks(List<AutoModAntiAdvertisingLink> links) {
        this.links = links;
    }
}
