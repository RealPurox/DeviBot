package net.devibot.provider.commands.dev;

import net.devibot.core.Core;
import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;

public class GuildDataCommand extends ICommand {

    private DiscordBot discordBot;

    public GuildDataCommand(DiscordBot discordBot) {
        super("guilddata");
        this.discordBot = discordBot;
    }


    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getGuild() == null && command.getArgs().length == 0) {
            sender.errorMessage().append("Please provider a guild id or execute this command in a guild").execute();
            return;
        }

        String id = command.getArgs().length > 0 ? command.getArgs()[0] : command.getGuild().getId();
        DeviGuild deviGuild = discordBot.getCacheManager().getDeviGuildCache().getDeviGuild(id);

        String message = Core.GSON_PRETTY.toJson(deviGuild);

        sender.message().append("GuildData for id=").append(id).appendCodeBlock("Settings:\n\n" + message, "json").execute();
    }

}
