package me.purox.devi.commands.guild.handler;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.bson.Document;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutoModRolesHandler {

    private Devi devi;
    public AutoModRolesHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(Command command, CommandSender sender) {
        String builder = DeviEmote.INFO.get() + " | " + devi.getTranslation(command.getLanguage(), 266) + " -> " + devi.getTranslation(command.getLanguage(), 275) + "\n\n" +
                "```python\n" +
                devi.getTranslation(command.getLanguage(), 268) + "\n\n" +
                " '1' => " + devi.getTranslation(command.getLanguage(), 276) + "\n" +
                " '2' => " + devi.getTranslation(command.getLanguage(), 277) + "\n" +
                " '3' => " + devi.getTranslation(command.getLanguage(), 278) + "\n" +
                "```\n" + devi.getTranslation(command.getLanguage(), 271, "`cancel`");

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
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 262));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 279));
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
                        //display
                        case 1:
                            sendRoles(command, sender);
                            break;
                        //add
                        case 2:
                            String addResponse = DeviEmote.INFO.get() + " | " + devi.getTranslation(command.getLanguage(), 266) + " -> " + devi.getTranslation(command.getLanguage(), 288) + "\n\n" +
                                    "```python\n" +
                                    devi.getTranslation(command.getLanguage(), 289) +
                                    "```\n" + devi.getTranslation(command.getLanguage(), 271, "`cancel`");
                            sender.reply(addResponse);
                            startAddWaiter(1, command, sender);
                            break;
                        //remove
                        case 3:
                            String removeResponse = DeviEmote.INFO.get() + " | " + devi.getTranslation(command.getLanguage(), 266) + "  -> " + devi.getTranslation(command.getLanguage(), 290) + "\n\n" +
                                    "```python\n" +
                                    devi.getTranslation(command.getLanguage(), 291) +
                                    "```\n" + devi.getTranslation(command.getLanguage(), 271, "`cancel`");
                            sender.reply(removeResponse);
                            startRemoveWaiter(1, command, sender);
                            break;
                        default:
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 265));
                            startWaiter(nextAttempt, command, sender);
                            break;
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 263)));
    }

    private void sendRoles(Command command, CommandSender sender) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(213, 123, 23));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 292));
        builder.appendDescription(devi.getTranslation(command.getLanguage(), 293) + "\n");
        builder.appendDescription(devi.getTranslation(command.getLanguage(), 294)+ "\n\n");

        Guild guild = command.getEvent().getGuild();
        List<Role> roles = command.getDeviGuild().getIgnoredRoles().stream().map(doc -> guild.getRoleById(doc.getString("role"))).collect(Collectors.toList());

        if (roles.size() == 0)
            builder.appendDescription(":no_bell: | " + devi.getTranslation(command.getLanguage(), 295));
        else builder.appendDescription(roles.stream().map(Role::getAsMention).collect(Collectors.joining(", ")));

        sender.reply(builder.build());
    }

    private void startAddWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 262));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 310));
                        return;
                    }

                    String input = response.getMessage().getContentRaw();
                    Role role = DiscordUtils.getRole(input, command.getEvent().getGuild());

                    if (role == null) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 296));
                        startAddWaiter(nextAttempt, command, sender);
                        return;
                    }

                    boolean isIgnoredAlready = false;
                    for (Document doc : command.getDeviGuild().getIgnoredRoles()) {
                        if (doc.getString("role").equals(role.getId())) {
                            isIgnoredAlready = true;
                            break;
                        }
                    }

                    if (isIgnoredAlready) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 297));
                        return;
                    }

                    Document document = new Document()
                            .append("guild", command.getEvent().getGuild().getId())
                            .append("role", role.getId());

                    UpdateResult updateResult = devi.getDatabaseManager().saveToDatabase("ignored_roles", document);
                    if (updateResult.wasAcknowledged()) {
                        command.getDeviGuild().getIgnoredRoles().add(document);
                        sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 298, role.getName()));
                    } else {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 264, "<https://www.devibot.net/support>"));
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 263)));
    }

    private void startRemoveWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 262));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 310));
                        return;
                    }

                    String input = response.getMessage().getContentRaw();
                    Role role = DiscordUtils.getRole(input, command.getEvent().getGuild());

                    if (role == null) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 296));
                        startRemoveWaiter(nextAttempt, command, sender);
                        return;
                    }

                    Document document = null;
                    for (Document doc : command.getDeviGuild().getIgnoredRoles()) {
                        if (doc.getString("role").equals(role.getId())) {
                            document = doc;
                            break;
                        }
                    }

                    if (document == null) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 299));
                        return;
                    }

                    DeleteResult deleteResult = devi.getDatabaseManager().removeFromDatabase("ignored_roles", document.getString("_id"));
                    if (deleteResult.wasAcknowledged()) {
                        command.getDeviGuild().getIgnoredRoles().remove(document);
                        sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 300, role.getName()));
                    } else {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 264, "<https://www.devibot.net/support>"));
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 263)));
    }
}
