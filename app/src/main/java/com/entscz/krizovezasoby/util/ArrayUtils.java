package com.entscz.krizovezasoby.util;

import java.util.ArrayList;

public class ArrayUtils {

    public static String join(ArrayList<String> list, String delimiter){

        String res = "";

        for(int i = 0; i<list.size(); i++){
            res += list.get(i);
            if(i<list.size()-1) res += delimiter;
        }

        return res;

    }

}
