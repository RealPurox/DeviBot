package me.purox.devi.commands.handler;

import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.ArrayList;
import java.util.List;

public class ConsoleCommandSenderImpl implements ConsoleCommandSender {

    private Devi devi;

    public ConsoleCommandSenderImpl (Devi devi) {
        this.devi = devi;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public String getDiscriminator() {
        return "5555";
    }

    @Override
    public String getAvatarId() {
        return null;
    }

    @Override
    public String getAvatarUrl() {
        return null;
    }

    @Override
    public String getDefaultAvatarId() {
        return UserImpl.DefaultAvatar.values()[Integer.parseInt(getDiscriminator()) % UserImpl.DefaultAvatar.values().length].toString();
    }

    @Override
    public String getDefaultAvatarUrl() {
        return "https://discordapp.com/assets/" + getDefaultAvatarId() + ".png";
    }

    @Override
    public String getEffectiveAvatarUrl() {
        return getAvatarUrl() == null ? getDefaultAvatarUrl() : getAvatarUrl();
    }

    @Override
    public boolean hasPrivateChannel() {
        return false;
    }

    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        throw new IllegalStateException("Cannot open a PrivateChannel with the console");
    }

    @Override
    public List<Guild> getMutualGuilds() {
        return new ArrayList<>();
    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public JDA getJDA() {
        return devi.getShardManager().getShards().get(0);
    }

    @Override
    public boolean isFake() {
        return true;
    }

    @Override
    public String getAsMention() {
        return "@CONSOLE";
    }

    @Override
    public long getIdLong() {
        return 0;
    }
}
