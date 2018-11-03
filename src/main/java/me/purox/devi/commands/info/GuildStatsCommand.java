package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class GuildStatsCommand extends ICommand {

    private Devi devi;

    public GuildStatsCommand(Devi devi) {
        super("guildstats", "guild", "guildinfo");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        Guild guild = command.getGuild();

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
}
