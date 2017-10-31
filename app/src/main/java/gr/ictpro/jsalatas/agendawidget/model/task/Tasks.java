package gr.ictpro.jsalatas.agendawidget.model.task;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.Events;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.task.opentasks.TaskContract;

import java.util.*;

public class Tasks {
    public final static String READ_TASKS_PERMISSION = "org.dmfs.permission.READ_TASKS";
    private static List<Task> taskList;

    public static void refreshTaskList() {
        if (!checkPermissions()) {
            return;
        }
        final String[] TASK_PROJECTION = new String[]{
                TaskContract.TaskListColumns._ID,
                TaskContract.TaskListColumns.ACCOUNT_NAME,
                TaskContract.TaskListColumns.LIST_NAME,
                TaskContract.TaskListColumns.LIST_COLOR,
                TaskContract.TaskListColumns.SYNC_ENABLED
        };

        final ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();
        final Uri uri = Uri.parse(TaskContract.BASE_URI + TaskContract.TaskList.CONTENT_URI);
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

        String[] tasksList = Settings.getStringPref(AgendaWidgetApplication.getContext(), "tasks", appWidgetId).split("@@@");
        StringBuilder sb = new StringBuilder();
        for (String task : tasksList) {
            if (!sb.toString().isEmpty()) {
                sb.append(" OR ");
            }
            sb.append(TaskContract.TaskColumns.LIST_ID).append(" = ").append(task);
        }

        String selectedAccountsFilter = sb.toString();

        java.util.Calendar calendarInstance = GregorianCalendar.getInstance();
        Date selectedRangeStart = calendarInstance.getTime();
        Long searchPeriod = Settings.getLongPref(AgendaWidgetApplication.getContext(), "searchPeriod", appWidgetId);

        // CONFIRM: I believe I need to round down milliseconds' value to zero from the end time.
        //          This will avoid cases that end time matches exactly the end time of an event.
        calendarInstance.setTimeInMillis(selectedRangeStart.getTime() + searchPeriod);
        calendarInstance.set(java.util.Calendar.MILLISECOND, 0);
        Date selectedRangeEnd = calendarInstance.getTime();

        sb = new StringBuilder();
        sb.append("(((").append(CalendarContract.Events.DTSTART).append(" >= ").append(selectedRangeStart.getTime()).append(") AND ");
        sb.append("(").append(CalendarContract.Events.DTSTART).append(" <= ").append(selectedRangeEnd.getTime()).append(")) OR ");
        sb.append("((").append(CalendarContract.Events.DTEND).append(" >= ").append(selectedRangeStart.getTime()).append(") AND ");
        sb.append("(").append(CalendarContract.Events.DTEND).append(" <= ").append(selectedRangeEnd.getTime()).append(")))");

        final ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();

        final String[] TASK_PROJECTION = new String[]{
                TaskContract.TaskColumns._ID,
                TaskContract.TaskColumns.TASK_COLOR,
                TaskContract.TaskColumns.TITLE,
                TaskContract.TaskColumns.LOCATION,
                TaskContract.TaskColumns.DESCRIPTION,
                TaskContract.TaskColumns.TZ,
                TaskContract.TaskColumns.DTSTART,
                TaskContract.TaskColumns.DUE,
                TaskContract.TaskColumns.IS_ALLDAY,
                TaskContract.TaskColumns.PRIORITY,
                TaskContract.TaskColumns.COMPLETED,
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
        // DTSTART >= startRange and DTSTART <= endRange
        sb.append("(((")
                .append(TaskContract.TaskColumns.DTSTART).append(">=").append(startRange)
                .append(" AND ")
                .append(TaskContract.TaskColumns.DTSTART).append("<=").append(endRange)
                .append(")")
                .append(" or ")
                // DUE >= startRange and DUE <= endRange
                .append("(")
                .append(TaskContract.TaskColumns.DUE).append(">=").append(startRange)
                .append(" AND ")
                .append(TaskContract.TaskColumns.DUE).append("<=").append(endRange)
                .append(")")
                .append(" or ")
                // DSTART <= startRange and DUE => endRange
                .append("(")
                .append(TaskContract.TaskColumns.DTSTART).append("<=").append(startRange)
                .append(" AND ")
                .append(TaskContract.TaskColumns.DUE).append(">=").append(endRange)
                .append(")")
                .append(" or ")
                //  DSTART = 0 and (DUE = 0 OR DUE >= startRange)
                .append("(")
                .append(TaskContract.TaskColumns.DTSTART).append(" is null")
                .append(" AND (")
                .append(TaskContract.TaskColumns.DUE).append(" is null")
                .append(" or ")
                .append(TaskContract.TaskColumns.DUE).append(">=").append(startRange)
                .append("))")
                .append(" or ")
                // DUE = 0 and (DTSTART = 0 OR DTSTART <= endRange)
                .append("(")
                .append(TaskContract.TaskColumns.DUE).append(" is null")
                .append(" AND (")
                .append(TaskContract.TaskColumns.DTSTART).append(" is null")
                .append(" or ")
                .append(TaskContract.TaskColumns.DTSTART).append("<=").append(endRange)
                .append(")))")
        // TODO: add overdue according to the chosen settting

                .append(" or (")
                .append(TaskContract.TaskColumns.DUE).append("<=").append(startRange)
                .append("))")

                .append(" AND (")
                .append(TaskContract.TaskColumns.COMPLETED).append(" is null")
                .append(" or ")
                .append(TaskContract.TaskColumns.COMPLETED).append("=0")
                .append(")");

        Log.d("TASK", ">>>>>>>>> " + sb.toString());

        String selection = "(" + selectedAccountsFilter + ") AND (" + sb.toString() + ")";

        final Uri uri = Uri.parse(TaskContract.BASE_URI + TaskContract.Tasks.CONTENT_URI);
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

            Log.d("TASK", ">>>>>>>>> title: " + title +
                    ", allday: " + allDay +
                    ", start: " + cur.getLong(6) + " " + startDate  +
                    ", end: " + cur.getLong(7) +
                    ", priority: " + priority +
                    ", completed: " + cur.getLong(10));

            TaskEvent e = new TaskEvent(id, color, title, location, description, startDate, endDate, allDay, priority);
            Events.adjustAllDayEvents(e);
            Log.d("TASK", ">>>>>>>>" + e.getStartDate());
            taskEvents.add(e);
        }
        cur.close();

        return taskEvents;
    }


}
