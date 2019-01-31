package net.devibot.provider.commands.dev;

import net.devibot.core.Core;
import net.devibot.core.entities.DeviGuild;
import net.devibot.core.entities.User;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Emote;

public class UserDataCommand extends ICommand {

    private DiscordBot discordBot;

    public UserDataCommand(DiscordBot discordBot) {
        super("userdata");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.reply(Emote.ERROR + " | Please provider a user id");
            return;
        }

        String id = command.getArgs()[0];
        User user = discordBot.getCacheManager().getUserCache().getUser(id);

        String message = Core.GSON_PRETTY.toJson(user);

        sender.reply("UserData for id=" + id + "```json\nData:\n\n" + message + "```");
    }
}
