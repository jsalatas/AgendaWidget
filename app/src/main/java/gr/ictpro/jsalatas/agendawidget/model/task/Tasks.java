package gr.ictpro.jsalatas.agendawidget.model.task;

import android.content.ContentResolver;
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

public class Tasks {
    public final static String READ_TASKS_PERMISSION = "org.dmfs.permission.READ_TASKS";
    private static List<Task> taskList;

    public static void refreshTaskList() {
        if (!checkPermissions()) {
            return;
        }
        //TODO: Get actual setting
        //TaskContract tasks = TaskProvider.getTaskContract(Settings.getStringPref(AgendaWidgetApplication.getContext(), "taskProvider", appWidgetId);
        TaskContract tasks = TaskProvider.getTaskContract("gr.ictpro.jsalatas.agendawidget.model.task.providers.OpenTaskProvider");

        final String[] TASK_PROJECTION = new String[]{
                tasks.getListId(),
                tasks.getListAccountName(),
                tasks.getListName(),
                tasks.getListColor(),
                tasks.getListSyncEnabled()
        };

        final ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();
        final Uri uri = Uri.parse(tasks.getTaskListsURI());
        Cursor cur = cr.query(uri, TASK_PROJECTION, null, null, null);
        final List<Task> result = new ArrayList<>();

        while (cur.moveToNext()) {
            Long id = cur.getLong(0);
            String accountName = cur.getString(1);
            String name = cur.getString(2);
            int color = cur.getInt(3);

            // TODO: created an option on whether to show non-synced calendars and task lists
            boolean syncEnabled = cur.getInt(4) == 1;
            if (syncEnabled) {
                Task t = new Task(id, accountName, name, color);
                result.add(t);
            }
        }
        cur.close();
        taskList = result;
    }

    private static boolean checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(AgendaWidgetApplication.getContext(), READ_TASKS_PERMISSION);
        return permissionCheck != PackageManager.PERMISSION_DENIED;
    }

    public static List<Task> getTaskList() {
        return taskList;
    }

    public static List<EventItem> getEvents(int appWidgetId) {
        List<EventItem> taskEvents = new ArrayList<>();
        if (!checkPermissions()) {
            return taskEvents;
        }

        refreshTaskList();

        //TODO: Get actual setting
        //TaskContract tasks = TaskProvider.getTaskContract(Settings.getStringPref(AgendaWidgetApplication.getContext(), "taskProvider", appWidgetId);
        TaskContract tasks = TaskProvider.getTaskContract("gr.ictpro.jsalatas.agendawidget.model.task.providers.OpenTaskProvider");

        String[] tasksList = Settings.getStringPref(AgendaWidgetApplication.getContext(), "tasks", appWidgetId).split("@@@");
        StringBuilder sb = new StringBuilder();
        for (String task : tasksList) {
            if (!sb.toString().isEmpty()) {
                sb.append(" OR ");
            }
            sb.append(tasks.getItemListId()).append(" = ").append(task);
        }

        String selectedAccountsFilter = sb.toString();

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

        final String[] TASK_PROJECTION = new String[]{
                tasks.getItemId(),
                tasks.getItemTaskColor(),
                tasks.getItemTitle(),
                tasks.getItemLocation(),
                tasks.getItemDescription(),
                tasks.getItemTz(),
                tasks.getItemDtstart(),
                tasks.getItemDue(),
                tasks.getItemIsAllday(),
                tasks.getItemPriority(),
                tasks.getItemCompleted(),
        };

        long id;
        @ColorInt int color;
        String title;
        String location;
        String description;
        Date startDate;
        Date endDate;
        boolean allDay;
        int priority;

        long startRange = selectedRangeStart.getTime();
        long endRange = selectedRangeEnd.getTime();

        sb = new StringBuilder();
        final String dtStart = tasks.getItemDtstart();
        final String due = tasks.getItemDue();

        // FIXME: This is a mess :(
        if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "useCalendarSearchPeriod", appWidgetId)) {
            // DTSTART >= startRange and DTSTART <= endRange
            sb.append("((((")
                    .append(dtStart).append(">=").append(startRange)
                    .append(" AND ")
                    .append(dtStart).append("<=").append(endRange)
                    .append(")")
                    .append(" or ")
                    // DUE >= startRange and DUE <= endRange
                    .append("(")
                    .append(due).append(">=").append(startRange)
                    .append(" AND ")
                    .append(due).append("<=").append(endRange)
                    .append(")")
                    .append(" or ")
                    // DSTART <= startRange and DUE => endRange
                    .append("(")
                    .append(dtStart).append("<=").append(startRange)
                    .append(" AND ")
                    .append(due).append(">=").append(endRange)
                    .append(")")
                    .append(" or ")
                    //  DSTART = 0 and (DUE = 0 OR DUE <= endRange)
                    .append("(")
                    .append(dtStart).append(" is null")
                    .append(" AND (")
                    .append(due).append(" is null")
                    .append(" or ")
                    .append(due).append("<=").append(endRange)
                    .append("))")
                    .append(" or ")
                    // DUE = 0 and (DTSTART = 0 OR DTSTART <= endRange)
                    .append("(")
                    .append(due).append(" is null")
                    .append(" AND (")
                    .append(dtStart).append(" is null")
                    .append(" or ")
                    .append(dtStart).append("<=").append(endRange)
                    .append(")))")
                    .append(" or (")
                    .append(due).append("<=").append(startRange)
                    .append("))");

        } else {
            sb.append("(")
                    .append(due).append(" is null")
                    .append(" or ")
                    .append(due).append(">=").append(startRange);
        }
        if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "showOverdueTasks", appWidgetId)) {
            sb.append(" or (")
                    .append(due).append("<").append(startRange)
                    .append(")");
        }
        sb.append(") AND (")
                .append(dtStart).append(" is null")
                .append(" or ")
                .append(dtStart).append("<=").append(now.getTime());
        sb.append(") AND (")
                .append(tasks.getItemCompleted()).append(" is null")
                .append(" or ")
                .append(tasks.getItemCompleted()).append("=0")
                .append(")");

        String selection = "(" + selectedAccountsFilter + ") AND (" + sb.toString() + ")";

        final Uri uri = Uri.parse(tasks.getTasksURI());
        Cursor cur = cr.query(uri, TASK_PROJECTION, selection, null, null);


        while (cur.moveToNext()) {
            id = cur.getLong(0);
            color = cur.getInt(1);
            title = cur.getString(2);
            location = cur.getString(3);
            description = cur.getString(4);
            allDay = cur.getInt(8) == 1;
            calendarInstance.setTimeInMillis(cur.getLong(6));
            startDate = calendarInstance.getTime();
            calendarInstance.setTimeInMillis(cur.getLong(7));
            endDate = calendarInstance.getTime();
            priority = cur.getInt(9);

            TaskEvent e = new TaskEvent(id, color, title, location, description, startDate, endDate, allDay, priority);
            Events.adjustAllDayEvents(e);

            if ((allDay && now.compareTo(DateUtils.dayCeil(e.getEndDate())) < 0)
                    || (!allDay && now.compareTo(e.getEndDate()) <= 0)
                    || e.getEndDate().getTime() == 0
                    || (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "showOverdueTasks", appWidgetId) && now.compareTo(e.getEndDate()) > 0)) {
                taskEvents.add(e);
            }
        }
        cur.close();

        return taskEvents;
    }


}
