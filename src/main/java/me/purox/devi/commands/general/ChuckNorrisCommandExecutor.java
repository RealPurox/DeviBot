package me.purox.devi.commands.general;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;

import java.util.Arrays;
import java.util.List;

public class ChuckNorrisCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ChuckNorrisCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        String URL = "https://api.chucknorris.io/jokes/random";
        Unirest.get(URL).asStringAsync(new Callback<String>() {
            @Override
            public void completed(HttpResponse<String> response) {
                sender.reply(sender.getAsMention() + ", " + response.getBody());
            }

            @Override
            public void failed(UnirestException e) {
                sender.reply(devi.getTranslation(command.getLanguage(), 217));
            }

            @Override
            public void cancelled() {
                sender.reply(devi.getTranslation(command.getLanguage(), 217));
            }
        });
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 218;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("chuck", "norris");
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
