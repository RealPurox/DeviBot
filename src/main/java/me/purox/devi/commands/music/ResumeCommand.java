package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;

public class ResumeCommand implements Command {

    private Devi devi;
    public ResumeCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        if (!devi.getMusicManager().getPlayer(event.getGuild()).isPaused()) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 127));
            return;
        }

        devi.getMusicManager().getPlayer(event.getGuild()).setPaused(false);
        MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 126));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 122;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("stop");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
