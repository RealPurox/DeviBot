package me.purox.devi.commands.guild.automod;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class AutoModListener extends ListenerAdapter {

    private Devi devi;
    private final Pattern INVITE_LINK = Pattern.compile("discord(?:app\\.com|\\.gg)[\\/invite\\/]?(?:(?!.*[Ii10OolL]).[a-zA-Z0-9]{5,6}|[a-zA-Z0-9\\-]{2,32})");

    public AutoModListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getTextChannel().getType() != ChannelType.TEXT || event.getGuild() == null) return;
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS)) {
            AtomicBoolean hasIgnoredRole = new AtomicBoolean(false);
            event.getMember().getRoles().forEach(r -> {
                if (deviGuild.getAutoModIgnoredRoles().contains(r.getId())) {
                    hasIgnoredRole.set(true);
                }
            });
            if (!hasIgnoredRole.get()) {
                if (INVITE_LINK.matcher(event.getMessage().getContentRaw()).find()) {
                    if (MessageUtils.deleteMessage(event.getMessage()))
                        MessageUtils.sendMessage(event.getChannel(), ":warning: " + devi.getTranslation(language, 78, event.getAuthor().getAsMention()));
                }
            }
        }
    }
}
