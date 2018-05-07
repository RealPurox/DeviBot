package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class LeaveCommand implements Command {

    private Devi devi;
    public LeaveCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        System.out.println(devi.getMusicManager());
        System.out.println(devi.getMusicManager().getManager(event.getGuild()));
        System.out.println(devi.getMusicManager().getManager(event.getGuild()).getAudioPlayer());
        if (!devi.getMusicManager().getManager(event.getGuild()).getAudioPlayer().isPaused() && !devi.getMusicManager().isIdle(event.getGuild())){
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 112, "`" + prefix + "stop`"));
            return;
        }

        if (event.getGuild().getAudioManager().getConnectionStatus() != ConnectionStatus.CONNECTED) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 113));
            return;
        }

        event.getGuild().getAudioManager().closeAudioConnection();
        MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 114));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 143;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
