package me.purox.devi.commandsold.music;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;
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
    public void execute(String[] args, ICommand command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());
        AudioInfo current = guildPlayer.getCurrent();

        if (current == null) {
            sender.reply(Emote.ERROR.get() + " | I'm not playing music at the moment!");
            return;
        }

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#36393E"));

        builder.setAuthor(devi.getTranslation(command.getLanguage(), 479));
        builder.addField(devi.getTranslation(command.getLanguage(), 480), "[" + current.getAudioTrack().getInfo().title + "](" + current.getAudioTrack().getInfo().uri + ")\n\n", false);
        builder.addField(devi.getTranslation(command.getLanguage(), 481), current.getRequester().getName() + "#" + current.getRequester().getDiscriminator() + "\n\n", false);

        String position = devi.getMusicManager().getTrackTime(current.getAudioTrack().getPosition()).replaceAll("[()]", "");
        String duration = devi.getMusicManager().getTrackTime(current.getAudioTrack().getDuration()).replaceAll("[()]", "");

        double percent = ((double) current.getAudioTrack().getPosition() / (double)current.getAudioTrack().getDuration()) * 10;
        int progress = (int) Math.round(percent);

        StringBuilder progressBar = new StringBuilder(" ~~**[");
        for (int i = 0; i < progress; i++) {
            progressBar.append("--");
        }
        progressBar.append("](https://www.devibot.net)");
        for (int i = 0; i < 10 - progress; i++) {
            progressBar.append("--");
        }
        progressBar.append("**~~ ");

        builder.addField(devi.getTranslation(command.getLanguage(), 482), position + progressBar.toString() + duration, false);

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 128;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("playing", "nowplaying", "np");
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
