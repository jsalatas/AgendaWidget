package gr.ictpro.jsalatas.agendawidget.model;

import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;
import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendars;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskProvider;
import gr.ictpro.jsalatas.agendawidget.model.task.Tasks;

import java.util.*;

public class Events {
    public static List<EventItem> getEvents(int appWidgetId) {
        List<EventItem> events = new ArrayList<>();

        events.addAll(Calendars.getEvents(appWidgetId));
        events.addAll(Tasks.getEvents(appWidgetId));

        // TODO: Correct Sort
        TaskContract tasks = TaskProvider.getTaskContract(Settings.getStringPref(AgendaWidgetApplication.getContext(), "taskProvider", appWidgetId));
        Collections.sort(events, tasks);

        return events;
    }

    public static void adjustAllDayEvents(CalendarEvent event) {
        // Assume current time zone for all day events
        if (event.isAllDay()) {
            TimeZone tzLocal = TimeZone.getDefault();
            java.util.Calendar calendarInstance = GregorianCalendar.getInstance();

            if(event.getStartDate().getTime() != 0) {
                calendarInstance.setTimeInMillis(event.getStartDate().getTime() - tzLocal.getOffset(event.getStartDate().getTime()));
                event.setStartDate(calendarInstance.getTime());
            }

            if(event.getEndDate().getTime() != 0) {
                calendarInstance.setTimeInMillis(event.getEndDate().getTime() - tzLocal.getOffset(event.getEndDate().getTime()));
                event.setEndDate(calendarInstance.getTime());
            }
        }

    }

    public static Long getEarliestEnd(List<EventItem> events) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(Long.MAX_VALUE);
        for (EventItem i: events) {
            if(i instanceof CalendarEvent) {
                CalendarEvent ce =(CalendarEvent) i;
                if(ce.getEndDate().getTime() > 0 && ce.getEndDate().compareTo(c.getTime())<0) {
                    c.setTime(ce.getEndDate());
                }
            }
        }

        return c.getTimeInMillis();
    }
}
