package me.purox.devi.commands.admin;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;

public class AdminStatsCommand extends ICommand {

    private Devi devi;

    public AdminStatsCommand(Devi devi) {
        super("adminstats");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
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
}
