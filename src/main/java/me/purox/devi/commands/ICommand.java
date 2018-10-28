package me.purox.devi.commands;

import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
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

    public ICommand(String invoke, String... aliases) {
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

    public ICommand setPremission(Permission permission) {
        this.permission = permission;
        return this;
    }

    public ICommand setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
        return this;
    }

    public String getInvoke() {
        return invoke;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public Permission getPermission() {
        return permission;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public static class Command extends MessageReceivedEvent {

        private DeviGuild deviGuild;
        private Language language;
        private String prefix;
        private String invoke;
        private String raw;
        private String[] args;

        public Command(MessageReceivedEvent event, String prefix, Devi devi) {
            super(event.getJDA(), event.getResponseNumber(), event.getMessage());

            String[] split = event.getMessage().getContentRaw().substring(prefix.length()).split(" ");
            List<String> list = new ArrayList<>(Arrays.asList(split));

            this.invoke = split[0];
            this.args = list.subList(1, list.size()).toArray(new String[0]);
            this.raw = event.getMessage().getContentRaw();
            this.prefix = prefix;

            this.deviGuild = devi.getDeviGuild(event.getGuild().getId());
            this.language = Language.getLanguage(this.deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        }

        @Nullable
        public ICommand getICommand() {
            return deviGuild.getDevi().getCommandHandler().getCommands().get(invoke);
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
    }

    public abstract void execute(CommandSender sender, ICommand.Command command);
}
