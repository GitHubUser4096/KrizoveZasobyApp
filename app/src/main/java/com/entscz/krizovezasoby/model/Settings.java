package com.entscz.krizovezasoby.model;

public class Settings {

    public final String criticalValue;
    public final String criticalUnit;
    public final String warnValue;
    public final String warnUnit;
    public final String recommendedValue;
    public final String recommendedUnit;
    public final String dateFormat;
    public final boolean sendNotifs;

    public Settings(String criticalValue, String criticalUnit, String warnValue, String warnUnit, String recommendedValue, String recommendedUnit, String dateFormat, boolean sendNotifs) {
        this.criticalValue = criticalValue;
        this.criticalUnit = criticalUnit;
        this.warnValue = warnValue;
        this.warnUnit = warnUnit;
        this.recommendedValue = recommendedValue;
        this.recommendedUnit = recommendedUnit;
        this.dateFormat = dateFormat;
        this.sendNotifs = sendNotifs;
    }

}
