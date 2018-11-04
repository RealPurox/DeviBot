package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.IMentionable;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class UserInfoCommand extends ICommand {

    private Devi devi;

    public UserInfoCommand(Devi devi) {
        super("userinfo", "user", "userstats");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if(command.getArgs().length == 0){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            EmbedBuilder embed = new EmbedBuilder();

            embed.setAuthor(sender.getName(), null, sender.getEffectiveAvatarUrl());
            embed.setColor(Color.decode("#7289da"));
            embed.setThumbnail(sender.getEffectiveAvatarUrl());

            Member target = command.getGuild().getMember(sender);
            embed.addField(devi.getTranslation(command.getLanguage(), 442), target.getJoinDate().format(formatter), true); // joined guild
            embed.addField(devi.getTranslation(command.getLanguage(), 443), target.getOnlineStatus().toString().replaceAll("_", " "), true); // online status
            embed.addField(devi.getTranslation(command.getLanguage(), 444), target.getNickname() == null ? devi.getTranslation(command.getLanguage(), 445) : target.getNickname(), true); // nickname
            embed.addField(devi.getTranslation(command.getLanguage(), 446), target.getGame() == null ? devi.getTranslation(command.getLanguage(), 445) : target.getGame().getName(), true); // playing
            embed.addField(devi.getTranslation(command.getLanguage(), 447), target.getRoles().isEmpty() ? devi.getTranslation(command.getLanguage(), 445) : target.getRoles().stream().map(IMentionable::getAsMention).collect(Collectors.joining(", ")), true); // roles
            embed.addField(devi.getTranslation(command.getLanguage(), 448), "[" + devi.getTranslation(command.getLanguage(), 91) + "](" + target.getUser().getEffectiveAvatarUrl()+ ")", true);
            sender.reply(embed.build());
            return;
        }

        User user = DiscordUtils.getUser(command.getArgs()[0], command.getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 440));
            return;
        }

        Member target = command.getGuild().getMember(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(target.getUser().getName(), null, target.getUser().getEffectiveAvatarUrl());
        embed.setColor(Color.decode("#7289da"));
        embed.setThumbnail(target.getUser().getEffectiveAvatarUrl());

        embed.addField(devi.getTranslation(command.getLanguage(), 442), target.getJoinDate().format(formatter), true); // joined guild
        embed.addField(devi.getTranslation(command.getLanguage(), 443), target.getOnlineStatus().toString().replaceAll("_", " "), true); // online status
        embed.addField(devi.getTranslation(command.getLanguage(), 444), target.getNickname() == null ? devi.getTranslation(command.getLanguage(), 445) : target.getNickname(), true); // nickname
        embed.addField(devi.getTranslation(command.getLanguage(), 446), target.getGame() == null ? devi.getTranslation(command.getLanguage(), 445) : target.getGame().getName(), true); // playing
        embed.addField(devi.getTranslation(command.getLanguage(), 447), target.getRoles().isEmpty() ? devi.getTranslation(command.getLanguage(), 445) : target.getRoles().stream().map(IMentionable::getAsMention).collect(Collectors.joining(", ")), true); // roles
        embed.addField(devi.getTranslation(command.getLanguage(), 448), "[" + devi.getTranslation(command.getLanguage(), 91) + "](" + target.getUser().getEffectiveAvatarUrl()+ ")", true);

        sender.reply(embed.build());

    }
}
