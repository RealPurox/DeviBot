package me.purox.devi.commands.handler;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;

import javax.annotation.CheckReturnValue;
import java.util.List;

public interface ConsoleCommandSender extends ISnowflake, IMentionable, IFakeable {
    
    String getName();

    String getDiscriminator();

    String getAvatarId();

    String getAvatarUrl();

    String getDefaultAvatarId();

    String getDefaultAvatarUrl();

    String getEffectiveAvatarUrl();

    boolean hasPrivateChannel();

    @CheckReturnValue
    RestAction<PrivateChannel> openPrivateChannel();

    List<Guild> getMutualGuilds();

    boolean isBot();

    JDA getJDA();
}