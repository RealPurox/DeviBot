package me.purox.devi.punishments.options;

public class BanOptions implements Options {

    private int days;

    public BanOptions setDays(int days) {
        this.days = days;
        return this;
    }

    public int getDays() {
        return days;
    }

    @Override
    public String toString() {
        return "Days: " + days;
    }
}
