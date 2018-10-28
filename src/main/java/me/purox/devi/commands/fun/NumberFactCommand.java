package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;

public class NumberFactCommand extends ICommand {

    private Devi devi;

    public NumberFactCommand(Devi devi) {
        super("numberfact", "number");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String URL = "http://numbersapi.com/random/trivia";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asString(success -> sender.reply("**"+sender.getName()+"**" + ", " + success.getBody()),
                        error -> sender.reply(devi.getTranslation(command.getLanguage(), 217)));
    }
}
