package gr.ictpro.jsalatas.agendawidget.model.task;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.task.providers.NoTaskProvider;
import gr.ictpro.jsalatas.agendawidget.ui.AgendaWidget;
import gr.ictpro.jsalatas.agendawidget.utils.DateUtils;

import java.util.*;

public class Tasks {
    public static List<Task>  refreshTaskList(int appWidgetId) {
        TaskContract tasks = TaskProvider.getTaskContract(Settings.getStringPref(AgendaWidgetApplication.getContext(), "taskProvider", appWidgetId));
        return refreshTaskList(tasks);
    }
    public static List<Task> refreshTaskList(TaskContract tasks) {
        if (tasks.getTaskListsURI() == null || !checkPermissions(tasks)) {
            return new ArrayList<>();
        }

        final String[] TASK_PROJECTION = tasks.getListSelectFields();

        final ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();
        final Uri uri = Uri.parse(tasks.getTaskListsURI());
        Cursor cur = cr.query(uri, TASK_PROJECTION, null, null, null);
        final List<Task> result = new ArrayList<>();

        while (cur.moveToNext()) {
            Task t = tasks.getTaskList(cur);
            if(t != null) {
                result.add(t);
            }
        }
        cur.close();
        return result;
    }

    private static boolean checkPermissions(TaskContract tasks) {
        if (tasks.getPermissions() == null) {
            return true;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(AgendaWidgetApplication.getContext(), tasks.getPermissions());
        return permissionCheck != PackageManager.PERMISSION_DENIED;
    }

    public static List<EventItem> getEvents(int appWidgetId) {
        List<EventItem> taskEvents = new ArrayList<>();
        TaskContract tasks = TaskProvider.getTaskContract(Settings.getStringPref(AgendaWidgetApplication.getContext(), "taskProvider", appWidgetId));

        if(tasks == null) {
            return taskEvents;
        }
        if (tasks instanceof NoTaskProvider || !checkPermissions(tasks)) {
            return taskEvents;
        }

        refreshTaskList(appWidgetId);

        Date now = GregorianCalendar.getInstance().getTime();

        TimeZone tzLocal = TimeZone.getDefault();

        java.util.Calendar calendarInstance = GregorianCalendar.getInstance();
        Date selectedRangeStart = DateUtils.dayFloor(calendarInstance.getTime());
        calendarInstance.setTimeInMillis(selectedRangeStart.getTime() + tzLocal.getOffset(calendarInstance.getTimeInMillis()));
        selectedRangeStart = DateUtils.dayFloor(calendarInstance.getTime());

        Long searchPeriod = Settings.getLongPref(AgendaWidgetApplication.getContext(), "searchPeriod", appWidgetId);

        calendarInstance.setTimeInMillis(selectedRangeStart.getTime() + searchPeriod);
        Date selectedRangeEnd = DateUtils.dayEnd(calendarInstance.getTime());

        final ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();

        final Uri uri = Uri.parse(tasks.getTasksURI());
        Cursor cur = cr.query(uri, tasks.getEventSelectFields(), tasks.getTaskFilter(selectedRangeStart, selectedRangeEnd, appWidgetId), tasks.getTaskFilterArgs(), null);

        while (cur.moveToNext()) {
            TaskEvent e = tasks.getTaskEvent(cur, appWidgetId);
            if(e != null) {
                tasks.adjustAllDayEvents(e);

                if ((e.isAllDay() && now.compareTo(DateUtils.dayCeil(e.getEndDate())) < 0)
                        || (!e.isAllDay() && now.compareTo(e.getEndDate()) <= 0)
                        || e.getEndDate().getTime() == 0
                        || (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "showOverdueTasks", appWidgetId) && now.compareTo(e.getEndDate()) > 0)) {
                    taskEvents.add(e);
                }
            }
        }
        cur.close();

        return taskEvents;
    }


}
