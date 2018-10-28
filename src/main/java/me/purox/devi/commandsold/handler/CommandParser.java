package me.purox.devi.commandsold.handler;

import me.purox.devi.commandsold.handler.impl.ICommandImpl;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CommandParser {

    private Devi devi;

    CommandParser(Devi devi) {
        this.devi = devi;
    }

    CommandContainer parseCommand(String raw, MessageReceivedEvent event){
        raw = raw.substring(1);
        String[] split = raw.split(" ");
        List<String> list = new ArrayList<>(Arrays.asList(split));

        String[] args = list.subList(1, list.size()).toArray(new String[0]);

        return new CommandContainer(args, new ICommandImpl(devi, event));
    }

    public class CommandContainer{
        private final String[] args;
        private final ICommand command;

        CommandContainer(String[] args, ICommand command) {
            this.args = args;
            this.command = command;
        }

        public String[] getArgs() {
            return args;
        }

        public ICommand getCommand() {
            return command;
        }
    }
}
