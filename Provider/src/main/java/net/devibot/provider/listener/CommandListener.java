package net.devibot.provider.listener;

import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    private DiscordBot discordBot;

    public CommandListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //block bots
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();
        String prefix = discordBot.getConfig().getDefaultPrefix();

        //we're in a text channel and it's in a guild
        if (event.getChannelType() == ChannelType.TEXT && event.getGuild() != null) {
            DeviGuild deviGuild = discordBot.getCacheManager().getDeviGuildCache().getDeviGuild(event.getGuild().getId());
            prefix = deviGuild.getPrefix();
        }

        //was the bot mention used as the prefix?
        if (event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention() + " ")) {
            prefix = event.getJDA().getSelfUser().getAsMention() + " ";
        } else if (event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention())) {
            prefix = event.getJDA().getSelfUser().getAsMention();
        }

        //ok .. message starts with the prefix
        if (message.startsWith(prefix)) {
            String invoke = message.substring(prefix.length()).split(" ")[0].toLowerCase();
            //it's a command!
            if (discordBot.getCommandHandler().getCommands().containsKey(invoke)) {
                discordBot.getCommandHandler().handleCommand(new ICommand.Command(event, prefix, discordBot));
            }
        }
    }
}
