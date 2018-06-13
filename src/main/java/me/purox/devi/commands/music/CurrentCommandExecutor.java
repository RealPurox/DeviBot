package me.purox.devi.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class CurrentCommandExecutor implements CommandExecutor {

    private Devi devi;
    public CurrentCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        AudioPlayer player = devi.getMusicManager().getPlayer(command.getEvent().getGuild());
        if (player.getPlayingTrack() == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 129));
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 130), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg");
        builder.addField(devi.getTranslation(command.getLanguage(), 87), player.getPlayingTrack().getInfo().title, false);
        builder.addField(devi.getTranslation(command.getLanguage(), 98), devi.getMusicManager().getTimestamp(player.getPlayingTrack().getPosition(), command.getLanguage()) + "/" + devi.getMusicManager().getTimestamp(player.getPlayingTrack().getDuration(), command.getLanguage()), false);

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 128;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("playing", "nowplaying");
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MUSIC;
    }
}
