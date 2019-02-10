package net.devibot.provider.commands.dev;

import net.devibot.core.Core;
import net.devibot.core.entities.User;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;

public class UserDataCommand extends ICommand {

    private DiscordBot discordBot;

    public UserDataCommand(DiscordBot discordBot) {
        super("userdata");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.errorMessage().append("Please provider a user id").execute();
            return;
        }

        String id = command.getArgs()[0];
        User user = discordBot.getCacheManager().getUserCache().getUser(id);

        String message = Core.GSON_PRETTY.toJson(user);

        sender.message().append("UserData for id=").append(id).appendCodeBlock("Data:\n\n" + message, "json").execute();
    }
}
