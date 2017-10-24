package gr.ictpro.jsalatas.agendawidget.model.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;

import java.util.*;

public class Calendars {
    private static List<Calendar> calendarList;

    public static void refreshCalendarList() {
        if (!checkPermissions()) {
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

    public static List<CalendarEvent> getEvents(int appWidgetId) {
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        if (!checkPermissions()) {
            return calendarEvents;
        }

        refreshCalendarList();

        String[] calendarsList = Settings.getStringPref(AgendaWidgetApplication.getContext(), "calendars", appWidgetId).split("@@@");
        StringBuilder sb = new StringBuilder();
        for (String calendar: calendarsList) {
            if (!sb.toString().isEmpty()) {
                sb.append(" OR ");
            }
            sb.append(CalendarContract.Events.CALENDAR_ID).append(" = ").append(calendar);
        }

        String selectedAccountsFilter = sb.toString();

        java.util.Calendar calendarInstance = GregorianCalendar.getInstance();
        Date selectedRangeStart = calendarInstance.getTime();
        Long searchPeriod = Settings.getLongPref(AgendaWidgetApplication.getContext(), "searchPeriod", appWidgetId);
        calendarInstance.setTimeInMillis(selectedRangeStart.getTime() + searchPeriod);
        Date selectedRangeEnd = calendarInstance.getTime();

        sb = new StringBuilder();
        sb.append("(((").append(CalendarContract.Events.DTSTART).append(" >= ").append(selectedRangeStart.getTime()).append( ") AND ");
        sb.append("(").append(CalendarContract.Events.DTSTART).append(" <= ").append(selectedRangeEnd.getTime()).append(")) OR ");
        sb.append("((").append(CalendarContract.Events.DTEND).append(" >= ").append(selectedRangeStart.getTime()).append(") AND ");
        sb.append("(").append(CalendarContract.Events.DTEND).append(" <= ").append(selectedRangeEnd.getTime()).append(")))");

// Debug
//        Log.d("Sync", ">>>>>>>>> sync events from " + CalendarEvent.df.format(selectedRangeStart) + " to " + CalendarEvent.df.format(selectedRangeEnd));

        ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();

        final String[] PROJECTION = new String[]{
                CalendarContract.Instances._ID,
                CalendarContract.Instances.CALENDAR_COLOR,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.EVENT_LOCATION,
                CalendarContract.Instances.DESCRIPTION,
                CalendarContract.Instances.EVENT_TIMEZONE,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.EVENT_END_TIMEZONE,
                CalendarContract.Instances.END,
                CalendarContract.Instances.ALL_DAY,
        };

        long id;
        @ColorInt int color;
        String title;
        String location;
        String description;
        Date startDate;
        Date endDate;
        boolean allDay;

        String selection = "(" + selectedAccountsFilter + ")";

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, selectedRangeStart.getTime());
        ContentUris.appendId(builder, selectedRangeEnd.getTime());

        Cursor cur = cr.query(builder.build(), PROJECTION, selection, null, null);
        TimeZone tzLocal = TimeZone.getDefault();

        while (cur.moveToNext()) {
            id = cur.getLong(0);
            color = cur.getInt(1);
            title = cur.getString(2);
            location = cur.getString(3);
            description = cur.getString(4);
            allDay = cur.getInt(9) == 1;
            calendarInstance.setTimeInMillis(cur.getLong(6));
            startDate = calendarInstance.getTime();
            calendarInstance.setTimeInMillis(cur.getLong(8));
            endDate = calendarInstance.getTime();

            // Assume current time zone for all day events
            if(allDay) {
                calendarInstance.setTimeInMillis(startDate.getTime() - tzLocal.getOffset(startDate.getTime()));
                startDate = calendarInstance.getTime();
                calendarInstance.setTimeInMillis(endDate.getTime() - tzLocal.getOffset(endDate.getTime()));
                endDate = calendarInstance.getTime();
            }

            CalendarEvent e = new CalendarEvent(id, color, title, location, description, startDate, endDate, allDay);
            if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "repeatMultidayEvents", appWidgetId) && e.isMultiDay()) {
                calendarEvents.addAll(e.getMultidayEventsList(selectedRangeEnd));
            } else {
                calendarEvents.add(e);
            }
        }
        cur.close();

        Collections.sort(calendarEvents);
// Debug
//        for (CalendarEvent c : calendarEvents) {
//            Log.d("Sync", "    >>>>> " + c.toString(selectedRangeStart, selectedRangeEnd));
//        }
        return calendarEvents;
    }


}
