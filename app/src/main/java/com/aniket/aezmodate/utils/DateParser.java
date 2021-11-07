package com.aniket.aezmodate.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {
    private static final DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

    public static String convertDateToString(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(dateString);
        String strDate = "";
        assert date != null;
        strDate = dateFormat1.format(date);
        return strDate;
    }

    public static String formatDayTime(long ts2){
        Calendar calendar = Calendar.getInstance();
        return DateFormat.getDateInstance().format(calendar.getTime());
    }

    public static String getDateMsg(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm aa");
        String strDate = mdformat.format(calendar.getTime());
//        String currentTime = DateFormat.getTimeInstance().format(calendar.getTime());
        return strDate;
    }
}
