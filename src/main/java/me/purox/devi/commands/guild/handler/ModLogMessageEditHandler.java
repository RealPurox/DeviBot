package me.purox.devi.commands.guild.handler;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class ModLogMessageEditHandler {

    private Devi devi;
    public ModLogMessageEditHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(Command command, CommandSender sender) {
        String builder = ":information_source: | " + devi.getTranslation(command.getLanguage(), 304) + " -> " + devi.getTranslation(command.getLanguage(), 339) + "\n\n" +
                "```python\n" +
                devi.getTranslation(command.getLanguage(), 305) + "\n\n" +
                " '1' => " + devi.getTranslation(command.getLanguage(), 269) + "\n" +
                " '2' => " + devi.getTranslation(command.getLanguage(), 270) + "\n" +
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
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 308));
                        return;
                    }

                    String input = response.getMessage().getContentRaw().split(" ")[0];

                    int entered;
                    try {
                        entered = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        entered = -1;
                    }

                    if (entered > 2 || entered < 1) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 265));
                        startWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_EDITED, entered == 1);
                    command.getDeviGuild().saveSettings();

                    if (entered == 1)
                        sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 340));
                    else sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 341));
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 309)) );
    }
}