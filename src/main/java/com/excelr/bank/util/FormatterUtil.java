package com.excelr.bank.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatterUtil {

    public static LocalDateTime formatData(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
       return LocalDateTime.parse(date, formatter);

    }


}
