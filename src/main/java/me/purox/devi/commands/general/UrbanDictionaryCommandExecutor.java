package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class UrbanDictionaryCommandExecutor implements CommandExecutor {

    private Devi devi;

    public UrbanDictionaryCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!(command.getEvent().getTextChannel().isNSFW())) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 559));
            return;
        }
        if (args.length == 0) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 560, command.getPrefix() + "urban <term>"));
            return;
        }

        String search = String.join("%20", args);
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
                    
                    em.addField("**" + devi.getTranslation(command.getLanguage(), 563) + "**", definition.length() >= 1024 ? definition.substring(0, 900) + "... " + devi.getTranslation(command.getLanguage(), 565, link): definition, true);
                    em.addField("**" + devi.getTranslation(command.getLanguage(), 564) + "**", example.length() > 1024 ? example.substring(0, 900) + "... " + devi.getTranslation(command.getLanguage(), 565, link) : example, true);
                    em.addField("**" + devi.getTranslation(command.getLanguage(), 566) + "**", Emote.SUCCESS + " " + urban.getInt("thumbs_up") + " | " + Emote.ERROR + " " + urban.getInt("thumbs_down"), true);
                    em.addField("**" + devi.getTranslation(command.getLanguage(), 567) + "**", urban.getString("permalink"), true);

                    sender.reply(em.build());
                });
            }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 568;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("urbandictionary", "ub");
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.NSFW_COMMANDS;
    }
}
