package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.util.Collections;
import java.util.List;

public class PurgeCommand implements Command {

    private Devi devi;
    public PurgeCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length < 1) {
          MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "purge <messages>`"));
           return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            amount = -1;
        }

        int finalAmount = amount;
        List<Message> messages;
        try {
            messages = event.getChannel().getHistoryBefore(event.getMessageId(), amount).complete().getRetrievedHistory();

            event.getTextChannel().deleteMessages(messages).queue(
                    success -> MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 153, finalAmount))
            );
        } catch (InsufficientPermissionException e) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 155));
        } catch (IllegalArgumentException e) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 152));
        }


    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 151;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("prune");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
