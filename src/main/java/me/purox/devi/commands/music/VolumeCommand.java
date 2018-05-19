package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class VolumeCommand implements Command {

    private Devi devi;
    public VolumeCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length < 1) {
            sender.reply(devi.getTranslation(language, 12, "`" + prefix + "volume <1 - 100>`"));
            return;
        }

        int volume;
        try {
            volume = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            volume = -1;
        }

        if (volume < 1 || volume > 100) {
            sender.reply(devi.getTranslation(language, 191));
            return;
        }

        devi.getMusicManager().getPlayer(event.getGuild()).setVolume(volume);
        MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 190, volume));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 189;
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
