package net.devibot.provider.commands.dev;

import net.devibot.core.entities.Ban;
import net.devibot.core.entities.User;
import net.devibot.core.utils.DiscordWebhook;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Emote;

public class GlobalPardonCommand extends ICommand {

    private DiscordBot discordBot;

    public GlobalPardonCommand(DiscordBot discordBot) {
        super("globalpardon");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.reply(Emote.ERROR + " | Correct usage: `" + command.getPrefix() + "globalpardon <user id> [reason]`");
            return;
        }

        try {
            Long.valueOf(command.getArgs()[0]);
        } catch (NumberFormatException e) {
            sender.reply(Emote.ERROR + " | `" + command.getArgs()[0] + "` doesn't seem to be an user id.");
        }

        User user = discordBot.getCacheManager().getUserCache().getUser(command.getArgs()[0]);

        if (!user.getBan().isActive()) {
            sender.reply(Emote.ERROR + " | User with id `" + command.getArgs()[0] + "` is currently not prohibited from using Devi.");
            return;
        }

        Ban newBan = new Ban();
        user.setBan(newBan);
        discordBot.getMainframeManager().saveUser(user);
        sender.reply(Emote.SUCCESS + " | User with id `" + command.getArgs()[0] + "`is no longer prohibited from using Devi.");

        DiscordWebhook webhook = new DiscordWebhook(discordBot.getConfig().getControlRoomWebhook());
        webhook.setContent(user.getName() +  "#" + user.getDiscriminator() + " `(" + user.getId() + ")` has been globally unbanned by " + sender.getName() + "#"  + sender.getDiscriminator());
        webhook.execute();
    }
}
