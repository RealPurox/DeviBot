package me.purox.devi.commands.nsfw;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;

public class UrbanDictionaryCommand extends ICommand {

    private Devi devi;

    public UrbanDictionaryCommand(Devi devi) {
        super("urban", "urbandic", "ub");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!(command.getTextChannel().isNSFW())) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 559));
            return;
        }
        if (command.getArgs().length == 0) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 560, command.getPrefix() + "urban <term>"));
            return;
        }

        String search = String.join("%20", command.getArgs());
        String URL = "http://api.urbandictionary.com/v0/define?term=" + search;
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(response -> {
                    if (response.getStatus() == 404 || response.getBody().getJSONArray("list").length() == 0) {
                        sender.reply(devi.getTranslation(command.getLanguage(), 561));
                        return;
                    }
                    JSONObject body = response.getBody();
                    JSONObject urban = body.getJSONArray("list").getJSONObject(0);

                    EmbedBuilder em = new EmbedBuilder();
                    em.setAuthor(devi.getTranslation(command.getLanguage(), 562) + ": " + urban.getString("word"), null, "https://i.imgur.com/HJFnmOq.png");
                    em.setColor(Color.decode("#1D2439"));

                    String definition = urban.getString("definition").replace("[", "").replace("]", "");
                    String example = urban.getString("example").replace("[", "").replace("]", "");
                    String link = urban.getString("permalink");

                    em.addField("**" + devi.getTranslation(command.getLanguage(), 563) + "**", definition.length() > 1024 ? definition.substring(0, 900) + "... " + devi.getTranslation(command.getLanguage(), 565, link): definition, true);
                    em.addField("**" + devi.getTranslation(command.getLanguage(), 564) + "**", example.length() > 1024 ? example.substring(0, 900) + "... " + devi.getTranslation(command.getLanguage(), 565, link) : example, true);
                    em.addField("**" + devi.getTranslation(command.getLanguage(), 566) + "**", Emote.SUCCESS + " " + urban.getInt("thumbs_up") + " | " + Emote.ERROR + " " + urban.getInt("thumbs_down"), true);
                    em.addField("**" + devi.getTranslation(command.getLanguage(), 567) + "**", link, true);

                    sender.reply(em.build());
                });
    }
}
