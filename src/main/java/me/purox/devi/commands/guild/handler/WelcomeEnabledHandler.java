package me.purox.devi.commands.guild.handler;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class WelcomeEnabledHandler {

    private Devi devi;

    public WelcomeEnabledHandler(Devi devi) {
        this.devi = devi;
    }

    public void startWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(Emote.ERROR.get() + " | Welcome module settings selection has been cancelled");
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(Emote.ERROR.get() + " | You've failed to enter a valid number 3 times in a row. Welcome module settings selection has been cancelled.");
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
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 265));
                        startWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS, entered == 1);
                    command.getDeviGuild().saveSettings();

                    if (entered == 1)
                        sender.reply(Emote.SUCCESS.get() + " | Join and leave messages have been enabled");
                    else sender.reply(Emote.SUCCESS.get() + " | Join and leave messages have been disabled");
                },
                15, TimeUnit.SECONDS, () -> sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 263)) );
    }}
