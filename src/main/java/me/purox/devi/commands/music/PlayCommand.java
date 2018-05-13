package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PlayCommand implements Command {

    private Devi devi;
    public PlayCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if(event.getGuild().getAudioManager().getConnectionStatus() != ConnectionStatus.CONNECTED) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 116, "`" + prefix  + "join`"));
            return;
        }

        if (args.length < 1) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "play <link or yt search>`"));
        }

        String input = Arrays.stream(args).skip(0).collect(Collectors.joining(" "));

        if(!args[0].startsWith("https://") && !args[0].startsWith("http://")) {
            input = "ytsearch:" + input;
        }

        devi.getMusicManager().loadTrack(event, input, event.getGuild().getSelfMember().getVoiceState().getChannel());
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
