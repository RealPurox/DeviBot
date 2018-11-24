package me.purox.devi.commands.admin.translators;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.entities.Language;

public class TranslationProgressCommand extends ICommand {

    private Devi devi;

    public TranslationProgressCommand(Devi devi) {
        super("progress", "translationprogress");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        /*id = staff guild*/
        DeviGuild deviGuild = devi.getDeviGuild("392264119102996480");

        if (!(command.getDeviGuild() == deviGuild)) return;
        if (!devi.getTranslators().contains(sender.getId()) || (!devi.getAdmins().contains(sender.getId()))) return;

        StringBuilder builder = new StringBuilder();

        builder.append("```").append("--== Translation Progress ==--\n\n");

        int totalTrans = devi.getDeviTranslations().get(Language.ENGLISH).keySet().size();
        /*insert triggered feminist here*/

        for (Language language : Language.values()) {
            int translated = devi.getDeviTranslations().get(language).keySet().size();
            builder.append(language.getName()).append(" -> ").append(Math.round(((double) translated / (double) totalTrans) * 100)).append("%\n");
        }

        builder.append("```");

        sender.reply(builder.toString());
    }
}
