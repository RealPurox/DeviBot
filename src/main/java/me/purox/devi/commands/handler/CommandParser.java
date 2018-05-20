package me.purox.devi.commands.handler;

import me.purox.devi.commands.handler.impl.CommandImpl;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CommandParser {

    private Devi devi;
    public CommandParser(Devi devi) {
        this.devi = devi;
    }

    CommandContainer parseCommand(String raw, MessageReceivedEvent event){
        raw = raw.substring(1);
        String[] split = raw.split(" ");
        List<String> list = new ArrayList<>(Arrays.asList(split));

        String[] args = list.subList(1, list.size()).toArray(new String[0]);

        return new CommandContainer(args, new CommandImpl(devi, event));
    }

    public class CommandContainer{
        private final String[] args;
        private final Command command;

        CommandContainer(String[] args, Command command) {
            this.args = args;
            this.command = command;
        }

        public String[] getArgs() {
            return args;
        }

        public Command getCommand() {
            return command;
        }
    }
}
