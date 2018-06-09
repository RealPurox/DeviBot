package me.purox.devi.commands.guild.handler;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class ModLogChannelHandler {

    private Devi devi;
    public ModLogChannelHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(Command command, CommandSender sender) {
        String builder = ":information_source: | " + devi.getTranslation(command.getLanguage(), 304) + " -> Change Mod-Log channel\t\n\n" +
                "```python\n" +
                "Please reply to this message with the channel mention, channel name or channel ID to change the Mod-Log channel.\n\n" +
                "```\n" + devi.getTranslation(command.getLanguage(), 306, "`cancel`");
        sender.reply(builder);
        startWaiter(1, command, sender);
    }

    private void startWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 307));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | You've failed to enter a valid channel 3 times in a row. Mod-Log settings selection has been cancelled\t");
                        return;
                    }

                    String input = response.getMessage().getContentRaw();
                    TextChannel textChannel = DiscordUtils.getTextChannel(input, command.getEvent().getGuild());

                    if (textChannel == null) {
                        sender.reply(DeviEmote.ERROR.get() + " | Provided channel was not found. Please try again.");
                        startWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL, textChannel.getId());
                    command.getDeviGuild().saveSettings();
                    sender.reply(DeviEmote.SUCCESS.get() + " | The Mod-Log channel has been changed to " + textChannel.getAsMention());
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 309)) );
    }
}
