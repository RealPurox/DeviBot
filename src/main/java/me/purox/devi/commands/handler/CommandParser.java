package me.purox.devi.commands.handler;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParser {

    public static CommandContainer parseCommand(String raw, MessageReceivedEvent event){
        raw = raw.substring(1);
        String[] split = raw.split(" ");
        String invoke = split[0];
        List<String> list = new ArrayList<>(Arrays.asList(split));

        String[] args = list.subList(1, list.size()).toArray(new String[0]);

        return new CommandContainer(invoke, args, event);
    }

    public static class CommandContainer{
        private final String invoke;
        private final String[] args;
        private final MessageReceivedEvent event;

        public CommandContainer(String invoke, String[] args, MessageReceivedEvent event) {
            this.invoke = invoke;
            this.args = args;
            this.event = event;
        }

        public String getInvoke() {
            return invoke;
        }

        public String[] getArgs() {
            return args;
        }

        public MessageReceivedEvent getEvent() {
            return event;
        }
    }
}
