package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;

public class DogCommand extends ICommand {

    private Devi devi;

    public DogCommand(Devi devi) {
        super("dog", "doggo");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String URL = "https://dog.ceo/api/breeds/image/random";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(success -> {
                    JSONObject body = success.getBody();
                    if (success.getStatus() == 404 || success.getBody() == null) {
                        sender.reply(Emote.ERROR + "| " + devi.getTranslation(command.getLanguage(), 217));
                        return;
                    }
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(devi.getTranslation(command.getLanguage(), 538));
                    embed.setImage(body.getString("message"));
                    sender.reply(embed.build());
                });
    }
}
