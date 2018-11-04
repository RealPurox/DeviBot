package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import org.json.JSONObject;

public class CatFactCommand extends ICommand {

    private Devi devi;

    public CatFactCommand(Devi devi) {
        super("catfact");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String URL = "https://catfact.ninja/fact";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(success -> {
                    JSONObject body = success.getBody();
                    if (success.getStatus() == 404 || success.getBody() == null) {
                        sender.reply(Emote.ERROR + "| " + devi.getTranslation(command.getLanguage(), 217));
                        return;
                    }
                    sender.reply("**" + sender.getName() + "**" + ", " + body.getString("fact"));
                });
    }
}
