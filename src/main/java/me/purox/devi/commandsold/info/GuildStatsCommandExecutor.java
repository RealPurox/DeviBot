package me.purox.devi.commandsold.info;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class GuildStatsCommandExecutor implements CommandExecutor {

    private Devi devi;

    public GuildStatsCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        Guild guild = command.getEvent().getGuild();

        int channels = guild.getVoiceChannels().size() + guild.getTextChannels().size();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(devi.getTranslation(command.getLanguage(), 427));
        embed.setColor(Color.decode("#7289da"));

        embed.addField(devi.getTranslation(command.getLanguage(), 428), guild.getName(), true);
        embed.addField(devi.getTranslation(command.getLanguage(), 430), guild.getId(), true);
        embed.addField(devi.getTranslation(command.getLanguage(), 429), guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true);
        embed.addField(devi.getTranslation(command.getLanguage(), 431), guild.getRegion().getName() + " " + guild.getRegion().getEmoji(), true);
        embed.addField(devi.getTranslation(command.getLanguage(), 432), guild.getCreationTime().format(formatter), true);
        embed.addField(devi.getTranslation(command.getLanguage(), 433), Integer.valueOf(guild.getRoles().size()).toString(), true);
        embed.addField(devi.getTranslation(command.getLanguage(), 435), Integer.valueOf(guild.getMembers().size()).toString(), true);
        embed.addField(devi.getTranslation(command.getLanguage(), 437), channels +
                " [ " + devi.getTranslation(command.getLanguage(), 434) + ": " + Integer.valueOf(guild.getTextChannels().size()).toString() +
                " | " + devi.getTranslation(command.getLanguage(), 436) + ": " + Integer.valueOf(guild.getVoiceChannels().size()).toString() + " ] ", false);

        sender.reply(embed.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 426;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("guild", "guildinfo");
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.INFO_COMMANDS;
    }
}
