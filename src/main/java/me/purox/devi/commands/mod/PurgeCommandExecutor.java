package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.jodah.expiringmap.ExpirationPolicy;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PurgeCommandExecutor implements CommandExecutor {

    private Devi devi;
    public PurgeCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
           sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix()+ "purge <messages>`"));
           return;
        }

        if (!command.getEvent().getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 614));
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            amount = -1;
        }

        //limit to 500
        if (amount > 500 || amount < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 616));
            return;
        }

        int finalAmount = amount;
        deleteMessages(command.getEvent().getTextChannel(), command.getEvent().getMessageId(), amount, new DeleteResult(), result ->
                sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 153, result.getAmount())
                        + (result.isAllDeletedSuccessfully() ? "" : "\n\n" + devi.getTranslation(command.getLanguage(), 615))),
                failure -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 154, finalAmount)));
    }

    private class DeleteResult {

        private int amount;
        private boolean allDeletedSuccessfully;

        DeleteResult() {
            this.amount = 0;
            this.allDeletedSuccessfully = false;
        }

        int getAmount() {
            return amount;
        }

        boolean isAllDeletedSuccessfully() {
            return allDeletedSuccessfully;
        }

        void setAllDeletedSuccessfully() {
            this.allDeletedSuccessfully = true;
        }

        void increaseAmount(int increment) {
            this.amount += increment;
        }
    }

    private void deleteMessages(TextChannel channel, String firstId, int amount, DeleteResult deleteResult, Consumer<? super DeleteResult> completed, Consumer<? super Throwable> failure) {
        if (amount == 1) {
            channel.getHistoryBefore(firstId, 1).queue(messageHistory -> {
                if (messageHistory.getRetrievedHistory().isEmpty()) {
                    completed.accept(deleteResult);
                    return;
                }
                messageHistory.getRetrievedHistory().get(0).delete().queue((o) -> {
                    deleteResult.setAllDeletedSuccessfully();
                    deleteResult.increaseAmount(amount);
                    completed.accept(deleteResult);
                }, failure);
            }, failure);
            return;
        }

        AtomicInteger atomicAmount = new AtomicInteger(amount);
        channel.getHistoryBefore(firstId, amount > 100 ? 100 : amount)
                .queue(messageHistory -> {
                    List<Message> retrieved = messageHistory.getRetrievedHistory().stream()
                            .filter(message -> OffsetDateTime.now().minusWeeks(2).isBefore(message.getCreationTime()))
                            .collect(Collectors.toList());
                    retrieved.forEach(message -> devi.getPrunedMessages().put(message.getId(), "", ExpirationPolicy.CREATED, 5, TimeUnit.MINUTES));
                    atomicAmount.set(atomicAmount.get() - 100);

                    deleteResult.increaseAmount(retrieved.size());

                    if (retrieved.isEmpty()) {
                        completed.accept(deleteResult);
                        return;
                    }

                    //we have 100 or less messages to delete so we can use our beautiful callbacks
                    if (atomicAmount.get() <= 0) {
                        deleteResult.setAllDeletedSuccessfully();
                        channel.deleteMessages(retrieved).queue((o) -> completed.accept(deleteResult), failure);
                        return;
                    }

                    //more messages = more work :rolling_eyes:
                    //not really but yeah
                    channel.deleteMessages(retrieved).queue((o) -> deleteMessages(channel, retrieved.get(retrieved.size() - 1).getId(),
                            atomicAmount.get(), deleteResult, completed, failure), failure);
                }, failure);
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}
