package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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

        //limit to 500
        if (amount > 500 || amount < 1) {
            sender.reply("//TODO EDIT LATER => Limit extended");
            return;
        }

        retrieveHistory(command.getEvent().getTextChannel(), command.getEvent().getMessageId(), amount, messages -> {
                sender.reply("SUCCESS");
        }, failure -> sender.reply("FAILURE: " + failure.getMessage()));
    }

    //TODO CLEAN THIS FUCKING MESS
    private void retrieveHistory(TextChannel channel, String firstId, int amount, Consumer<? super Void> completed, Consumer<? super Throwable> failure) {
        List<Message> retrieved = new ArrayList<>();
        //100
        channel.getHistoryBefore(firstId, 100)
                .queue(messageHistory1 -> {
                    retrieved.addAll(messageHistory1.getRetrievedHistory());

                    if (amount <= 100) {
                        channel.deleteMessages(retrieved).queue(completed, failure);
                    }
                    //200
                    else
                        channel.getHistoryBefore(retrieved.get(retrieved.size() - 1).getId(), 100)
                                .queue(messageHistory2 -> {
                                    retrieved.addAll(messageHistory2.getRetrievedHistory());

                                    if (amount <= 200) {
                                        channel.deleteMessages(retrieved).queue(completed, failure);
                                    }
                                    //300
                                    else
                                        channel.getHistoryBefore(retrieved.get(retrieved.size() - 1), 100)
                                                .queue(messageHistory3 -> {
                                                    retrieved.addAll(messageHistory3.getRetrievedHistory());

                                                    if (amount <= 300) {
                                                        channel.deleteMessages(retrieved).queue(completed, failure);
                                                    }
                                                    //400
                                                    else
                                                        channel.getHistoryBefore(retrieved.get(retrieved.size() - 1), 100)
                                                            .queue(messageHistory4 -> {
                                                                retrieved.addAll(messageHistory4.getRetrievedHistory());

                                                                if (amount <= 400) {
                                                                    channel.deleteMessages(retrieved).queue(completed, failure);
                                                                }
                                                                //500
                                                                else
                                                                    channel.getHistoryBefore(retrieved.get(retrieved.size() -1 ), 100)
                                                                        .queue(messageHistory5 -> {
                                                                            retrieved.addAll(messageHistory5.getRetrievedHistory());
                                                                            channel.deleteMessages(retrieved).queue(completed, failure);
                                                                        });
                                                            });
                                                });
                                });
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
