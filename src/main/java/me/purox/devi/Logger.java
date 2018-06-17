package me.purox.devi;

import me.purox.devi.core.Devi;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Logger {

    private List<String> logs;
    private boolean debug;
    private SimpleDateFormat dateFormat;
    private long lastPush;
    private Devi devi;

    public Logger(Devi devi) {
        this.devi = devi;
        this.logs = new ArrayList<>();
        this.debug = false;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        this.lastPush = System.currentTimeMillis();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            StringBuilder builder = new StringBuilder();

            for (String log : logs) {
                builder.append(log).append("\n");
            }

            logs.clear();

            new RequestBuilder(devi.getOkHttpClient()).setURL("https://hastebin.com/documents").setRequestType(Request.RequestType.POST)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setStringBody(builder.toString())
                    .build()
                    .asJSON(res -> {
                        Guild guild = devi.getShardManager().getGuildById("392264119102996480");
                        if (guild == null)return;
                        TextChannel channel = guild.getTextChannelById("422325680739713034");
                        if (channel == null)return;
                        MessageUtils.sendMessageAsync(channel, "Devi's logs [" + dateFormat.format(new Date(lastPush)) + "] - [" + dateFormat.format(new Date()) + "]: https://hastebin.com/" + res.getBody().getString("key") + ".txt");
                    });
        }, 2, 2, TimeUnit.HOURS);
    }

    private String getTime() {
        return "[" + dateFormat.format(new Date()) + "] ";
    }

    public void log(Object object) {
        log("INFO", object);
    }

    public void debug(Object object) {
        if (debug) log("DEBUG", object);
    }

    public void error(Object object) {
        log("ERROR", object);
    }

    public void warning(Object object) {
        log("WARNING", object);
    }

    public void wtf(Object object) {
        log("WHAT THE FUCK IS GOING ON", object);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void log(String prefix, Object object) {
        String log = getTime()  + "[" + prefix + "] " + object.toString();
        logs.add(log);
        System.out.println(log);
    }
}
