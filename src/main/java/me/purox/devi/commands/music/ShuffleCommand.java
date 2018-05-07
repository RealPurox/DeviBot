package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class ShuffleCommand implements Command {

    private Devi devi;
    public ShuffleCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        if (devi.getMusicManager().getManager(event.getGuild()).getQueue().isEmpty()) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 139));
            return;
        }

        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 100));
            return;
        }

        devi.getMusicManager().getManager(event.getGuild()).shuffleQueue();
        MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 140));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 138;
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
