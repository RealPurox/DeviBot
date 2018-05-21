package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    private Devi devi;

    public MessageListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() != null) {
            GuildSettings settings = devi.getDeviGuild(event.getGuild().getId()).getSettings();
            if (event.getMessage().getContentRaw().equalsIgnoreCase(event.getJDA().getSelfUser().getAsMention())) {
                Language language = Language.getLanguage(settings.getStringValue(GuildSettings.Settings.LANGUAGE));
                MessageUtils.sendMessageAsync(event.getChannel(), devi.getTranslation(language, 2, "`" + settings.getStringValue(GuildSettings.Settings.PREFIX) + "`"));
            }
        }
    }
}
