package com.entscz.krizovezasoby.util;

public class Timer {

    public static void sleep(int timeMS){
//        try {
//            Thread.sleep(timeMS);
//        } catch(Exception e){}
        long i = 0;
        while(i<timeMS){
            i++;
            try {
                Thread.sleep(1);
            } catch(Exception e){}
        }
    }

}
