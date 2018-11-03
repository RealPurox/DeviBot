package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import org.bson.Document;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class FeedbackCommand extends ICommand {

    private Devi devi;
    private HashMap<Long, Long> cooldown;

    public FeedbackCommand(Devi devi) {
        super("feedback");
        this.devi = devi;
        this.cooldown = new HashMap<>();
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if (args.length <= 0) {
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle(" ");
            error.setColor(Color.decode("#ff4d4d"));
            error.setDescription("**" + devi.getTranslation(command.getLanguage(), 486) + "**" + "\n" +
                    devi.getTranslation(command.getLanguage(), 484));
            sender.reply(error.build());
            return;

        }

        long cooldowntime = 1800000;
        if (!cooldown.containsKey(command.getAuthor().getIdLong()) || !(System.currentTimeMillis() - cooldown.get(command.getAuthor().getIdLong()) >= cooldowntime)) {
            String time = TimeUtils.toRelative(cooldown.get(command.getAuthor().getIdLong()) + cooldowntime - System.currentTimeMillis());
            EmbedBuilder error = new EmbedBuilder();
            error.setTitle(" ");
            error.setColor(Color.decode("#ff4d4d"));
            error.setDescription("**" + devi.getTranslation(command.getLanguage(), 486) + "**" + "\n" +
                    devi.getTranslation(command.getLanguage(), 485, time.substring(0, time.length() -4)));
            sender.reply(error.build());
        } else {
            cooldown.put(command.getAuthor().getIdLong(), System.currentTimeMillis());

            String messageRaw = String.join(" ", args);

            Document doc = new Document();
            doc.put("_id", UUID.randomUUID().toString());
            doc.put("username", sender.getName());
            doc.put("discriminator", sender.getDiscriminator());
            doc.put("userid", sender.getId());
            doc.put("date", System.currentTimeMillis());
            doc.put("message", messageRaw);
            doc.put("viewed", false);

            EmbedBuilder message = new EmbedBuilder();
            message.setTitle("New Feedback Received");
            message.setColor(Color.decode("#4775d1"));
            message.setThumbnail(sender.getEffectiveAvatarUrl());
            message.addField("User", sender.getName() + "#" + sender.getDiscriminator() + " (" + sender.getId() + ")", false);
            message.addField("Message", messageRaw, false);
            devi.sendFeedbackMessage(message.build());

            devi.getDatabaseManager().getClient().getDatabase("website").getCollection("users_feedback").insertOne(doc);

            EmbedBuilder success = new EmbedBuilder();
            success.setTitle(" ");
            success.setColor(Color.decode("#00e64d"));
            success.setDescription("**" + devi.getTranslation(command.getLanguage(), 487) + "**" + "\n" +
                    devi.getTranslation(command.getLanguage(), 488));
            sender.reply(success.build());

        }
    }
}
