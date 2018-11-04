package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.entities.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoModListener extends ListenerAdapter {

    private Devi  devi;
    private final Pattern INVITE_LINK = Pattern.compile("discord(?:app\\.com|\\.gg)[\\/invite\\/]?(?:(?!.*[Ii10OolL]).[a-zA-Z0-9]{5,6}|[a-zA-Z0-9\\-]{2,32})");
    private final Pattern DISCORD_ASSETS = Pattern.compile("discordapp\\.com\\/attachments");
    private final Pattern EMOJI = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);
    private final Pattern CAPS = Pattern.compile("[A-Z]");

    public AutoModListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() == null || event.getMember() == null) return;
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        AtomicBoolean hasIgnoredRole = new AtomicBoolean(false);

        if (!event.getMember().getRoles().isEmpty()) {
            event.getMember().getRoles().forEach(r -> deviGuild.getIgnoredRoles().forEach(role -> {
                if (role.getRole().equals(r.getId())) {
                    hasIgnoredRole.set(true);
                }
            }));
        }

        if (!hasIgnoredRole.get() && !event.getAuthor().isBot() && !event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            //anti advertisement
            if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS)) {
                if (INVITE_LINK.matcher(event.getMessage().getContentRaw()).find() && !DISCORD_ASSETS.matcher(event.getMessage().getContentRaw()).find()) {
                    if (MessageUtils.deleteMessage(event.getMessage()))
                        MessageUtils.sendMessageAsync(event.getChannel(), ":warning: " + devi.getTranslation(language, 78, event.getAuthor().getAsMention()));
                }
            }
            //anti caps
            if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_CAPS)) {
                if (event.getMessage().getContentRaw().length() > 10) {
                    String message = event.getMessage().getContentDisplay();
                    Matcher matcher = CAPS.matcher(message);

                    int capsCount = 0;
                    while (matcher.find()) capsCount++;

                    double capsPercentage = (double) capsCount / (double) message.length();
                    if (capsPercentage > 0.70) {
                        if (MessageUtils.deleteMessage(event.getMessage()))
                            MessageUtils.sendMessageAsync(event.getChannel(), ":warning: " + devi.getTranslation(language, 82, event.getAuthor().getAsMention()));
                    }
                }
            }
            //anti emoji spam
            if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_EMOJI)) {
                String message = event.getMessage().getContentRaw().replace(" ", "");
                Matcher matcher = EMOJI.matcher(message);

                Set<Integer> emojis = new HashSet<>();
                while (matcher.find()) {
                    emojis.add(matcher.start());
                }

                int backToBackEmojis = 0;
                for (Integer emoji : emojis) {
                    if (emojis.contains(emoji + 2))
                        backToBackEmojis++;
                }

                if (backToBackEmojis >= 4) {
                    if (MessageUtils.deleteMessage(event.getMessage()))
                        MessageUtils.sendMessageAsync(event.getChannel(), ":warning: " + devi.getTranslation(language, 162, event.getAuthor().getAsMention()));
                }
            }
        }
    }
}
