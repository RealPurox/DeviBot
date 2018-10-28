package me.purox.devi.core.guild.entities;

import com.google.gson.annotations.SerializedName;

public class Punishment {

    private String _id;
    private String guild;
    @SerializedName(value = "case")
    private int caseId;
    private long time;
    private String type;
    private String punisher;
    private String punished;
    private String reason;
    private String message;
    private String channel;

    public String getGuild() {
        return guild;
    }

    public int getCaseId() {
        return caseId;
    }

    public long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getPunisher() {
        return punisher;
    }

    public String getPunished() {
        return punished;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    public String getChannel() {
        return channel;
    }

    public String get_id() {
        return _id;
    }
}
