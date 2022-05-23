package com.entscz.krizovezasoby.util;

import java.util.Locale;

public class StringUtils {

    public static String toFirstUpper(String str){
        if(str.length()==0) return str;
        return str.substring(0, 1).toUpperCase(Locale.ROOT)+str.substring(1);
    }

}
