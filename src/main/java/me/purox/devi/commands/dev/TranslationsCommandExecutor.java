package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class TranslationsCommandExecutor implements CommandExecutor {

    private Devi devi;

    public TranslationsCommandExecutor(Devi devi){
        this.devi = devi;
    }


    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        if (args.length == 0) {
            sender.reply("Invalid usage. " + command.getPrefix() + "translation <view> <id>");
            return;
        }
        if (args[0].equals("view")) {
            if (args.length == 1) {
                sender.reply("You have to enter an ID!");
                return;
            }
            try {
                int id = Integer.parseInt(args[1]);
                StringBuilder builder = new StringBuilder("**[ID] - Language - Text**");
                for (Language lang : Language.values()) {
                    builder.append("\n" + "[`" + id + "`] " + "**" + lang.name() + ":** " + devi.getTranslation(lang, id));
                }
                sender.reply(builder.toString());
            } catch (NumberFormatException e) {
                sender.reply("Invalid id.");
            }
        }

        }


    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
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
        return ModuleType.DEV;
    }
}
