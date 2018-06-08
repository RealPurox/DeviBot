package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrefixCommandExecutor implements CommandExecutor {

    private final Devi devi;
    public PrefixCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        MessageReceivedEvent event = command.getEvent();

        Message message = MessageUtils.sendMessageSync(event.getChannel(), ":information_source: | " + devi.getTranslation(command.getLanguage(), 249, "`cancel`"));
        if (message == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 217));
            return;
        }

        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 250));
                        return;
                    }

                    String prefix = response.getMessage().getContentRaw().split(" ")[0];
                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.PREFIX, prefix);
                    command.getDeviGuild().saveSettings();
                    sender.reply(":ok_hand: | " + devi.getTranslation(command.getLanguage(), 251, "`" + prefix + "`"));
                },
                15, TimeUnit.SECONDS, () -> sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 252)));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 248;
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
