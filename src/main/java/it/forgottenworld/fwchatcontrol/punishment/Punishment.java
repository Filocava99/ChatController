package it.forgottenworld.fwchatcontrol.punishment;

public class Punishment {

    private final PunishmentType type;
    private final int duration;

    public Punishment(PunishmentType type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public PunishmentType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }
}
