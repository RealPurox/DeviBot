package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.utils.MessageUtils;
import me.purox.devi.utils.Reactions;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SupportCommand extends ICommand {

    private Devi devi;

    public SupportCommand(Devi devi) {
        super("support");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getChannel().getType() != ChannelType.PRIVATE) {
            sender.reply("**" + sender.getName() + "**, if you are in need of help from one of our staff members, want to request a features or if you have found a bug you want to report, join our support server or use this command in a private message with Devi for additional support.\n\nhttps://discord.gg/6Ac4uqZ");
            return;
        }

        String userMessage = "Thanks a lot for using our support chat feature. Your private channel with me (this channel) will temporarily be transformed into a support chat and you will no longer be able to use commands in here. " +
                "Our staff members will be able to see every message you send in this channel and so will you be able to see all messages they send in the support chat.\n\n" +
                "Please do not provide any sensitive information like emails, passwords, etc.\n\n";

        String staffMessage = "Hello <@&422327399401652224>, {user} is in need of assistance. Please claim the support chat by reacting with \uD83C\uDD97.";

        sender.reply(userMessage + "If you wish to continue please react with \uD83C\uDD97.", (msg) -> {
            AtomicBoolean reacted = new AtomicBoolean(false);

            Reactions.reactionGUI(command.getJDA(), sender.getId(), true, msg, (callback) -> {
                        reacted.set(true);
                        Guild guild = devi.getStaffGuild().get();

                        guild.getCategoryById(520323760788865025L).createTextChannel(sender.getName() + "_" + sender.getDiscriminator()).queue(channel -> {
                            msg.editMessage(userMessage + Emote.SUCCESS + " | Awesome! All staff members have been alerted and one of them will get back to you as soon as possible. If you do not get a response withing the next 15 minutes please try again at a later point of time as there might not be a staff member available at the moment").queue(placeHolder -> {}, placeHolder -> {});

                            MessageUtils.sendMessageAsync((MessageChannel) channel, staffMessage.replace("{user}", sender.getName() + "#" + sender.getDiscriminator()), sent -> {
                                Reactions.reactionGUI(command.getJDA(), "422327399401652224", false, sent, staffId -> {
                                    ((MessageChannel) channel).sendMessage(staffId).queue();
                                }, timeOut -> {
                                    //todo
                                }, Collections.singletonList("\uD83C\uDD97"));
                            });
                            }, failure -> {
                            msg.editMessage(userMessage + Emote.ERROR + " | Something went wrong while attempting to setup your support chat. Our developers have been alerted and will work on it as soon as they see it.").queue(placeHolder -> {}, placeHolder -> {});
                            devi.sendMessageToDevelopers("Failed to create support channel for user " + sender + "\n\nThrowable:" + failure);
                        });
                    }, (timeOut) -> {
                        if (!reacted.get())
                            msg.editMessage(userMessage + Emote.ERROR + " | Sorry, you took to long to react to my message. If you are still in need of support, run the support command again.").queue(placeHolder -> {}, placeHolder -> {});
                    }, Collections.singletonList("\uD83C\uDD97"), 3, TimeUnit.MINUTES);
        });
    }
}
