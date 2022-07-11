package com.entscz.krizovezasoby.model;

public class Product {

    public final int id;
    public final String brand;
    public final String type;
    public final int amountValue;
    public final String amountUnit;
    public final String shortDesc;
    public final String code;
    public final String imgName;
    public final String packageType;
    public final String description;

    public Product(int id, String brand, String type, int amountValue, String amountUnit, String shortDesc, String code, String imgName, String packageType, String description) {
        this.id = id;
        this.brand = brand;
        this.type = type;
        this.amountValue = amountValue;
        this.amountUnit = amountUnit;
        this.shortDesc = shortDesc;
        this.code = code;
        this.imgName = imgName;
        this.packageType = packageType;
        this.description = description;
    }

    public String getTitle(){
        String title = brand+" • "+type+
                (amountValue==-1 ? "" : " • "+amountValue+" "+amountUnit);
        return title;
    }

}
