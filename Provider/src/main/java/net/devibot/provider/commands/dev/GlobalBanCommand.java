package net.devibot.provider.commands.dev;

import net.devibot.core.entities.Ban;
import net.devibot.core.entities.User;
import net.devibot.core.utils.DiscordWebhook;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GlobalBanCommand extends ICommand {

    private DiscordBot discordBot;

    public GlobalBanCommand(DiscordBot discordBot) {
        super("globalban");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.reply(Emote.ERROR + " | Correct usage: `" + command.getPrefix() + "globalban <user id> [reason]`");
            return;
        }

        try {
            Long.valueOf(command.getArgs()[0]);
        } catch (NumberFormatException e) {
            sender.reply(Emote.ERROR + " | `" + command.getArgs()[0] + "` doesn't seem to be an user id.");
        }

        User user = discordBot.getCacheManager().getUserCache().getUser(command.getArgs()[0]);

        if (user.getBan().isActive()) {
            sender.reply(Emote.ERROR + " | User with id `" + command.getArgs()[0] + "` is already prohibited from using devi.");
            return;
        }

        String reason = command.getArgs().length == 1 ? "Unknown Reason" : Arrays.stream(command.getArgs()).skip(1).collect(Collectors.joining(" "));

        Ban newBan = new Ban(sender.getId(), reason);
        user.setBan(newBan);
        discordBot.getMainframeManager().saveUser(user);
        sender.reply(Emote.SUCCESS + " | User with id `" + command.getArgs()[0] + "` is now prohibited from using Devi.");

        DiscordWebhook webhook = new DiscordWebhook(discordBot.getConfig().getControlRoomWebhook());
        webhook.setContent(user.getName() +  "#" + user.getDiscriminator() + " `(" + user.getId() + ")` has been globally banned by " + sender.getName() + "#"  + sender.getDiscriminator() + "\nReason: " + reason);
        webhook.execute();
    }
}
