package net.devibot.core.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class User {

    @SerializedName("_id")
    private String id = "error";
    private String name = "N/A";
    private String discriminator = "0000";
    private String avatar = "";

    private Ban ban = new Ban();

    private List<Strike> strikes = new ArrayList<>(); // TODO: 31/01/2019 perhaps dont stroke all strikes as it might take a lot of memory if people have a lot of strikes on certain servers

    public User() { }

    public User(net.devibot.grpc.entities.User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.discriminator = user.getDiscriminator();
        this.avatar = user.getAvatar();
        this.ban = new Ban(user.getBan());
        this.strikes = user.getStrikesList().stream().map(Strike::new).collect(Collectors.toList());
    }

    public net.devibot.grpc.entities.User toGrpc() {
        return net.devibot.grpc.entities.User.newBuilder()
                .setId(this.id)
                .setName(this.name)
                .setDiscriminator(this.discriminator)
                .setAvatar(this.avatar)
                .setBan(this.ban.toGrpc())
                .addAllStrikes(this.strikes.stream().map(Strike::toGrpc).collect(Collectors.toList()))
                .build();
    }

}
