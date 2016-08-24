package lam.project.foureventuserdroid.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by spino on 24/08/16.
 */

public class DateConverter {

    private static final String FORMATTER = "dd/MM - hh:mm";

    public static String fromMillis(long millis) {

        SimpleDateFormat formatter = new SimpleDateFormat(FORMATTER, Locale.ITALY);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    public static String toMillis(String date) throws ParseException{

        Date parsedDate = new SimpleDateFormat(FORMATTER,Locale.ITALY).parse(date);

        return Long.toString(parsedDate.getTime());
    }

    public static String getTime(String start,String end){

        String time = "";

        time += start.split(" - ")[1];

        if(end != null) {

            time += " - " + end.split(" - ")[1];
        }

        return time;
    }

    public static String getDate(String start,String end){

        String time = "";

        time += start.split(" - ")[0];

        if(end != null) {

            time += " - " + end.split(" - ")[0];
        }

        return time;
    }
}
