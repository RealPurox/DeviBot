package net.devibot.provider.commands.dev;

import net.devibot.core.Core;
import net.devibot.core.entities.Ban;
import net.devibot.core.entities.User;
import net.devibot.core.utils.DiscordWebhook;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;

public class GlobalPardonCommand extends ICommand {

    private DiscordBot discordBot;

    public GlobalPardonCommand(DiscordBot discordBot) {
        super("globalpardon");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.errorMessage().append("Correct usage: `").append(command.getPrefix()).append("globalpardon <user id> [reason]`").execute();
            return;
        }

        try {
            Long.valueOf(command.getArgs()[0]);
        } catch (NumberFormatException e) {
            sender.errorMessage().append("`").append(command.getArgs()[0]).append("` doesn't seem to be an user id.").execute();
        }

        User user = discordBot.getCacheManager().getUserCache().getUser(command.getArgs()[0]);

        if (!user.getBan().isActive()) {
            sender.errorMessage().append("| User with id `").append(command.getArgs()[0]).append("` is currently not prohibited from using Devi.").execute();
            return;
        }

        Ban newBan = new Ban();
        user.setBan(newBan);
        discordBot.getMainframeManager().saveUser(user);
        sender.successMessage().append("User with id `").append(command.getArgs()[0]).append("`is no longer prohibited from using Devi.").execute();

        DiscordWebhook webhook = new DiscordWebhook(Core.CONFIG.getControlRoomWebhook());
        webhook.setContent(user.getName() +  "#" + user.getDiscriminator() + " `(" + user.getId() + ")` has been globally unbanned by " + sender.getName() + "#"  + sender.getDiscriminator());
        webhook.execute();
    }
}
