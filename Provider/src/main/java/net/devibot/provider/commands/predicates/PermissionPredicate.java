package net.devibot.provider.commands.predicates;

import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.Provider;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.entities.Emote;
import net.devibot.provider.entities.Language;
import net.devibot.provider.utils.MessageUtils;
import net.devibot.provider.utils.Translator;

import java.util.Arrays;
import java.util.function.Predicate;

public class PermissionPredicate implements Predicate<ICommand.Command> {

    @Override
    public boolean test(ICommand.Command command) {
        ICommand iCommand = command.getICommand();
        if (iCommand == null) return false;

        //it's a dev
        if (Arrays.asList(Provider.getInstance().getDiscordBot().getConfig().getDevelopers()).contains(command.getAuthor().getId()))
            return true;

        if (command.getGuild() != null && iCommand.getPermission() != null) {
            if (!command.getMember().hasPermission(command.getTextChannel(), iCommand.getPermission())) {
                DeviGuild deviGuild = Provider.getInstance().getDiscordBot().getCacheManager().getDeviGuildCache().getDeviGuild(command.getGuild().getId());
                MessageUtils.sendMessage(command.getTextChannel(), Emote.ERROR + " | " + Translator.getTranslation(Language.getLanguage(deviGuild.getLanguage()), 31));
                return false;
            }
        }
        return true;
    }
}
