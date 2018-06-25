package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponse;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class MusicLogCommandExecutor implements CommandExecutor {

    private Devi devi;

    public MusicLogCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        TextChannel textChannel = DiscordUtils.getTextChannel(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUSIC_LOG_CHANNEL), command.getEvent().getGuild());

        WaitingResponse enabled = new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.BOOLEAN)
                .setSetting(GuildSettings.Settings.MUSIC_LOG_ENABLED)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 450)).build();

        WaitingResponse channel = new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.CHANNEL)
                .setSetting(GuildSettings.Settings.MUSIC_LOG_CHANNEL)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 449)).build();

        new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.SELECTOR)
                .addSelectorOption(devi.getTranslation(command.getLanguage(), 346), enabled)
                .addSelectorOption(devi.getTranslation(command.getLanguage(), 347) +
                        " (" + devi.getTranslation(command.getLanguage(), textChannel == null ? 348 : 349, "#" + (textChannel != null ? textChannel.getName() : "??") + ")"), channel)
                .build().handle();
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 318;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MUSIC;
    }
}
