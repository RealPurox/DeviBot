package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
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
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(success -> sender.reply(sender.getAsMention() + ", " + success.getBody().getString("value")),
                        error -> sender.reply(devi.getTranslation(command.getLanguage(), 217)));
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
