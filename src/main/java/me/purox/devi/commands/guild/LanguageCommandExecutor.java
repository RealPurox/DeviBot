package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LanguageCommandExecutor implements CommandExecutor {

    private Devi devi;
    private HashMap<Integer, Language> languageMap;

    public LanguageCommandExecutor(Devi devi) {
        this.devi = devi;
        this.languageMap = new HashMap<>();

        int i = 1;
        for (Language language : Language.values()) {
            languageMap.put(i++, language);
        }
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        builder.append(":information_source: | ").append(devi.getTranslation(command.getLanguage(), 260)).append("\n\n");
        builder.append("```python\n");
        for(Integer i : languageMap.keySet()) {
            builder.append("'").append(i).append("' => ").append(languageMap.get(i).getName()).append("\n");
        }
        builder.append("```\n").append(devi.getTranslation(command.getLanguage(), 261, "`cancel`"));

        sender.reply(builder.toString());
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
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 255));
                        return;
                    }

                    String input = response.getMessage().getContentRaw().split(" ")[0];

                    int entered;
                    try {
                        entered = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        entered = -1;
                    }

                    if (entered < 1 || entered > languageMap.size()) {
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 265));
                        startWaiter(nextAttempt, command, sender);
                        return;
                    }

                    Language language = languageMap.get(entered);
                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.LANGUAGE, language.name());
                    command.getDeviGuild().saveSettings();
                    sender.reply(":ok_hand: | " + devi.getTranslation(language, 258, "`" + language.getName() + "`"));
                },
                15, TimeUnit.SECONDS, () -> sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 259)) );
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 253;
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
