package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class JoinCommandExecutor implements CommandExecutor {

    private Devi devi;
    public JoinCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!command.getEvent().getMember().getVoiceState().inVoiceChannel()){
            sender.reply(devi.getTranslation(command.getLanguage(), 100));
            return;
        }

        if (command.getEvent().getGuild().getSelfMember().getVoiceState().inVoiceChannel()){
            sender.reply(devi.getTranslation(command.getLanguage(), 101));
            return;
        }

        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void onPing(long l) { }

            @Override
            public void onStatusChange(ConnectionStatus connectionStatus) {
                if (connectionStatus == ConnectionStatus.CONNECTED) {
                    String message = "";
                    message += ":white_check_mark: " + devi.getTranslation(command.getLanguage(), 105) + "\n";
                    message += "`-` " + devi.getTranslation(command.getLanguage(), 106, "`" + command.getPrefix() + "play <link or yt search>`") + "\n";
                    message += "`-` " + devi.getTranslation(command.getLanguage(), 107, "`" + command.getPrefix() + "stop`", "`" + command.getPrefix() + "pause`", "`" + command.getPrefix() + "resume`") + "\n";
                    message += "`-` " + devi.getTranslation(command.getLanguage(), 108, "`" + command.getPrefix() + "current`") + "\n";
                    message += "`-` " + devi.getTranslation(command.getLanguage(), 109, "`" + command.getPrefix() + "queue`") + "\n";
                    message += "`-` " + devi.getTranslation(command.getLanguage(), 110, "`" + command.getPrefix() + "leave`") + "\n";
                    sender.reply(new EmbedBuilder()
                            .setColor(new Color(34, 113, 126))
                            .setAuthor(devi.getTranslation(command.getLanguage(), 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                            .setDescription(message)
                            .build());
                    //create player if it doesn't exist
                    devi.getMusicManager().getPlayer(command.getEvent().getGuild());
                } else if (connectionStatus == ConnectionStatus.DISCONNECTED_LOST_PERMISSION) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 102));
                } else if (connectionStatus.name().startsWith("ERROR") || (connectionStatus.name().startsWith("DISCONNECTED") && !connectionStatus.name().equals("DISCONNECTED_LOST_PERMISSION"))) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 111));
                }
            }

            @Override
            public void onUserSpeaking(User user, boolean b) { }
        };

        try {
            command.getEvent().getGuild().getAudioManager().setConnectionListener(listener);
            command.getEvent().getGuild().getAudioManager().openAudioConnection(command.getEvent().getMember().getVoiceState().getChannel());
        } catch (IllegalArgumentException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 100));
        } catch (UnsupportedOperationException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 103));
        } catch (GuildUnavailableException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 104));
        } catch (InsufficientPermissionException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 102));
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
