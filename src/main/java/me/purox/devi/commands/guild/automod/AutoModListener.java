package me.purox.devi.commands.guild.automod;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS)) {
            if (INVITE_LINK.matcher(event.getMessage().getContentRaw()).find()) {
                //TODO: anti advertisement
            }
        }
    }
}
