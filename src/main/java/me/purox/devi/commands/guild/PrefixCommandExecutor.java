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
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.Arrays;
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

        Message message = MessageUtils.sendMessageSync(event.getChannel(), ":information_source: | Please select a new prefix by replying to this message or type `cancel` to cancel the prefix selection");
        if (message == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 217));
            return;
        }

        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> checkUser(evt, event.getMessageId(), event.getAuthor().getId()),
                response -> {
                    if (PermissionUtil.checkPermission(event.getTextChannel(), event.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
                        devi.getPrunedMessages().put(message.getId(), "", ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES);
                        devi.getPrunedMessages().put(response.getMessage().getId(), "", ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES);
                        event.getTextChannel().deleteMessages(Arrays.asList(message, response.getMessage())).queue();
                    }

                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(":no_entry: | The prefix selection was cancelled");
                        return;
                    }

                    String prefix = response.getMessage().getContentRaw().split(" ")[0];
                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.PREFIX, prefix);
                    command.getDeviGuild().saveSettings();
                    sender.reply(":ok_hand: | The prefix has been changed to `" + prefix + "`");
                },
                15, TimeUnit.SECONDS, () -> {
                    if (PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
                        devi.getPrunedMessages().put(message.getId(), "", ExpirationPolicy.CREATED, 30, TimeUnit.SECONDS);
                        message.delete().queue();
                    }
                    sender.reply("took to long to respond");
                });
    }

    private boolean checkUser(MessageReceivedEvent event, String messageID, String authorID) {
        return event.getAuthor().getId().equals(authorID) &&
                !event.getMessageId().equals(messageID) &&
                !event.getAuthor().isBot();
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
        return Arrays.asList("changeprefix", "editprefix");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
