package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.VoiceState;

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
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        if (args.length == 0) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 456));
            return;
        }

        VoiceChannel deviChannel = command.getEvent().getGuild().getSelfMember().getVoiceState().getChannel();
        GuildVoiceState userState = command.getEvent().getGuild().getMember(sender).getVoiceState();

        if (!userState.inVoiceChannel()) {
            sender.reply(DeviEmote.ERROR.get() + " | You're not in a voice channel!");
            return;
        }

        if (deviChannel != null && userState.getChannel().getIdLong() != deviChannel.getIdLong()) {
            sender.reply(DeviEmote.ERROR.get() + " | I'm in another voice channel already");
            return;
        }

        String query = Arrays.stream(args).skip(0).collect(Collectors.joining(" "));
        if (!args[0].startsWith("http")) query = "ytsearch:" + query;
        guildPlayer.loadSong(query, command, sender);
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MUSIC;
    }

}
