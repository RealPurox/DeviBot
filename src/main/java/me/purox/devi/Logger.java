package me.purox.devi;

import me.purox.devi.core.Devi;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Logger {

    private List<Document> logs;
    private boolean debug;
    private SimpleDateFormat dateFormat;
    private Devi devi;

    public Logger(Devi devi) {
        this.devi = devi;
        this.logs = new ArrayList<>();
        this.debug = true;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::pushLogs, 1, 1, TimeUnit.MINUTES);
    }

    private void pushLogs() {
        if (devi.getSettings().isDevBot()) return;
        if (logs.size() != 0) devi.getDatabaseManager().getClient().getDatabase("website").getCollection("devi_logs").insertMany(logs);
        logs.clear();
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

        Document logDoc = new Document();
        logDoc.put("bot", devi.getSettings().isDevBot() ? "DEV" : "PUBLIC");
        logDoc.put("type", prefix.toLowerCase());
        logDoc.put("time", System.currentTimeMillis());
        logDoc.put("message", object.toString());

        logs.add(logDoc);
        System.out.println(log);
    }
}