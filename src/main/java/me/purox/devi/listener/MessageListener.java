package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.regex.Pattern;

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
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 2, "`" + settings.getStringValue(GuildSettings.Settings.PREFIX) + "`"));
            }
            if (event.getMessage().getContentRaw().contains("discord")) {
                Pattern pattern = Pattern.compile("discord(?:app\\.com|\\.gg)[\\/invite\\/]?(?:(?!.*[Ii10OolL]).[a-zA-Z0-9]{5,6}|[a-zA-Z0-9\\-]{2,32})");
                if (pattern.matcher(event.getMessage().getContentRaw()).find()) {
                    //TODO: anti advertisement
                }
            }
        }
    }
}
