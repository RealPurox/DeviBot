package net.devibot.provider.listener.automod.predicates;

import net.devibot.provider.Provider;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class InvitesPredicate implements Predicate<Message> {

    private String[] admins = Provider.getInstance().getConfig().getDevelopers();

    private final Pattern DISCORD_LINK = Pattern.compile("discord(?:app\\.com|\\.gg)[\\/invite\\/]?(?:(?!.*[Ii10OolL]).[a-zA-Z0-9]{5,6}|[a-zA-Z0-9\\-]{2,32})");
    private final Pattern DISCORD_ASSETS = Pattern.compile("discordapp\\.com\\/attachments");

    @Override
    public boolean test(Message message) {
        //message was sent by a developer
        if (Arrays.stream(admins).noneMatch(id -> id.equals(message.getAuthor().getId()))) return false;

        String input = message.getContentRaw();

        // TODO: 06/02/2019 ignored roles and devi developer
        return DISCORD_LINK.matcher(input).matches() && !DISCORD_ASSETS.matcher(input).matches();
    }
}
