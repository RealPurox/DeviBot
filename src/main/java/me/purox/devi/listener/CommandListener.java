package me.purox.devi.listener;

import me.purox.devi.commands.CommandHandler;
import me.purox.devi.commands.ICommand;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.entities.Command;
import me.purox.devi.core.waiter.ResponseWaiter;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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

        //RESPONSE WAITER START
        //This should be executed before handling commands
        ResponseWaiter waiter = devi.getResponseWaiter();
        if (event.getGuild() != null && waiter.getWaitingResponses().containsKey(event.getGuild().getId())) {
            Set<ResponseWaiter.Waiter> waitingResponses = waiter.getWaitingResponses().get(event.getGuild().getId());
            ResponseWaiter.Waiter[] toRemove = waitingResponses.toArray(new ResponseWaiter.Waiter[0]);
            //noinspection unchecked
            Set<ResponseWaiter.Waiter> filteredToRemove = Stream.of(toRemove).filter(i -> i.attempt(event, new ResponseWaiter.Response(event.getAuthor(), event.getMessage()))).collect(Collectors.toSet());
            waitingResponses.removeAll(filteredToRemove);
            if (filteredToRemove.size() != 0) return;
        }
        //RESPONE WAITER END

        //it's a text channel and we're in a guild
        if (event.getChannelType() == ChannelType.TEXT && event.getGuild() != null) {
            DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());

            //check if it was a custom command first
            List<Command> commandEntities = deviGuild.getCommandEntities();
            String invokeWithoutPrefix = message.split(" ")[0];

            for (Command command : commandEntities) {
                if (command.getInvoke().equals(invokeWithoutPrefix)) {
                    MessageUtils.sendMessageAsync(event.getChannel(), command.getResponse());
                    return;
                }
            }

            //if it wasn't a custom command, update the prefix in case they changed it.
            //don't change for dev bot
            if (!devi.getSettings().isDevBot())
                prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);
        }

        //check if they used the bot mention as the prefix OR the bot mention with a space afterwards
        if (event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention() + " ")) {
            prefix = event.getJDA().getSelfUser().getAsMention() + " ";
        } else if (event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention())) {
            prefix = event.getJDA().getSelfUser().getAsMention();
        }

        //a bot didn't try to execute a command and the message does start with the prefix ..
        if (!event.getAuthor().isBot() && message.startsWith(prefix)) {
            String invoke = message.substring(prefix.length()).split(" ")[0].toLowerCase();
            CommandHandler commandHandler = devi.getCommandHandler();

            //.. let's check if we know that command
            if (commandHandler.getCommands().containsKey(invoke)) {
                //yes we do so fire that baby
                commandHandler.handleCommand(new ICommand.Command(event, prefix, devi));
            }
            // else => apparently we don't so don't do anything ¯\_(ツ)_/¯
        }

    }
}
