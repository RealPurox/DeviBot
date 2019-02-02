package net.devibot.provider.commands.predicates;

import net.devibot.provider.Provider;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.entities.ModuleType;

import java.util.Arrays;
import java.util.function.Predicate;

public class CommandModulePredicate implements Predicate<ICommand.Command> {

    @Override
    public boolean test(ICommand.Command command) {
        ICommand iCommand = command.getICommand();
        if (iCommand == null) return false;

        return iCommand.getModuleType() == ModuleType.DEV && Arrays.asList(Provider.getInstance().getDiscordBot().getConfig().getDevelopers()).contains(command.getAuthor().getId());
    }

}
