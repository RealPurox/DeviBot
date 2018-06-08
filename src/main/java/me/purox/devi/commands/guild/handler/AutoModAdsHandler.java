package me.purox.devi.commands.guild.handler;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class AutoModAdsHandler {

    private Devi devi;
    public AutoModAdsHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(Command command, CommandSender sender) {

        String builder = ":information_source: | Edit Auto-Mod settings -> enable or disable filtering discord invite links\n\n" +
                "```python\n" +
                "Reply to this message with one of the options listed below to edit your Auto-Mod settings\n\n" +
                " '1' => Enabled\n" +
                " '2' => Disabled\n" +
                "```\nYou can cancel editing your Auto-Mod settings by typing `cancel`";

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
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 254));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(":no_entry: | You've failed to enter a valid number 3 times in a row. Auto-Mod settings selection has been cancelled");
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
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 265));
                        startWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS, entered == 1);
                    command.getDeviGuild().saveSettings();

                    if (entered == 1)
                        sender.reply(":ok_hand: | Auto-Mod will now filter discord invite links");
                    else sender.reply(":ok_hand: | Auto-Mod will no longer filter discord invite links");
                },
                15, TimeUnit.SECONDS, () -> sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 263)) );
    }
}


