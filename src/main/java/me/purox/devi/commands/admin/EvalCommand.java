package me.purox.devi.commands.admin;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;

public class EvalCommand extends ICommand {

    private Devi devi;
    private ScriptEngine engine;

    public EvalCommand(Devi devi) {
        super("eval");
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
    public void execute(CommandSender sender, Command command) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        String evaluation = String.join(" ", command.getArgs());

        engine.put("args", command.getArgs());
        engine.put("command", command);
        engine.put("sender", sender);
        engine.put("devi", devi);
        engine.put("icommand", this);

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
}
