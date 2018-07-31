package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VolumeCommandExecutor implements CommandExecutor {

    private Devi devi;
    //message id -> message, volume
    private ExpiringMap<String, Map.Entry<Message, Integer>> volumeMap;

    public VolumeCommandExecutor(Devi devi) {
        this.devi = devi;
        this.volumeMap = ExpiringMap.builder().variableExpiration().build();
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());
        if (args.length == 0) {
            sender.reply(getVolumeEmbed(guildPlayer), message -> {
                if (!MessageUtils.addReactions(message, "\u2795", "\u2796")) {
                    sender.reply(":warning: | I don't have permission to add reactions to messages, please use {0} to change the volume.".replace("{0}", "`"+ command.getPrefix() + "volume <1 - 150>`"));
                    return;
                }
                volumeMap.put(message.getId(), new AbstractMap.SimpleEntry<>(message, guildPlayer.getAudioPlayer().getVolume()), ExpirationPolicy.ACCESSED, 5, TimeUnit.MINUTES);
            });
        }
    }

    private MessageEmbed getVolumeEmbed(GuildPlayer guildPlayer) {
        EmbedBuilder builder = new EmbedBuilder().setTitle("Volume");
        int volume = guildPlayer.getAudioPlayer().getVolume() / 10;

        StringBuilder progressBar = new StringBuilder(" ~~**[");
        for (int i = 0; i < volume; i++) {
            progressBar.append("--");
        }
        progressBar.append("](https://www.devibot.net)");
        for (int i = 0; i < 15 - volume; i++) {
            progressBar.append("--");
        }
        progressBar.append("**~~ ");

        builder.setDescription("`" + guildPlayer.getAudioPlayer().getVolume() + "` " + progressBar.toString() + " `150`");
        builder.setFooter("Use the reactions below to change the volume", null);
        return builder.build();
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return null;
    }
}
