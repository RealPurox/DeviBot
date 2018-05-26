package me.purox.devi.commands.info;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;

import java.util.Collections;
import java.util.List;

public class NumberFactCommandExecutor implements CommandExecutor {

    private Devi devi;

    public NumberFactCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        try {
            String URL = "http://numbersapi.com/random/trivia";
            String fact = Unirest.get(URL).asString().getBody();
            sender.reply(sender.getAsMention() + ", " + fact);
        } catch (UnirestException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 217));
        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 216;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("number");
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
