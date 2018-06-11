package me.purox.devi.listener;

import me.purox.devi.commands.handler.CommandHandler;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;

import java.util.List;

public class CommandListener extends ListenerAdapter {

    private Devi devi;
    public CommandListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String prefix = devi.getSettings().getDefaultPrefix();


        if (devi.hasDatabaseConnection() && event.getChannelType() == ChannelType.TEXT && event.getGuild() != null) {
            DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());

            //custom commands
            List<Document> commands = deviGuild.getCommands();
            String invokeWithoutPrefix = message.split(" ")[0];

            for (Document command : commands) {
                if (command.getString("invoke").equals(invokeWithoutPrefix)) {
                    MessageUtils.sendMessageAsync(event.getChannel(), command.getString("response"));
                    return;
                }
            }

            //custom prefix
            if (!devi.getSettings().isDevBot())
                prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);
        }

        if (!event.getAuthor().isBot() && message.startsWith(prefix)) {
            String invoke = message.split(" ")[0].toLowerCase().substring(prefix.length());
            CommandHandler commandHandler = devi.getCommandHandler();

            if (commandHandler.getCommands().containsKey(invoke)) {
                if (!devi.hasDatabaseConnection()) {
                    MessageUtils.sendMessageAsync(event.getChannel(), DeviEmote.ERROR.get() + " | Our services are currently down for an issue with our database server provider. Thanks for your patience.");
                    //return;
                }

                commandHandler.handleCommand(prefix, message, event, new CommandSender(event.getAuthor(), event));
            }
        }
    }
}
