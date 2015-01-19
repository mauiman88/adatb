package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mate on 2015.01.19..
 */
public class DateUtils {
    public static String formatHUYear(Date date) {
        if(date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } else {
            return null;
        }
    }

    public static String formatHUTime(Date date) {
        if(date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        } else {
            return null;
        }
    }

    public static Date formatToDateFromYear(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public static Date formatToDateFromTime(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
