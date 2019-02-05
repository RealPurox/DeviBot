package net.devibot.provider.commands.predicates;

import net.devibot.core.entities.Ban;
import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.commands.ICommand;
import net.devibot.core.entities.Emote;
import net.devibot.core.entities.Language;
import net.devibot.provider.utils.MessageUtils;
import net.devibot.provider.utils.Translator;

import java.util.function.Predicate;

public class UserBannedPredicate implements Predicate<ICommand.Command> {

    @Override
    public boolean test(ICommand.Command command) {
        ICommand iCommand = command.getICommand();
        if (iCommand == null)
            return false;

        Ban ban = command.getUser().getBan();
        if (ban.isActive()) {
            DeviGuild deviGuild = command.getDeviGuild();
            MessageUtils.sendMessage(command.getTextChannel(), Emote.ERROR + " | " + Translator.getTranslationOLD(deviGuild == null ? Language.ENGLISH : Language.getLanguage(deviGuild.getLanguage()), 634, command.getAuthor().getAsMention()));
        }

        return !ban.isActive(); //user is banned => test failed => false
    }
}
