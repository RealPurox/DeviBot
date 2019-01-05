package net.devibot.core.entities;

import com.google.gson.annotations.SerializedName;

public class DeviGuild {

    private String id;

    public DeviGuild(String id) {
        this.id = id;
    }

    public DeviGuild(net.devibot.grpc.entities.DeviGuild entity) {
        this.setId(entity.getId());
        this.setPrefix(entity.getPrefix());
        this.setLanguage(entity.getLanguage());
        this.setMuteRole(entity.getMuteRole());
        this.setModLogEnabled(entity.getModLogEnabled());
        this.setModLogChannel(entity.getModLogChannel());
        this.setModLogMutes(entity.getModLogMutes());
        this.setModLogBans(entity.getModLogBans());
        this.setModLogKicks(entity.getModLogKicks());
        this.setModLogVoiceKicks(entity.getModLogVoiceKicks());
        this.setModLogMessageEdited(entity.getModLogMessageEdited());
        this.setModLogMessageDeleted(entity.getModLogMessageDeleted());
        this.setAutoModEnabled(entity.getAutoModEnabled());
        this.setAutoModAntiAds(entity.getAutoModAntiAds());
        this.setAutoModAntiCaps(entity.getAutoModAntiCaps());
        this.setAutoModAntiEmojiSpam(entity.getAutoModAntiEmojiSpam());
    }

    private String prefix = "!";
    private String language = "ENGLISH";

    @SerializedName("mute_role")
    private String muteRole = "-1";
    @SerializedName("mod_log_enabled")
    private boolean modLogEnabled = false;
    @SerializedName("mod_log_channel")
    private String modLogChannel = "-1";
    @SerializedName("mod_log_mutes")
    private boolean modLogMutes = true;
    @SerializedName("mod_log_bans")
    private boolean modLogBans = true;
    @SerializedName("mod_log_kicks")
    private boolean modLogKicks = true;
    @SerializedName("mod_log_voice_kicks")
    private boolean modLogVoiceKicks = true;
    @SerializedName("mod_log_message_edited")
    private boolean modLogMessageEdited= true;
    @SerializedName("mod_log_message_deleted")
    private boolean modLogMessageDeleted = true;

    @SerializedName("auto_mod_enabled")
    private boolean autoModEnabled = false;
    @SerializedName("auto_mod_anti_ads")
    private boolean autoModAntiAds = true;
    @SerializedName("auto_mod_anti_caps")
    private boolean autoModAntiCaps = true;
    @SerializedName("auto_mod_anti_emoji_spam")
    private boolean autoModAntiEmojiSpam = true;

    /*
     * ============ METHODS ============
     */

    public net.devibot.grpc.entities.DeviGuild toGrpc() {
        return net.devibot.grpc.entities.DeviGuild.newBuilder()
                .setId(this.id)
                .setPrefix(this.prefix)
                .setLanguage(this.language)
                .setMuteRole(this.muteRole)
                .setModLogEnabled(this.modLogEnabled)
                .setModLogChannel(this.modLogChannel)
                .setModLogMutes(this.modLogMutes)
                .setModLogBans(this.modLogBans)
                .setModLogKicks(this.modLogKicks)
                .setModLogVoiceKicks(this.modLogVoiceKicks)
                .setModLogMessageDeleted(this.modLogMessageDeleted)
                .setModLogMessageEdited(this.modLogMessageEdited)
                .setAutoModEnabled(this.autoModEnabled)
                .setAutoModAntiAds(this.autoModAntiAds)
                .setAutoModAntiCaps(this.autoModAntiCaps)
                .setAutoModAntiEmojiSpam(this.autoModAntiEmojiSpam)
                .build();
    }


    /*
     * ============ GETTER ============
     */

    public String getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getLanguage() {
        return language;
    }

    public String getMuteRole() {
        return muteRole;
    }

    public boolean isModLogEnabled() {
        return modLogEnabled;
    }

    public String getModLogChannel() {
        return modLogChannel;
    }

    public boolean isModLogMutes() {
        return modLogMutes;
    }

    public boolean isModLogBans() {
        return modLogBans;
    }

    public boolean isModLogKicks() {
        return modLogKicks;
    }

    public boolean isModLogVoiceKicks() {
        return modLogVoiceKicks;
    }

    public boolean isModLogMessageEdited() {
        return modLogMessageEdited;
    }

    public boolean isModLogMessageDeleted() {
        return modLogMessageDeleted;
    }

    public boolean isAutoModEnabled() {
        return autoModEnabled;
    }

    public boolean isAutoModAntiAds() {
        return autoModAntiAds;
    }

    public boolean isAutoModAntiCaps() {
        return autoModAntiCaps;
    }

    public boolean isAutoModAntiEmojiSpam() {
        return autoModAntiEmojiSpam;
    }

    /*
     * ============ SETTER ============
     */

    public void setId(String id) {
        this.id = id;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setMuteRole(String muteRole) {
        this.muteRole = muteRole;
    }

    public void setModLogEnabled(boolean modLogEnabled) {
        this.modLogEnabled = modLogEnabled;
    }

    public void setModLogChannel(String modLogChannel) {
        this.modLogChannel = modLogChannel;
    }

    public void setModLogMutes(boolean modLogMutes) {
        this.modLogMutes = modLogMutes;
    }

    public void setModLogBans(boolean modLogBans) {
        this.modLogBans = modLogBans;
    }

    public void setModLogKicks(boolean modLogKicks) {
        this.modLogKicks = modLogKicks;
    }

    public void setModLogVoiceKicks(boolean modLogVoiceKicks) {
        this.modLogVoiceKicks = modLogVoiceKicks;
    }

    public void setModLogMessageEdited(boolean modLogMessageEdited) {
        this.modLogMessageEdited = modLogMessageEdited;
    }

    public void setModLogMessageDeleted(boolean modLogMessageDeleted) {
        this.modLogMessageDeleted = modLogMessageDeleted;
    }

    public void setAutoModEnabled(boolean autoModEnabled) {
        this.autoModEnabled = autoModEnabled;
    }

    public void setAutoModAntiAds(boolean autoModAntiAds) {
        this.autoModAntiAds = autoModAntiAds;
    }

    public void setAutoModAntiCaps(boolean autoModAntiCaps) {
        this.autoModAntiCaps = autoModAntiCaps;
    }

    public void setAutoModAntiEmojiSpam(boolean autoModAntiEmojiSpam) {
        this.autoModAntiEmojiSpam = autoModAntiEmojiSpam;
    }
}
