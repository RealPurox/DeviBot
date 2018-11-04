package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CatCommand extends ICommand {

    private Devi devi;

    public CatCommand(Devi devi) {
        super("cat");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String URL = "https://cataas.com/cat";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asString(success -> {
                    if (success.getStatus() == 404 || success.getBody() == null) {
                        sender.reply(Emote.ERROR + "| " + devi.getTranslation(command.getLanguage(), 217));
                        return;
                    }
                    MessageBuilder message = new MessageBuilder();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(devi.getTranslation(command.getLanguage(), 540));
                    try {
                        InputStream file = new URL(URL).openStream();
                        embed.setImage("attachment://cat.png");
                        message.setEmbed(embed.build());
                        command.getChannel().sendFile(file, "cat.png", message.build()).queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.reply(devi.getTranslation(command.getLanguage(), 544));
                    }
                });
    }
}
