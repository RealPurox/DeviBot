package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PurgeCommandExecutor implements CommandExecutor {

    private Devi devi;
    public PurgeCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
          sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix()+ "purge <messages>`"));
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
            messages = command.getEvent().getChannel().getHistoryBefore(command.getEvent().getMessageId(), amount).complete().getRetrievedHistory();
            messages.forEach(message -> devi.getPrunedMessages().put(message.getId(), "", ExpirationPolicy.CREATED, 5, TimeUnit.MINUTES));

            command.getEvent().getTextChannel().deleteMessages(messages).queue(
                    success -> sender.reply(devi.getTranslation(command.getLanguage(), 153, finalAmount))
            );
        } catch (InsufficientPermissionException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 155));
        } catch (IllegalArgumentException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 152));
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
