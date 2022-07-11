package com.entscz.krizovezasoby.model;

public class CharityPlace {

    public final String charityName;
    public final String street;
    public final String place;
    public final String postCode;
    public final String openHours;
    public final String contacts;

    public CharityPlace(String charityName, String street, String place, String postCode, String openHours, String contacts) {
        this.charityName = charityName;
        this.street = street;
        this.place = place;
        this.postCode = postCode;
        this.openHours = openHours;
        this.contacts = contacts;
    }

    public String getAddress(){
        return street+", "+place+", "+postCode;
    }

}
