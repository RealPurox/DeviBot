package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class AdminStatsCommandExecutor implements CommandExecutor {

    private Devi devi;

    public AdminStatsCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        StringBuilder builder = new StringBuilder();

        builder.append("```").append("-- == Translations == --\n\n");

        int totalTrans = devi.getDeviTranslations().get(Language.ENGLISH).keySet().size();

        for (Language language : Language.values()) {
            int translated = devi.getDeviTranslations().get(language).keySet().size();
            builder.append(language.getName()).append(" -> ").append(Math.round(((double) translated / (double) totalTrans) * 100)).append("%\n");
        }


        builder.append("```");

        sender.reply(builder.toString());
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
        return null;
    }
}
