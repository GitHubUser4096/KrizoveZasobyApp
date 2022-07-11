package com.entscz.krizovezasoby.model;

public class Bag {

    public enum State {
        EMPTY, RECOMMENDED, WARN, CRITICAL, EXPIRED, OK
    };

    public final int id;
    public final String name;
    public final State state;

    public Bag(int id, String name, String state){
        this.id = id;
        this.name = name;
        this.state = State.valueOf(state.toUpperCase());
    }

    @Override
    public String toString() {
        return name;
    }
}
