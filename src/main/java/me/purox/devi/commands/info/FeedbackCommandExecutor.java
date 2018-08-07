package me.purox.devi.commands.info;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.bson.Document;

import java.awt.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FeedbackCommandExecutor implements CommandExecutor {

    private Devi devi;
    private long lastUsed;


    public FeedbackCommandExecutor(Devi devi){
        this.devi = devi;
    }


    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        EmbedBuilder error;
        if (args.length <= 0) {
            error = new EmbedBuilder();
            error.setTitle(" ");
            error.setColor(Color.decode("#ff4d4d"));
            error.setDescription("**" + devi.getTranslation(command.getLanguage(), 486) + "**" + "\n" +
                    devi.getTranslation(command.getLanguage(), 484));
            sender.reply(error.build());
            error.clear();
            return;

        }
        long cooldowntime = 1800000;
        if (!(System.currentTimeMillis() - lastUsed >= cooldowntime)) {
            String time = TimeUtils.toRelative(lastUsed + cooldowntime - System.currentTimeMillis());
            error = new EmbedBuilder();
            error.setTitle(" ");
            error.setColor(Color.decode("#ff4d4d"));
            error.setDescription("**" + devi.getTranslation(command.getLanguage(), 486) + "**" + "\n" +
                    devi.getTranslation(command.getLanguage(), 485, time.substring(0, time.length() -4)));
            sender.reply(error.build());
            error.clear();
        } else {
            lastUsed = System.currentTimeMillis();

            String messageRaw = String.join(" ", args);

            Document doc = new Document();
            doc.put("_id", UUID.randomUUID().toString());
            doc.put("username", sender.getName());
            doc.put("discriminator", sender.getDiscriminator());
            doc.put("userid", sender.getId());
            doc.put("date", System.currentTimeMillis());
            doc.put("message", messageRaw);
            doc.put("viewed", false);

            EmbedBuilder message;
            message = new EmbedBuilder();
            message.setTitle("New Feedback Received");
            message.setColor(Color.decode("#4775d1"));
            message.setThumbnail(sender.getEffectiveAvatarUrl());
            message.addField("User", sender.getName() + "#" + sender.getDiscriminator() + " (" + sender.getId() + ")", false);
            message.addField("Message", messageRaw, false);
            devi.sendFeedbackMessage(message.build());

            devi.getDatabaseManager().getClient().getDatabase("website").getCollection("users_feedback").insertOne(doc);

            EmbedBuilder success;
            success = new EmbedBuilder();
            success.setTitle(" ");
            success.setColor(Color.decode("#00e64d"));
            success.setDescription("**" + devi.getTranslation(command.getLanguage(), 487) + "**" + "\n" +
                    devi.getTranslation(command.getLanguage(), 488));
            sender.reply(success.build());

        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 483;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.INFO_COMMANDS;
    }
}
