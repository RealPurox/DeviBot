package me.purox.devi.commands.guild.handler;

import com.mongodb.client.result.UpdateResult;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutoModRolesHandler {

    private Devi devi;
    public AutoModRolesHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(Command command, CommandSender sender) {
        String builder = ":information_source: | Edit Auto-Mod settings -> Roles ignored by Auto-Mod\n\n" +
                "```python\n" +
                "Reply to this message with one of the options listed below to edit your Auto-Mod settings\n\n" +
                " '1' => Display ignored roles\n" +
                " '2' => Add a role to ignored roles\n" +
                " '3' => Remove a role from ignored roles\n" +
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
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 262));
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

                    switch (entered) {
                        //display
                        case 1:
                            sendRoles(command, sender);
                            break;
                        //add
                        case 2:
                            String builder = ":information_source: | Edit Auto-Mod settings -> Add ignore role\n\n" +
                                    "```python\n" +
                                    "Please mention the role you want to be ignored by Auto-Mod or reply with its name or ID" +
                                    "```\nYou can cancel editing your Auto-Mod settings by typing `cancel`";
                            sender.reply(builder);
                            startAddWaiter(1, command, sender);
                            break;
                        //remove
                        case 3:
                            startRemoveWaiter(1, command, sender);
                            break;
                        default:
                            sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 256));
                            startWaiter(nextAttempt, command, sender);
                            break;
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 263)));
    }

    private void sendRoles(Command command, CommandSender sender) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Auto-Mod Ignored Roles");
        builder.appendDescription("All roles that are being ignored by Auto-Mod are listed below.\n");
        builder.appendDescription("Note: You will be ignored by Auto-Mod, no matter if you have one of the roles listed below, if you have the Manage Server permission.\n\n");

        Guild guild = command.getEvent().getGuild();
        List<Role> roles = command.getDeviGuild().getIgnoredRoles().stream().map(doc -> guild.getRoleById(doc.getString("role"))).collect(Collectors.toList());
        System.out.println(command.getDeviGuild().getIgnoredRoles());
        System.out.println(roles);

        sender.reply(builder.build());
    }

    private void startAddWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 262));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(":no_entry: | You've failed to enter a valid role 3 times in a row. Auto-Mod settings selection has been cancelled");
                        return;
                    }

                    String input = response.getMessage().getContentRaw();
                    Role role = DiscordUtils.getRole(input, command.getEvent().getGuild());

                    if (role == null) {
                        sender.reply(":no_entry: | Provided role was not found, please try again.");
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
                        sender.reply(":no_entry: | Provided role is already being ignored by Auto-Mod");
                        return;
                    }

                    Document document = new Document()
                            .append("guild", command.getEvent().getGuild().getId())
                            .append("role", role.getId());

                    UpdateResult updateResult = devi.getDatabaseManager().saveToDatabase("ignored_roles", document);
                    if (updateResult.wasAcknowledged()) {
                        command.getDeviGuild().getIgnoredRoles().add(document);
                        sender.reply(":ok_hand: | The role " + role.getName() + " will now be ignored by Auto-Mod");
                    } else {
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 264, "<https://www.devibot.net/support>"));
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 263)));
    }

    private void startRemoveWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 262));
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
                        sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 256));
                        startRemoveWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ENABLED, entered == 1);
                    command.getDeviGuild().saveSettings();

                    if (entered == 1)
                        sender.reply(":ok_hand: | Auto-Mod has been enabled");
                    else sender.reply(":ok_hand: | Auto-Mod has been disabled");
                },
                15, TimeUnit.SECONDS, () -> sender.reply(":no_entry: | " + devi.getTranslation(command.getLanguage(), 263)));
    }
}
