package lam.project.foureventuserdroid.utils;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by spino on 24/08/16.
 */

public class DateConverter {

    private static final String FORMATTER = "dd/MM - HH:mm";

    private static final String DATE_FORMATTER_V2 = "dd MMM yyyy";
    private static final String TIME_FORMATTER_V2 = "HH:mm";
    private static final String DATETIME_FORMATTER_V2 = "dd MMM yyyy - HH:mm";
    private static final String DIVIDER = " - ";

    public static String fromMillis(long millis) {

        SimpleDateFormat formatter = new SimpleDateFormat(FORMATTER,Locale.ITALY);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    public static String dateFromCalendar(Calendar calendar) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER_V2,Locale.ITALY);
        return dateFormat.format(calendar.getTime());
    }

    public static String dateFromMillis(long millis) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMATTER_V2,Locale.ITALY);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }



    public static String dateToMillis(String date) throws ParseException{

        Date parsedDate = new SimpleDateFormat(DATE_FORMATTER_V2,Locale.ITALY).parse(date);

        return Long.toString(parsedDate.getTime());
    }

    private static String toMillisComplete(String date) throws ParseException{

        Date parsedDate = new SimpleDateFormat(FORMATTER,Locale.ITALY).parse(date);

        return Long.toString(parsedDate.getTime());
    }

    public static String getTime(String start,String end){

        String time = "";

        time += start.split(DIVIDER)[1];

        if(end != null) {

            time += " - " + end.split(DIVIDER)[1];
        }

        return time;
    }

    public static String getDate(String start,String end){

        String startDate = start.split(DIVIDER)[0];
        String endDate = (end == null) ? null : end.split(DIVIDER)[0];
        String time = "";

        time += startDate;

        if(end != null && !startDate.equals(endDate)) {

            time = "Dal " + time + " al " + endDate;
        }

        return time;
    }
    public static long addYear(String date) {
        GregorianCalendar cal = new GregorianCalendar();
        try {
            return Long.valueOf(toMillisComplete(date.split(" - ")[0] + "/" + cal.get(Calendar.YEAR) + " - "+ date.split(" - ")[1]));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }
}
