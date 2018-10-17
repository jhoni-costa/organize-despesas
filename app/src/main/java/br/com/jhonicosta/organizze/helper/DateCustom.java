package br.com.jhonicosta.organizze.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCustom {

    public static String dataAtual() {
        long date = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }

    public static String dataFirebase(String data){
        String[] split = data.split("/");
        return split[1]+split[2];
    }
}
