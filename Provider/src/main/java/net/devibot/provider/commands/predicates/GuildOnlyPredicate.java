package net.devibot.provider.commands.predicates;

import net.devibot.provider.commands.ICommand;
import net.devibot.core.entities.Emote;
import net.devibot.core.entities.Language;
import net.devibot.provider.utils.MessageUtils;
import net.devibot.provider.utils.Translator;

import java.util.function.Predicate;

public class GuildOnlyPredicate implements Predicate<ICommand.Command> {

    @Override
    public boolean test(ICommand.Command command) {
        ICommand iCommand = command.getICommand();
        if (iCommand == null)
            return false;

        if (iCommand.isGuildOnly() && command.getGuild() == null) {
            MessageUtils.sendMessage(command.getTextChannel(), Emote.ERROR + " | " + Translator.getTranslationOLD(Language.ENGLISH, 1));
            return false;
        }
        return true;
    }
}
