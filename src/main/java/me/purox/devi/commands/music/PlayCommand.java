package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PlayCommand extends ICommand {

    private Devi devi;

    public PlayCommand(Devi devi) {
        super("play", "addqueue", "p");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());

        if (command.getArgs().length == 0) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 456));
            return;
        }

        VoiceChannel deviChannel = command.getGuild().getSelfMember().getVoiceState().getChannel();
        GuildVoiceState userState = command.getGuild().getMember(sender).getVoiceState();

        if (!userState.inVoiceChannel()) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 461));
            return;
        }

        if (deviChannel != null && userState.getChannel().getIdLong() != deviChannel.getIdLong()) {
            sender.reply(Emote.ERROR.get() + " |  " + devi.getTranslation(command.getLanguage(), 462));
            return;
        }

        String query = Arrays.stream(command.getArgs()).skip(0).collect(Collectors.joining(" "));
        if (!command.getArgs()[0].startsWith("http")) query = "ytsearch:" + query;
        guildPlayer.loadSong(query, command, sender);

    }
}
