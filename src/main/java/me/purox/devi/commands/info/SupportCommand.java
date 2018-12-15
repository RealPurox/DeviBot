package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.entities.supportchat.SupportChatCreator;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SupportCommand extends ICommand {

    private Devi devi;

    public SupportCommand(Devi devi) {
        super("support");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getChannel().getType() != ChannelType.PRIVATE) {
            sender.reply("**" + sender.getName() + "**, did you discover a bug, want to request a feature or are in need of assistance by one of your awesome staff members? Run this command in a private message with me or join our support server.\n\nhttps://discord.gg/6Ac4uqZ");
            return;
        }

        SupportChatCreator creator = new SupportChatCreator(sender.getId(), devi);
        creator.start(supportChat -> {
            Guild staffGuild = devi.getStaffGuild();

            supportChat.save(devi);

            TextChannel channel = staffGuild.getTextChannelById(supportChat.getChannel());
            User user = devi.getShardManager().getUserById(supportChat.getUser());
            User staff = devi.getShardManager().getUserById(supportChat.getStaff());

            MessageUtils.sendMessageAsync(channel, Emote.SUCCESS + " | Support chat is now set up. All messages sent by " + staff.getAsMention() + " will now be posted in " + user.getName() + "#" + user.getDiscriminator() + "'s DMs.", msg -> {
                msg.pin().queue(placeHolder -> {}, placeHolder -> {});
                MessageUtils.sendPrivateMessageAsync(user, Emote.INFO + " | We're done! You are now chatting with " + staff.getName() + "#" + staff.getDiscriminator());
            });
        });
    }
}
