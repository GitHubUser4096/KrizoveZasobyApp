package com.entscz.krizovezasoby.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class StreamReader {

    public static String readStream(InputStream is){

        try {

            ArrayList<Byte> buffer = new ArrayList<>();

            int i;
            while ((i = is.read()) >= 0) {
                buffer.add((byte)i);
            }

            byte[] bytes = new byte[buffer.size()];
            for(int j = 0; j<bytes.length; j++){
                bytes[j] = buffer.get(j);
            }

            return new String(bytes, StandardCharsets.UTF_8);

        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }

}
