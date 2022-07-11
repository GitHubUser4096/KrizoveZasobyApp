package com.entscz.krizovezasoby;

public class TestUtils {

    public static String randStr(int length){

        StringBuilder str = new StringBuilder();

        for(int i = 0; i<length; i++){
            char ch = (char)((int)(Math.random()*('z'-'a'))+'a');
            str.append(ch);
        }

        return str.toString();

    }

}
