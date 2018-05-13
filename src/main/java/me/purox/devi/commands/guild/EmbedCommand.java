package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildEmbed;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.bson.Document;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class EmbedCommand implements Command {

    private Devi devi;

    public EmbedCommand(Devi devi) {
        this.devi = devi;
    }

    private List<String> stringKeys  = Arrays.asList("authorname", "authorurl", "authoriconurl", "footertext", "footericonurl", "description", "imageurl", "thumbnailurl", "title", "titleurl");
    private List<String> integerKeys = Arrays.asList("colorred", "colorgreen", "colorblue");

    //embed create DONE
    //embed delete <id> DONE
    //embed preview <id> DONE
    //embed set <id> <key> <value>
    //embed remove <id> <key>
    //embed addfield <id> {value:<fieldKey>} {value:<fieldValue>} <inline>
    //embed removefield <id> <fieldKey>

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setTitle("Custom Guild Embeds");

        Document embeds = deviGuild.getEmbeds();

        if (args.length == 0 ) {
            if (embeds.size() == 0) {
                MessageUtils.sendMessage(event.getChannel(), "You don't have any embeds registered. Use `!embed create` to create your first embed!");
                return;
            } else {
                MessageUtils.sendMessage(event.getChannel(), builder.setDescription("There are " + embeds.size() + " embeds registered on your guild.\nUse `!embed preview <id>` to preview your embeds.\n\nEmbeds ID List:\n`" +
                        embeds.keySet().stream().collect(Collectors.joining("`\n`")) + "`").build());
                return;
            }
        }

        //<editor-fold desc="create">
        if(args[0].equalsIgnoreCase("create")) {
            if(embeds.size() == 10) {
                MessageUtils.sendMessage(event.getChannel(), "Due to performance reasons, we have to limit the amount of custom embeds per guild to 10. You've reached that limit.");
                return;
            }
            GuildEmbed guildEmbed = new GuildEmbed();
            Random random = new Random();

            String id = String.valueOf(random.nextInt(9999));
            if(embeds.containsKey(id)) id = String.valueOf(random.nextInt(9999));

            String sb = "";
            sb += "Successfully created a new GuildEmbed with ID `" + id + "`.\n\n";
            sb += "> In order to edit your embed simply type `!embed " + id + " set <key> <value>`\n";
            sb += "- Example: `!embed " + id + " title This is an example title`\n";
            sb += "> To remove a key you've set, use `!embed " + id + " remove <key>`\n";
            sb += "- Example: `!embed " + id + " remove title`\n";
            sb += "> In order to add a field to your embed, type `!embed " + id + " addfield {key:<fieldKey>} {value:<fieldValue>} <inline>`\n";
            sb += "- Example: `!embed " + id + " addfield {value:This is an example field} {key:I am an example field!} true`\n";
            sb += "> You want to remove a field from your embed? Just type `!embed removefield <fieldKey>`\n";
            sb += "- Example: `!embed " + id + " removefield This is an example field`\n\n";
            sb += "Sounds pretty complicated, huh? Don't worry you can find a detailed tutorial on our website here: http://vps530024.ovh.net/tutorial/how-to-create-an-embed.php.";

            MessageUtils.sendMessage(event.getChannel(), sb);
            embeds.put(id, guildEmbed.toDocument());
            deviGuild.saveSettings();
            return;
        }
        //</editor-fold>
        //<editor-fold desc="delete <id>">
        if(args[0].equalsIgnoreCase("delete")) {
            if(args.length == 1) {
                MessageUtils.sendMessage(event.getChannel(), "Looks like you've missed some arguments. Correct usage: `!embed delete <id>`");
                return;
            }
            int idd; try { idd = Integer.parseInt(args[1]); }
            catch (NumberFormatException e){ idd = 99; }
            String id = String.valueOf(idd);

            if(!embeds.containsKey(id)) {
                MessageUtils.sendMessage(event.getChannel(), "There is no embed with the ID " + args[1] + ". Type ");
                return;
            }

            embeds.remove(id);
            deviGuild.saveSettings();
            MessageUtils.sendMessage(event.getChannel(), "Embed " + args[1] + " has been removed successfully");
            return;
        }
        //</editor-fold>
        //<editor-fold desc="preview <id>">
        if (args[0].equalsIgnoreCase("preview")) {
            if(args.length == 1) {
                MessageUtils.sendMessage(event.getChannel(), "Looks like you've missed some arguments. Correct usage: `!embed preview <id>`");
                return;
            }
            String id = args[1];
            if(!embeds.containsKey(id)) {
                MessageUtils.sendMessage(event.getChannel(), "There is no embed with the ID " + id + " registered on your guild. Use `!embeds` to get a list of all embed IDs");
                return;
            }

            GuildEmbed guildEmbed = deviGuild.createEmbed((Document) embeds.get(id));
            MessageUtils.sendMessage(event.getChannel(), guildEmbed.toEmbed());
        }
        //</editor-fold>
        //<editor-fold desc="set <id> <key> <value>">
        if (args[0].equalsIgnoreCase("set")) {
            if(args.length < 4) {
                MessageUtils.sendMessage(event.getChannel(), "Looks like you've missed some arguments. Correct usage: `!embed set <id> <key> <value>`");
                return;
            }

            String id = args[1];
            if(!embeds.containsKey(id)) {
                MessageUtils.sendMessage(event.getChannel(), "There is no embed with the ID " + id + " registered on your guild. Use `!embeds` to get a list of all embed IDs");
                return;
            }
            String key = args[2];
            if (!stringKeys.contains(key.toLowerCase()) && !integerKeys.contains(key.toLowerCase())) {
                MessageUtils.sendMessage(event.getChannel(), "`" + key + "` is not a valid. Available keys: `" + stringKeys.stream().collect(Collectors.joining("` | `")) + integerKeys.stream().collect(Collectors.joining("` | `")) + "`");
                return;
            }
            String value = Arrays.stream(args).skip(3).collect(Collectors.joining(" "));
            if (integerKeys.contains(key.toLowerCase())) {
                int intValue;
                try {
                    intValue = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    MessageUtils.sendMessage(event.getChannel(), "`" + args[4] + "` is not a number. This embed setting requires a number as its value");
                    return;
                }
                return;
            } else {

                return;
            }
        }
        //</editor-fold>
        MessageUtils.sendMessage(event.getChannel(), "work in progress");
        return;
    }


    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 57;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("embeds");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
