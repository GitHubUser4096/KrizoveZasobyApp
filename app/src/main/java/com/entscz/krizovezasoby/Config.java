package com.entscz.krizovezasoby;

public class Config {

    private static final Config NGGCV = new Config("https://zasoby.nggcv.cz");
    private static final Config ONLINE_TEST = new Config("https://zasoby-test.nggcv.cz");

    private static Config config = NGGCV;

    public static Config get(){
        return config;
    }

    public final String url;

    public Config(String url){
        this.url = url;
    }

}
