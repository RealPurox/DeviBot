package me.purox.devi.listener;

import me.purox.devi.commands.handler.CommandHandler;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    private Devi devi;

    public CommandListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String prefix = devi.getSettings().getDefaultPrefix();

        //custom prefix
        if(!devi.getSettings().isDevBot() && event.getChannelType() == ChannelType.TEXT && event.getGuild() != null) {
            DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
            prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);
        }

        if (!event.getAuthor().isBot() && message.startsWith(prefix)) {
            CommandHandler commandHandler = devi.getCommandHandler();
            String invoke = message.split(" ")[0].toLowerCase().substring(prefix.length());
            if (commandHandler.getCommands().containsKey(invoke)) {
                commandHandler.handleCommand(prefix, message, event, new CommandSender(event.getAuthor(), event));
            }
        }
    }
}
