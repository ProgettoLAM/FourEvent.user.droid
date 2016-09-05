package lam.project.foureventuserdroid.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Convertitore della data e ora, da millisecondi a stringa e viceversa
 */
public class DateConverter {

    private static final String FORMATTER = "dd/MM - HH:mm";
    private static final String DATE_FORMATTER_V2 = "dd MMM yyyy";
    private static final String DATETIME_FORMATTER_V2 = "dd MMM yyyy - HH:mm";
    private static final String DIVIDER = " - ";

    /**
     * Convertitore da millisecondi a stringa (versione 1)
     * @param millis millisecondi in long
     * @return stringa della data dal calendario
     */
    public static String fromMillis(long millis) {

        SimpleDateFormat formatter = new SimpleDateFormat(FORMATTER,Locale.ITALY);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    /**
     * Si prende la data dal calendario e si formatta nella versione 2
     * @param calendar data
     * @return stringa formattata
     */
    public static String dateFromCalendar(Calendar calendar) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER_V2,Locale.ITALY);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Convertitore da millisecondi a stringa (versione 2)
     * @param millis millisecondi in long
     * @return stringa della data dal calendario
     */
    public static String dateFromMillis(long millis) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMATTER_V2,Locale.ITALY);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    /**
     * Convertitore della data da stringa a millisecondi (versione 2)
     * @param date data
     * @return long convertito in stringa
     * @throws ParseException
     */
    public static String dateToMillis(String date) throws ParseException{

        Date parsedDate = new SimpleDateFormat(DATE_FORMATTER_V2,Locale.ITALY).parse(date);

        return Long.toString(parsedDate.getTime());
    }

    /**
     * Convertitore della data da stringa a millisecondi (versione 1)
     * @param date data
     * @return long convertito in stringa
     * @throws ParseException
     */
    private static String toMillisComplete(String date) throws ParseException{

        Date parsedDate = new SimpleDateFormat(FORMATTER,Locale.ITALY).parse(date);

        return Long.toString(parsedDate.getTime());
    }

    /**
     * Ora completa
     * @param start ora di inizio
     * @param end ora di fine
     * @return orario completo in stringa
     */
    public static String getTime(String start,String end){

        String time = "";

        time += start.split(DIVIDER)[1];

        if(end != null) {

            time += " - " + end.split(DIVIDER)[1];
        }

        return time;
    }

    /**
     * Data completa
     * @param start data di inizio
     * @param end data di fine
     * @return data completa in stringa
     */
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

    /**
     * Data completa formattata
     * @param date data
     * @return stringa convertita in long
     */
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
