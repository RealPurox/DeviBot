package net.devibot.provider.commands.dev;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.ISimpleCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class EvalCommand extends ICommand {

    private static Logger logger = LoggerFactory.getLogger(EvalCommand.class);

    private DiscordBot discordBot;

    public EvalCommand(DiscordBot discordBot) {
        super("eval");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, ICommand.Command command) {
        List<Thread> threadList = new ArrayList<>();

        ExecutorService singleThreadPool = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            threadList.add(t);
            return t;
        });

        Future<Object> future = null;

        try {

            AtomicBoolean failure = new AtomicBoolean(false);

            Runnable runnable = () -> {
                try {
                    evaluate(command.getMessage().getContentRaw().substring(command.getPrefix().length() + "eval".length()), sender, command);
                } catch (Exception e) {
                    failure.set(true);

                    if (!(e.getCause() instanceof ThreadDeath))
                        sender.errorMessage().append("Evaluation threw an exception: `").append(e.toString()).append("`").execute();
                }
            };

            future = singleThreadPool.submit(runnable, Object.class);
            future.get(5, TimeUnit.SECONDS);

            if (failure.get()) return;
            if (!command.getMessage().getContentRaw().toLowerCase().contains("sender.reply("))
                sender.successMessage().append("Evaluation executed successfully").execute();

        } catch (Exception e) {
            if (e instanceof TimeoutException) {
                sender.errorMessage().append("Evaluation took more than 5 seconds to be executed and was therefore cancelled.").execute();
            } else {
                sender.errorMessage().append("Evaluation threw an exception: `").append(e.getCause().toString()).append("`").execute();
            }
            //noinspection ConstantConditions
            future.cancel(true);
            singleThreadPool.shutdownNow();
            threadList.forEach(Thread::stop);
        }
    }

    private static void evaluate(String source, CommandSender sender, Command command) throws Exception {
        ISimpleCompiler compiler = CompilerFactoryFactory.getDefaultCompilerFactory().newSimpleCompiler();
        compiler.cook(createDummyClassSource(source));
        evaluateDummyClassMethod(sender, command, compiler.getClassLoader());
    }

    private static String createDummyClassSource(String source) {
        return  "import net.devibot.core.agents.*;\n" +
                "import net.devibot.core.database.*;\n" +
                "import net.devibot.core.entities.*;\n" +
                "import net.devibot.core.request.*;\n" +
                "import net.devibot.core.utils.*;\n" +
                "import net.devibot.core.*;\n" +
                "import net.devibot.provider.agents.*;\n" +
                "import net.devibot.provider.cache.*;\n" +
                "import net.devibot.provider.commands.*;\n" +
                "import net.devibot.provider.core.*;\n" +
                "import net.devibot.provider.entities.*;\n" +
                "import net.devibot.provider.listener.*;\n" +
                "import net.devibot.provider.manager.*;\n" +
                "import net.devibot.provider.utils.*;\n" +
                "import java.io.*;\n" +
                "import java.lang.*;\n" +
                "import java.util.*;\n" +
                "import java.util.concurrent.*;\n" +
                "import net.dv8tion.jda.core.*;\n" +
                "import net.dv8tion.jda.core.entities.*;\n" +
                "import net.dv8tion.jda.core.entities.impl.*;\n" +
                "import net.dv8tion.jda.core.managers.*;\n" +
                "import net.dv8tion.jda.core.managers.impl.*;\n" +
                "import net.dv8tion.jda.core.utils.*;\n" +
                "import java.util.regex.*;\n" +
                "import java.awt.*;\n" +
                "\n" +
                "class DummyEvalClass {\n" +
                "   public static void eval(CommandSender sender, ICommand.Command command) {\n" +
                "       " + source + "\n" +
                "   }\n" +
                "}\n";
    }

    private static void evaluateDummyClassMethod(CommandSender sender, Command command, final ClassLoader classLoader) throws Exception {
        final Class<?> dummy = classLoader.loadClass("DummyEvalClass");
        final Method eval = dummy.getDeclaredMethod("eval", CommandSender.class, ICommand.Command.class);
        eval.setAccessible(true);
        eval.invoke(null, sender, command);
    }
}
