package me.purox.devi.commands.info;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UserInfoCommandExecutor implements CommandExecutor {

    private Devi devi;

    public UserInfoCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if(args.length == 0){
            sender.reply(devi.getTranslation(command.getLanguage(), 439));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 440));
            return;
        }

        Member target = command.getEvent().getGuild().getMember(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(devi.getTranslation(command.getLanguage(), 441) + " | " + target.getUser().getName());
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

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 438;
    }

    @Override
    public List<String> getAliases() {
        return null;
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
