package it.forgottenworld.fwchatcontrol;

import java.util.*;
import java.util.regex.Pattern;

public class Settings {

    private List<Pattern> regexes = new ArrayList<>();
    private int maxMessagesPerSecond = Integer.MAX_VALUE;
    private boolean warnIfFlooding = false;
    private int maxCapsCharPercentage = 0;
    private boolean warnIfCapsing = false;
    private boolean warnIfUsingBannedWords = true;
    private int maxWarnsBeforeMute = 3;
    private long muteDurationInSeconds = 32400;

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

    public boolean isWarnIfCapsing() {
        return warnIfCapsing;
    }

    public void setWarnIfCapsing(boolean warnIfCapsing) {
        this.warnIfCapsing = warnIfCapsing;
    }

    public boolean isWarnIfUsingBannedWords() {
        return warnIfUsingBannedWords;
    }

    public void setWarnIfUsingBannedWords(boolean warnIfUsingBannedWords) {
        this.warnIfUsingBannedWords = warnIfUsingBannedWords;
    }

    public int getMaxWarnsBeforeMute() {
        return maxWarnsBeforeMute;
    }

    public void setMaxWarnsBeforeMute(int maxWarnsBeforeMute) {
        this.maxWarnsBeforeMute = maxWarnsBeforeMute;
    }

    public long getMuteDurationInSeconds() {
        return muteDurationInSeconds;
    }

    public void setMuteDurationInSeconds(long muteDurationInSeconds) {
        this.muteDurationInSeconds = muteDurationInSeconds;
    }
}
