package net.devibot.provider.commands.dev;

import net.devibot.core.Core;
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
            sender.errorMessage().append("Correct usage: `").append(command.getPrefix()).append("globalban <user id> [reason]`").execute();
            return;
        }

        try {
            Long.valueOf(command.getArgs()[0]);
        } catch (NumberFormatException e) {
            sender.errorMessage().append("`").append(command.getArgs()[0]).append("` doesn't seem to be an user id.").execute();
        }

        User user = discordBot.getCacheManager().getUserCache().getUser(command.getArgs()[0]);

        if (user.getBan().isActive()) {
            sender.errorMessage().append("User with id `").append(command.getArgs()[0]).append("` is already prohibited from using devi.").execute();
            return;
        }

        String reason = command.getArgs().length == 1 ? "Unknown Reason" : Arrays.stream(command.getArgs()).skip(1).collect(Collectors.joining(" "));

        Ban newBan = new Ban(sender.getId(), reason);
        user.setBan(newBan);
        discordBot.getMainframeManager().saveUser(user);
        sender.successMessage().append("User with id `").append(command.getArgs()[0]).append("` is now prohibited from using Devi.").execute();

        DiscordWebhook webhook = new DiscordWebhook(Core.CONFIG.getControlRoomWebhook());
        webhook.setContent(user.getName() +  "#" + user.getDiscriminator() + " `(" + user.getId() + ")` has been globally banned by " + sender.getName() + "#"  + sender.getDiscriminator() + "\nReason: " + reason);
        webhook.execute();
    }
}
