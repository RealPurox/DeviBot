package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestCommandExecutor implements CommandExecutor {

    private Devi devi;

    public TestCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId()) && !sender.isConsoleCommandSender()) return;
        sender.reply("waiting for response ...");
        devi.getResponseWaiter().waitForResponse(command.getEvent().getGuild(),
                event -> checkUser(event, command.getEvent().getMessageId(), command.getEvent().getAuthor().getId(), command.getEvent().getMessageId()),
                response -> sender.reply("Got response: " + response.getMessage().getContentDisplay()),
                10, TimeUnit.SECONDS, () -> sender.reply("Timeout!"));
    }

    private boolean checkUser(MessageReceivedEvent event, String messageID, String authorID, String commandMessageID) {
        return event.getAuthor().getId().equals(authorID) &&
                !event.getMessageId().equals(messageID) &&
                !commandMessageID.equals(event.getMessageId()) &&
                !event.getAuthor().isBot();
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
