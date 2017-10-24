package gr.ictpro.jsalatas.agendawidget.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    public static int daysBetween(Date d1, Date d2) {
        if (d1.compareTo(d2) > 0) {
            Date tmp = d1;
            d1 = d2;
            d2 = tmp;
        }

        return Math.round((d2.getTime() - d1.getTime()) / (1000L * 60 * 60 * 24));
    }


    public static Date dayFloor(Date d) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(d.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date dayCeil(Date d) {

        return nextDay(dayFloor(d));
    }

    public static Date nextDay(Date d) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(d.getTime());
        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }

    public static boolean isInSameDay(Date d1, Date d2) {
        Calendar d1Calendar = GregorianCalendar.getInstance();
        d1Calendar.setTimeInMillis(dayFloor(d1).getTime());
        Calendar d2Calendar = GregorianCalendar.getInstance();
        d2Calendar.setTimeInMillis(dayFloor(d2).getTime());

        return d1Calendar.get(Calendar.YEAR) == d2Calendar.get(Calendar.YEAR) &&
                d1Calendar.get(Calendar.MONTH) == d2Calendar.get(Calendar.MONTH) &&
                d1Calendar.get(Calendar.DAY_OF_MONTH) == d2Calendar.get(Calendar.DAY_OF_MONTH);

    }

    public static boolean isAllDay(Date d1, Date d2) {
        if (d1.compareTo(d2) > 0) {
            Date tmp = d1;
            d1 = d2;
            d2 = tmp;
        }
        Calendar d1Calendar = GregorianCalendar.getInstance();
        d1Calendar.setTimeInMillis(d1.getTime());

        return (d2.getTime() - d1.getTime() == 1000 * 60 * 60 * 24) &&
                d1Calendar.get(Calendar.HOUR_OF_DAY) == 0 &&
                d1Calendar.get(Calendar.MINUTE) == 0 &&
                d1Calendar.get(Calendar.SECOND) == 0;
    }
}
