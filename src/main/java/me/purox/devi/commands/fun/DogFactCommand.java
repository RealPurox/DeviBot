package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class DogFactCommand extends ICommand {

    private Devi devi;

    public DogFactCommand(Devi devi) {
        super("dogfact");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String URL = "https://dog-api.kinduff.com/api/facts";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(success -> {
                    if (success.getStatus() == 404 || success.getBody() == null) {
                        sender.reply(Emote.ERROR + "| " + devi.getTranslation(command.getLanguage(), 217));
                        return;
                    }

                    JSONObject body = success.getBody();
                    JSONArray fact = body.getJSONArray("facts");
                    sender.reply("**" + sender.getName() + "**" + ", " + fact.getString(0));
                });
    }
}
