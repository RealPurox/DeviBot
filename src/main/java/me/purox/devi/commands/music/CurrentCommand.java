package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class CurrentCommand extends ICommand {

    private Devi devi;

    public CurrentCommand(Devi devi) {
        super("current", "playing", "nowplaying", "np");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());
        AudioInfo current = guildPlayer.getCurrent();

        if (current == null) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 465));
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
}
