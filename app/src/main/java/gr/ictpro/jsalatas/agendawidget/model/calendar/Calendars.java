package gr.ictpro.jsalatas.agendawidget.model.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;

import java.util.ArrayList;
import java.util.List;

public class Calendars {
    private static List<Calendar> calendarList;

    public static void refreshCalendarList() {
        if(!checkPermissions()) {
            return;
        }
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };

        final ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Cursor cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
        final List<Calendar> result = new ArrayList<>();

        while (cur.moveToNext()) {
            Long id = cur.getLong(0);
            String accountName = cur.getString(1);
            String name = cur.getString(2);
            int color = cur.getInt(3);

            Calendar c = new Calendar(id, accountName, name, color);
            result.add(c);
        }
        cur.close();
        calendarList = result;
    }

    public static List<Calendar> getCalendarList() {
        return calendarList;
    }

    private static boolean checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(AgendaWidgetApplication.getContext(), Manifest.permission.READ_CALENDAR);
        return permissionCheck != PackageManager.PERMISSION_DENIED;
    }


}
