package me.purox.devi.commandsold.guild.handler;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.guild.entities.IgnoredRole;
import me.purox.devi.core.waiter.WaitingResponse;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import org.bson.Document;

import java.awt.*;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoModRolesHandler {

    private Devi devi;
    public AutoModRolesHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(ICommand command, CommandSender sender) {
        WaitingResponse addRole = new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.CUSTOM)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 289))
                .setReplyText("")
                .withCustomCheck((response) -> {
                    String input = response.getMessage().getContentRaw();
                    Role role = DiscordUtils.getRole(input, command.getEvent().getGuild());

                    if (role == null) {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 413));
                        return null;
                    }

                    boolean isIgnoredAlready = false;
                    for (IgnoredRole ignoredRole : command.getDeviGuild().getIgnoredRoles()) {
                        if (ignoredRole.getRole().equals(role.getId())) {
                            isIgnoredAlready = true;
                            break;
                        }
                    }

                    if (isIgnoredAlready) {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 297));
                        return null;
                    }
                    return role;
                })
                .setTryAgainAfterCustomCheckFail(true)
                .withCustomVoid((object) -> {
                    if (object == null) return;
                    Role role = (Role) object;
                    Document document = new Document()
                            .append("guild", command.getEvent().getGuild().getId())
                            .append("role", role.getId());

                    UpdateResult updateResult = devi.getDatabaseManager().saveToDatabase("ignored_roles", document);
                    if (updateResult.wasAcknowledged()) {
                        document.append("_id", updateResult.getUpsertedId().asString().getValue());
                        command.getDeviGuild().getIgnoredRoles().add(Devi.GSON.fromJson(document.toJson(), IgnoredRole.class));
                        sender.reply(Emote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 298, role.getName()));
                    } else {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 264, "<https://www.devibot.net/support>"));

                    }
                })
                .build();

        WaitingResponse removeRole = new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.CUSTOM)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 289))
                .setReplyText("")
                .withCustomCheck((response) -> {
                    String input = response.getMessage().getContentRaw();
                    Role role = DiscordUtils.getRole(input, command.getEvent().getGuild());

                    if (role == null) {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 413));
                        return null;
                    }

                    IgnoredRole ignoredRole = null;
                    for (IgnoredRole ir : command.getDeviGuild().getIgnoredRoles()) {
                        if (ir.getRole().equals(role.getId())) {
                            ignoredRole = ir;
                            break;
                        }
                    }

                    if (ignoredRole == null) {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 299));
                        return null;
                    }

                    return new AbstractMap.SimpleEntry<>(ignoredRole, role);
                })
                .setTryAgainAfterCustomCheckFail(true)
                .withCustomVoid((object) -> {
                    if (object == null) return;
                    IgnoredRole ignoredRole = ((Map.Entry<IgnoredRole, Role>)object).getKey();
                    Role role = ((Map.Entry<IgnoredRole, Role>)object).getValue();

                    Document document = new Document().append("_id", ignoredRole.get_id()).append("role", ignoredRole.getRole()).append("guild", ignoredRole.getGuild());

                    DeleteResult deleteResult = devi.getDatabaseManager().removeFromDatabase("ignored_roles", document.getString("_id"));
                    if (deleteResult.wasAcknowledged()) {
                        command.getDeviGuild().getIgnoredRoles().remove(ignoredRole);
                        sender.reply(Emote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 300, role.getName()));
                    } else {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 264, "<https://www.devibot.net/support>"));
                    }
                })
                .build();

        new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.SELECTOR)
                .addVoidSelectorOption(devi.getTranslation(command.getLanguage(), 276), object -> sendRoles(command, sender))
                .addSelectorOption(devi.getTranslation(command.getLanguage(), 277), addRole)
                .addSelectorOption(devi.getTranslation(command.getLanguage(), 278), removeRole)
                .build().handle();
    }

    private void sendRoles(ICommand command, CommandSender sender) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.decode("#36393E"));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 292));
        builder.appendDescription(devi.getTranslation(command.getLanguage(), 293) + "\n\n");

        Guild guild = command.getEvent().getGuild();
        List<Role> roles = command.getDeviGuild().getIgnoredRoles().stream().map(role -> guild.getRoleById(role.getRole())).collect(Collectors.toList());

        if (roles.size() == 0)
            builder.appendDescription(":no_bell: | " + devi.getTranslation(command.getLanguage(), 295));
        else builder.appendDescription(roles.stream().map(Role::getAsMention).collect(Collectors.joining(", ")));
        builder.appendDescription("\n\n*" + devi.getTranslation(command.getLanguage(), 294) + "*");

        sender.reply(builder.build());
    }
}
