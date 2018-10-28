package me.purox.devi.commandsold.dev;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.List;

public class EvalCommandExecutor implements CommandExecutor {

    private Devi devi;
    private ScriptEngine engine;

    public EvalCommandExecutor(Devi devi) {
        this.devi = devi;
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("var imports = new JavaImporter(" +
                    "java.io," +
                    "java.lang," +
                    "java.util," +
                    "Packages.me.purox.devi," +
                    "Packages.net.dv8tion.jda.core," +
                    "Packages.net.dv8tion.jda.core.entities," +
                    "Packages.net.dv8tion.jda.core.entities.impl," +
                    "Packages.net.dv8tion.jda.core.managers," +
                    "Packages.net.dv8tion.jda.core.managers.impl," +
                    "Packages.net.dv8tion.jda.core.utils);");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        String evaluation = String.join(" ", args);

        engine.put("args", args);
        engine.put("command", command);
        engine.put("sender", sender);
        engine.put("devi", devi);

        try {
            if (evaluation.contains("sender.reply(")) {
                engine.eval(evaluation);
            } else {
                Object out = engine.eval(
                        "(function() {" +
                                "with (imports) {" +
                                evaluation + "}" +
                                "})();");
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.GREEN);
                builder.appendDescription("Code was successfully executed without any errors\n");
                sender.reply(builder.build());
            }
        } catch (Exception e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.appendDescription("An exception was thrown:\n");
            builder.appendDescription("```\n");
            builder.appendDescription(e + "\n");
            builder.appendDescription("```");
            sender.reply(builder.build());
        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
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
        return ModuleType.DEV;
    }
}
