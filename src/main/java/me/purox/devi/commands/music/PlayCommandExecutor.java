package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayCommandExecutor implements CommandExecutor {

    private Devi devi;
    public PlayCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if(command.getEvent().getGuild().getAudioManager().getConnectionStatus() != ConnectionStatus.CONNECTED) {
            sender.reply(devi.getTranslation(command.getLanguage(), 116, "`" + command.getPrefix() + "join`"));
            return;
        }

        if (args.length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "play <link or yt search>`"));
            return;
        }

        String input = Arrays.stream(args).skip(0).collect(Collectors.joining(" "));

        if(!args[0].startsWith("https://") && !args[0].startsWith("http://")) {
            input = "ytsearch:" + input;
        }

        devi.getMusicManager().loadTrack(command.getEvent(), input, command.getEvent().getGuild().getSelfMember().getVoiceState().getChannel());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 115;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("addqueue");
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
