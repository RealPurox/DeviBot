package net.devibot.provider.commands;

import net.devibot.core.entities.DeviGuild;
import net.devibot.core.entities.User;
import net.devibot.provider.Provider;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Language;
import net.devibot.core.entities.ModuleType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ICommand {

    private String invoke;
    private List<String> aliases;

    private boolean guildOnly;
    private int descriptionId;
    private Permission permission;
    private ModuleType moduleType;

    public ICommand(String invoke, String ... aliases) {
        this.invoke = invoke;
        this.aliases = aliases == null ? new ArrayList<>() : Arrays.asList(aliases);
    }

    public ICommand setGuildOnly(boolean guildOnly) {
        this.guildOnly = guildOnly;
        return this;
    }

    public ICommand setDescriptionId(int descriptionId) {
        this.descriptionId = descriptionId;
        return this;
    }

    public ICommand setPermission(Permission permission) {
        this.permission = permission;
        return this;
    }

    public ICommand setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
        return this;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getInvoke() {
        return invoke;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public static class Command extends MessageReceivedEvent {

        private DeviGuild deviGuild;
        private Language language;
        private String prefix;
        private String invoke;
        private String raw;
        private String[] args;
        private DiscordBot discordBot;
        private User user;

        public Command(MessageReceivedEvent event, String prefix, DiscordBot discordBot) {
            super(event.getJDA(), event.getResponseNumber(), event.getMessage());

            this.discordBot = discordBot;

            String[] split = event.getMessage().getContentRaw().substring(prefix.length()).split(" ");
            List<String> list = new ArrayList<>(Arrays.asList(split));

            this.invoke = split[0];
            this.args = list.subList(1, list.size()).toArray(new String[0]);
            this.raw = event.getMessage().getContentRaw();
            this.prefix = prefix;
            this.deviGuild = event.getGuild() == null ? null : discordBot.getCacheManager().getDeviGuildCache().getDeviGuild(event.getGuild().getId());
            this.language = event.getGuild() == null ? Language.ENGLISH : Language.getLanguage(deviGuild.getLanguage());
            this.user = Provider.getInstance().getCacheManager().getUserCache().getUser(event.getAuthor().getId());
        }

        @Nullable
        public ICommand getICommand() {
            return discordBot.getCommandHandler().getCommands().get(invoke);
        }

        public DeviGuild getDeviGuild() {
            return deviGuild;
        }

        public Language getLanguage() {
            return language;
        }

        public String getPrefix() {
            return prefix;
        }

        public String[] getArgs() {
            return args;
        }

        public String getRaw() {
            return raw;
        }

        public User getUser() {
            return user;
        }
    }

    public abstract void execute(CommandSender sender, ICommand.Command command);
}
