package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleCommandExecutor implements CommandExecutor {

    private Devi devi;

    public GoogleCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if(args.length < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 577) + " " + command.getPrefix() + "google <searchterm>");
            return;
        }
        String searchterm = Arrays.stream(args).skip(0).collect(Collectors.joining("+"));
        String URL = "http://lmgtfy.com/?q=" + searchterm;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 578), null, "https://i.imgur.com/BWlbFx3.png");
        builder.setColor(Color.decode("#008744"));
        builder.setDescription(URL);
        sender.reply(builder.build());

    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 579;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("lmgtfy");
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.FUN_COMMANDS;
    }
}
