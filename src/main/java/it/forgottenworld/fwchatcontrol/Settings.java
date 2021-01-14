package it.forgottenworld.fwchatcontrol;

import it.forgottenworld.fwchatcontrol.punishment.Punishment;
import it.forgottenworld.fwchatcontrol.punishment.PunishmentType;

import java.util.*;
import java.util.regex.Pattern;

public class Settings {

    private List<Pattern> regexes = new ArrayList<>();
    private int maxMessagesPerSecond = Integer.MAX_VALUE;
    private boolean warnIfFlooding = false;
    private int maxCapsCharPercentage = 0;
    private boolean warnIfUsingCaps = false;
    private boolean warnIfUsingBannedWords = true;
    private boolean capitalizeFirstLetter = false;
    private final Map<Integer, Punishment> punishments = new HashMap<>();

    public List<Pattern> getRegexes() {
        return regexes;
    }

    public void setRegexes(List<Pattern> regexes) {
        this.regexes = regexes;
    }

    public int getMaxCapsCharPercentage() {
        return maxCapsCharPercentage;
    }

    public void setMaxCapsCharPercentage(int maxCapsCharPercentage) {
        this.maxCapsCharPercentage = maxCapsCharPercentage;
    }

    public int getMaxMessagesPerSecond() {
        return maxMessagesPerSecond;
    }

    public void setMaxMessagesPerSecond(int maxMessagesPerSecond) {
        this.maxMessagesPerSecond = maxMessagesPerSecond;
    }

    public boolean isWarnIfFlooding() {
        return warnIfFlooding;
    }

    public void setWarnIfFlooding(boolean warnIfFlooding) {
        this.warnIfFlooding = warnIfFlooding;
    }

    public boolean isWarnIfUsingCaps() {
        return warnIfUsingCaps;
    }

    public void setWarnIfUsingCaps(boolean warnIfUsingCaps) {
        this.warnIfUsingCaps = warnIfUsingCaps;
    }

    public boolean isWarnIfUsingBannedWords() {
        return warnIfUsingBannedWords;
    }

    public void setWarnIfUsingBannedWords(boolean warnIfUsingBannedWords) {
        this.warnIfUsingBannedWords = warnIfUsingBannedWords;
    }

    public boolean isCapitalizeFirstLetter() {
        return capitalizeFirstLetter;
    }

    public void setCapitalizeFirstLetter(boolean capitalizeFirstLetter) {
        this.capitalizeFirstLetter = capitalizeFirstLetter;
    }

    public Map<Integer, Punishment> getPunishments() {
        return Collections.unmodifiableMap(punishments);
    }

    public void addPunishment(int warns, Punishment punishment){
        punishments.put(warns, punishment);
    }

    public Punishment removePunishment(int warns){
        return punishments.remove(warns);
    }
}
