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
        String builder = ":information_source: | " + devi.getTranslation(command.getLanguage(), 304) + " -> " + devi.getTranslation(command.getLanguage(), 330) + "\n\n" +
                "```python\n" +
                devi.getTranslation(command.getLanguage(), 227) + "\n\n" +
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
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 328));
                        return;
                    }

                    String input = response.getMessage().getContentRaw();
                    TextChannel textChannel = DiscordUtils.getTextChannel(input, command.getEvent().getGuild());

                    if (textChannel == null) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 331));
                        startWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL, textChannel.getId());
                    command.getDeviGuild().saveSettings();
                    sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 332, textChannel.getAsMention()));
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 309)) );
    }
}
