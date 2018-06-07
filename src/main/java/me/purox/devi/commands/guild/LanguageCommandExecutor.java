package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.Arrays;
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
        MessageReceivedEvent event = command.getEvent();

        StringBuilder builder = new StringBuilder();
        builder.append(":information_source: | Change your guild language by replying to this message with one of the languages listed below\n\n");
        builder.append("```python\n");
        for(Integer i : languageMap.keySet()) {
            builder.append("'").append(i).append("' => ").append(languageMap.get(i).getName()).append("\n");
        }
        builder.append("```\nYou can cancel the language selection by typing `cancel`");

        Message message = MessageUtils.sendMessageSync(event.getChannel(), builder.toString());
        if (message == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 217));
            return;
        }

        startWaiter(1, message, command, sender);
    }

    private void startWaiter(int attempt, Message message, Command command, CommandSender sender) {
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId()),
                response -> {
                    if (PermissionUtil.checkPermission(event.getTextChannel(), event.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
                        devi.getPrunedMessages().put(message.getId(), "", ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES);
                        devi.getPrunedMessages().put(response.getMessage().getId(), "", ExpirationPolicy.CREATED, 1, TimeUnit.MINUTES);
                        event.getTextChannel().deleteMessages(Arrays.asList(message, response.getMessage())).queue();
                    }

                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(":no_entry: | The language selection was cancelled");
                        return;
                    }

                    String input = command.getEvent().getMessage().getContentRaw().split(" ")[0];

                    int entered;
                    try {
                        entered = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        entered = -1;
                    }

                    if (entered < 1 || entered > languageMap.size()) {
                        Message updatedMessage = MessageUtils.sendMessageSync(event.getChannel(), ":no_entry: | Please provide a valid number." + );

                        return;
                    }


                    Language language = languageMap.get(entered);
                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.LANGUAGE, language.name());
                    command.getDeviGuild().saveSettings();
                    sender.reply(":ok_hand: | The language has been changed to " + language.getName());
                },
                15, TimeUnit.SECONDS, () -> {
                    if (PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
                        devi.getPrunedMessages().put(message.getId(), "", ExpirationPolicy.CREATED, 30, TimeUnit.SECONDS);
                        message.delete().queue();
                    }
                    sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 252));
                });
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
