package com.entscz.krizovezasoby.model;

public class Item {

    public final int id;
    public final Product product;
    public final int count;
    public final String expiration;
    public final String displayDate;
    public final String useIn;
    public final boolean used;
    public final String state;

    public Item(int id, Product product, int count, String expiration, String displayDate, String useIn, boolean used, String state) {
        this.id = id;
        this.product = product;
        this.count = count;
        this.expiration = expiration;
        this.displayDate = displayDate;
        this.useIn = useIn;
        this.used = used;
        this.state = state;
    }

}
