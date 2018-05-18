package me.purox.devi.commands.guild.custom;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class AddCommand implements Command {

    private Devi devi;
    public AddCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 177;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("addcmd", "createcmd", "createcommand");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
