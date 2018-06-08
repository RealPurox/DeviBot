package me.purox.devi.listener;

import me.purox.devi.commands.handler.CommandHandler;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.waiter.ResponseWaiter;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener extends ListenerAdapter {

    private Devi devi;
    public CommandListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String prefix = devi.getSettings().getDefaultPrefix();

        //I'm adding this to the command listener so things don't get fucked up
        //I might want to clean this one day
        ResponseWaiter waiter = devi.getResponseWaiter();
        if (event.getGuild() != null && waiter.getWaitingResponses().containsKey(event.getGuild().getId())) {
            Set<ResponseWaiter.WaitingResponse> waitingResponses = waiter.getWaitingResponses().get(event.getGuild().getId());
            ResponseWaiter.WaitingResponse[] toRemove = waitingResponses.toArray(new ResponseWaiter.WaitingResponse[0]);
            waitingResponses.removeAll(Stream.of(toRemove).filter(i -> i.attempt(event, new ResponseWaiter.Response(event.getAuthor(), event.getMessage()))).collect(Collectors.toSet()));
            if (toRemove.length > 0) return;
        }

        if (event.getChannelType() == ChannelType.TEXT && event.getGuild() != null) {
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

        if (event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention() + " ")) {
            prefix = event.getJDA().getSelfUser().getAsMention() + " ";
        } else if (event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention())) {
            prefix = event.getJDA().getSelfUser().getAsMention();
        }

        if (!event.getAuthor().isBot() && message.startsWith(prefix)) {
            String invoke = message.substring(prefix.length()).split(" ")[0].toLowerCase();
            CommandHandler commandHandler = devi.getCommandHandler();

            if (commandHandler.getCommands().containsKey(invoke)) {
                commandHandler.handleCommand(prefix, message, event, new CommandSender(event.getAuthor(), event));
            }
        }
    }
}
