package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class JoinCommand implements Command {

    private Devi devi;
    public JoinCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (!event.getMember().getVoiceState().inVoiceChannel()){
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 100));
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()){
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 101));
            return;
        }

        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void onPing(long l) { }

            @Override
            public void onStatusChange(ConnectionStatus connectionStatus) {
                if (connectionStatus == ConnectionStatus.CONNECTED) {
                    String message = "";
                    message += ":white_check_mark: " + devi.getTranslation(language, 105) + "\n";
                    message += "`-` " + devi.getTranslation(language, 106, "`" + prefix + "play <link or yt search>`") + "\n";
                    message += "`-` " + devi.getTranslation(language, 107, "`" + prefix + "stop`", "`" + prefix + "pause`", "`" + prefix + "resume`") + "\n";
                    message += "`-` " + devi.getTranslation(language, 108, "`" + prefix + "current`") + "\n";
                    message += "`-` " + devi.getTranslation(language, 109, "`" + prefix + "queue`") + "\n";
                    message += "`-` " + devi.getTranslation(language, 110, "`" + prefix + "leave`") + "\n";
                    MessageUtils.sendMessage(event.getChannel(), new EmbedBuilder()
                            .setColor(new Color(34, 113, 126))
                            .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                            .setDescription(message)
                            .build());
                } else if (connectionStatus == ConnectionStatus.DISCONNECTED_LOST_PERMISSION) {
                    MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 102));
                } else if (connectionStatus.name().startsWith("ERROR") || (connectionStatus.name().startsWith("DISCONNECTED") && !connectionStatus.name().equals("DISCONNECTED_LOST_PERMISSION"))) {
                    MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 111));
                }
            }

            @Override
            public void onUserSpeaking(User user, boolean b) { }
        };

        try {
            event.getGuild().getAudioManager().setConnectionListener(listener);
            event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        } catch (IllegalArgumentException e) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 100));
        } catch (UnsupportedOperationException e) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 103));
        } catch (GuildUnavailableException e) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 104));
        } catch (InsufficientPermissionException e) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 102));
        }
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 99;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("summon");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
