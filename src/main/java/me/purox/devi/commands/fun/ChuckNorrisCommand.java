package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;

public class ChuckNorrisCommand extends ICommand {

    private Devi devi;

    public ChuckNorrisCommand(Devi devi) {
        super("chucknorris", "chuck", "norris");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String URL = "https://api.chucknorris.io/jokes/random";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(success -> sender.reply("**" + sender.getName() + "**, " + success.getBody().getString("value")),
                        error -> sender.reply(devi.getTranslation(command.getLanguage(), 217)));
    }
}
