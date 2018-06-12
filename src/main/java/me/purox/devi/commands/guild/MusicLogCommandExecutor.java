package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicLogCommandExecutor implements CommandExecutor {

    private Devi devi;

    public MusicLogCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        TextChannel textChannel = DiscordUtils.getTextChannel(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUSIC_LOG_CHANNEL), command.getEvent().getGuild());
        String builder = ":information_source: | " + devi.getTranslation(command.getLanguage(), 345) + "\n\n" +
                "```python\n" +
                devi.getTranslation(command.getLanguage(), 319) + "\n\n" +
                " '1' => " + devi.getTranslation(command.getLanguage(), 346) + "\n" +
                " '2' => " + devi.getTranslation(command.getLanguage(), 347) + " (" + devi.getTranslation(command.getLanguage(), textChannel == null ? 348 : 349, "#" + (textChannel != null ? textChannel.getName() : "??")) + ")\n" +
                "```\n" + devi.getTranslation(command.getLanguage(), 320, "`cancel`");

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
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 321));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 322));
                        return;
                    }

                    String input = response.getMessage().getContentRaw().split(" ")[0];

                    int entered;
                    try {
                        entered = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        entered = -1;
                    }

                    switch (entered) {
                        //enable / disable
                        case 1:
                            String enabledResponse = ":information_source: | " + devi.getTranslation(command.getLanguage(), 345) + " -> " + devi.getTranslation(command.getLanguage(), 346) + "\n\n" +
                                    "```python\n" +
                                    " '1' => " + devi.getTranslation(command.getLanguage(), 269) + "\n" +
                                    " '2' => " + devi.getTranslation(command.getLanguage(), 270) + "\n" +
                                    "```\n" + devi.getTranslation(command.getLanguage(), 320, "`cancel`");
                            sender.reply(enabledResponse);
                            startEnableWaiter(1, command, sender);
                            break;
                        //channel
                        case 2:
                            String removeResponse = ":information_source: | "  + devi.getTranslation(command.getLanguage(), 345) +" -> " + devi.getTranslation(command.getLanguage(), 347) + "\n\n" +
                                    "```python\n" +
                                    devi.getTranslation(command.getLanguage(), 350) +
                                    "```\n" + devi.getTranslation(command.getLanguage(), 320, "`cancel`");
                            sender.reply(removeResponse);
                            startChannelWaiter(1, command, sender);
                            break;
                        default:
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 265));
                            startWaiter(nextAttempt, command, sender);
                            break;
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 323)));
    }

    private void startEnableWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 321));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 324));
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
                        startEnableWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED, entered == 1);
                    command.getDeviGuild().saveSettings();

                    if (entered == 1)
                        sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 351));
                    else sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 352));
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 323)));
    }

    private void startChannelWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 321));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 353));
                        return;
                    }

                    String input = response.getMessage().getContentRaw();
                    TextChannel textChannel = DiscordUtils.getTextChannel(input, command.getEvent().getGuild());

                    if (textChannel == null) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 331));
                        startChannelWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.MUSIC_LOG_CHANNEL, textChannel.getId());
                    command.getDeviGuild().saveSettings();
                    sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 354));
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 323)));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 318;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
