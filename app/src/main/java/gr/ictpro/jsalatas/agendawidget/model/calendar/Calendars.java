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
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.Events;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.utils.DateUtils;

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

    public static List<EventItem> getEvents(int appWidgetId) {
        List<EventItem> calendarEvents = new ArrayList<>();
        if (!checkPermissions()) {
            return calendarEvents;
        }

        refreshCalendarList();

        String[] calendarsList = Settings.getStringPref(AgendaWidgetApplication.getContext(), "calendars", appWidgetId).split("@@@");
        StringBuilder sb = new StringBuilder();
        for (String calendar : calendarsList) {
            if (!sb.toString().isEmpty()) {
                sb.append(" OR ");
            }
            sb.append(CalendarContract.Events.CALENDAR_ID).append(" = ").append(calendar);
        }

        String selectedAccountsFilter = sb.toString();

        TimeZone tzLocal = TimeZone.getDefault();

        java.util.Calendar calendarInstance = GregorianCalendar.getInstance();
        calendarInstance.setTimeInMillis(calendarInstance.getTimeInMillis() + tzLocal.getOffset(calendarInstance.getTimeInMillis()));

        Date selectedRangeStart = DateUtils.dayFloor(calendarInstance.getTime());
        Long searchPeriod = Settings.getLongPref(AgendaWidgetApplication.getContext(), "searchPeriod", appWidgetId);

        calendarInstance.setTimeInMillis(selectedRangeStart.getTime() + searchPeriod + tzLocal.getOffset(calendarInstance.getTimeInMillis()));
        Date selectedRangeEnd = DateUtils.dayEnd(calendarInstance.getTime());

        ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();

        final String[] PROJECTION = new String[]{
                CalendarContract.Instances.EVENT_ID,
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

        Date now = GregorianCalendar.getInstance().getTime();
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

            if(allDay || now.compareTo(endDate)<=0) {
                CalendarEvent e = new CalendarEvent(id, color, title, location, description, startDate, endDate, allDay);
                Events.adjustAllDayEvents(e);

                if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "repeatMultidayEvents", appWidgetId) && e.isMultiDay()) {
                    calendarEvents.addAll(e.getMultidayEventsList(selectedRangeEnd));
                } else {
                    calendarEvents.add(e);
                }
            }
        }
        cur.close();
        Collections.sort(calendarEvents);

        if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "groupByDate", appWidgetId)) {
            // add dayGroups
            List<EventItem> tmpCalendarEvents = new ArrayList<>();
            Date headerDate = DateUtils.dayFloor(DateUtils.previousDay(selectedRangeStart));

            for(EventItem e: calendarEvents) {
                Date current = DateUtils.dayFloor(e.getStartDate());
                if(current.compareTo(headerDate) != 0) {
                    headerDate = DateUtils.dayFloor(current);
                    if(headerDate.compareTo(DateUtils.dayFloor(selectedRangeStart)) < 0) {
                        headerDate = DateUtils.dayFloor(selectedRangeStart);
                    }
                    DayGroup dg = new DayGroup(headerDate);
                    if(!tmpCalendarEvents.contains(dg)) {
                        tmpCalendarEvents.add(dg);
                    }
                }
                tmpCalendarEvents.add(e);
            }

            calendarEvents = tmpCalendarEvents;

        }

        return calendarEvents;
    }


}
