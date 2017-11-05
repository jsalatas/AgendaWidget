package gr.ictpro.jsalatas.agendawidget.model.task.providers;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.Events;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.task.Task;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class OpenTaskProvider implements TaskContract {
    @Override
    public String getPermissions() {
        return "org.dmfs.permission.READ_TASKS";
    }

    @Override
    public @NonNull
    String getProviderName() {
        return "Open Tasks";
    }

    @Override
    public @NonNull
    String getBaseURI() {
        return "content://org.dmfs.tasks";
    }

    @Override
    public @NonNull
    String getProviderURI() {
        return "org.dmfs.tasks";
    }

    @Override
    public @NonNull
    String getTasksURI() {
        return getBaseURI() + "/tasks";
    }

    @Override
    public @NonNull
    Intent getViewIntent(TaskEvent event) {
        Uri contentUri = Uri.parse(getTasksURI());
        Uri uri = ContentUris.withAppendedId(contentUri, event.getId());
        return new Intent(Intent.ACTION_VIEW).setData(uri);
    }

    @Override
    public String getTaskListsURI() {
        return getBaseURI() + "/tasklists";
    }

    @Override
    public @NonNull
    String getListId() {
        return "_id";
    }

    @Override
    public @NonNull
    String getListName() {
        return "list_name";
    }

    @Override
    public @NonNull
    String getListAccountName() {
        return "account_name";
    }

    @Override
    public @NonNull
    String getListColor() {
        return "list_color";
    }

    @Override
    public @NonNull
    String getListSyncEnabled() {
        return "sync_enabled";
    }

    @Override
    public @NonNull
    String getItemId() {
        return "_id";
    }

    @Override
    public String getItemListId() {
        return "list_id";
    }

    @Override
    public @NonNull
    String getItemTitle() {
        return "title";
    }

    @Override
    public @NonNull
    String getItemLocation() {
        return "location";
    }

    @Override
    public @NonNull
    String getItemDescription() {
        return "description";
    }

    @Override
    public @NonNull
    String getItemDtstart() {
        return "dtstart";
    }

    @Override
    public @NonNull
    String getItemIsAllday() {
        return "is_allday";
    }

    @Override
    public @NonNull
    String getItemDue() {
        return "due";
    }

    @Override
    public @NonNull
    String getItemPriority() {
        return "priority";
    }

    @Override
    public @NonNull
    String getItemCompleted() {
        return "completed";
    }

    @Override
    public String getExtraFilter() {
        return null;
    }

    @Override
    public @ColorInt
    @NonNull
    int getPriorityColor(TaskEvent event) {
        int color = PRIORITY_NONE;
        if (event.getPriority() > 5) {
            // low
            color = PRIORITY_LOW;
        } else if (event.getPriority() == 5) {
            // medium
            color = PRIORITY_MEDIUM;
        } else if (event.getPriority() > 0 && event.getPriority() < 5) {
            // high
            color = PRIORITY_HIGH;
        }

        return color;
    }

    @Override
    public int compare(EventItem o1, EventItem o2) {
        if (o1 instanceof TaskEvent && o2 instanceof TaskEvent) {
            int o1Priority = ((TaskEvent) o1).getPriority();
            int o2Priority = ((TaskEvent) o2).getPriority();
            if (o1Priority != 0 && o2Priority != 0) {
                return o1Priority - o2Priority;
            } else if (o1Priority != o2Priority) {
                return o2Priority - o1Priority;
            }
        } else if (!(o1 instanceof TaskEvent) && o2 instanceof TaskEvent) {
            return 1;
        } else if (o1 instanceof TaskEvent) {
            return -1;
        }
        return o1.compareTo(o2);
    }

    @Override
    public void adjustAllDayEvents(CalendarEvent event) {
        Events.adjustAllDayEvents(event);
    }

    @Override
    public String[] getListSelectFields() {
        return new String[]{
                getListId(),
                getListAccountName(),
                getListName(),
                getListColor(),
                getListSyncEnabled()
        };
    }

    @Override
    public Task getTaskList(Cursor cursor) {
       Long id = cursor.getLong(0);
        String accountName = cursor.getString(1);
        String name = cursor.getString(2);
        int color = cursor.getInt(3);

        // TODO: create an option on whether to show non-synced calendars and task lists
        boolean syncEnabled = cursor.getInt(4) == 1;
        if (syncEnabled) {
            return new Task(id, accountName, name, color);
        }
        return null;
    }

    @Override
    public String getAccountsFilter(int appWidgetId) {
        String[] tasksList = Settings.getStringPref(AgendaWidgetApplication.getContext(), "tasks", appWidgetId).split("@@@");
        StringBuilder sb = new StringBuilder();
        String selectedAccountsFilter =" 1 = 1 ";
        if(getItemListId() != null) {
            if (tasksList.length > 0 && !tasksList[0].isEmpty()) {
                for (String task : tasksList) {

                    if (!sb.toString().isEmpty()) {
                        sb.append(" OR ");
                    }
                    sb.append(getItemListId()).append(" = ").append(task);
                }

                selectedAccountsFilter = sb.toString();
            }
        }
        return selectedAccountsFilter;
    }

    @Override
    public String[] getEventSelectFields() {
        return new String[]{
                getItemId(),
                getItemTitle(),
                getItemLocation(),
                getItemDescription(),
                getItemDtstart(),
                getItemDue(),
                getItemIsAllday(),
                getItemPriority(),
                getItemCompleted(),
        };
    }

    @Override
    public TaskEvent getTaskEvent(Cursor cursor, int appWidgetId) {
        long id = cursor.getLong(0);
        @ColorInt int color = cursor.getInt(1);
        String title = cursor.getString(1);
        String location = cursor.getString(2);
        String description = cursor.getString(3);
        boolean allDay = cursor.getInt(6) == 1;
        Calendar calendarInstance = GregorianCalendar.getInstance();
        calendarInstance.setTimeInMillis(cursor.getLong(4));
        Date startDate = calendarInstance.getTime();
        calendarInstance.setTimeInMillis(cursor.getLong(5));
        Date endDate = calendarInstance.getTime();
        int priority = cursor.getInt(7);

        return new TaskEvent(id, color, title, location, description, startDate, endDate, allDay, priority);
    }

    @Override
    public String getTaskFilter(Date startRange, Date endRange, int appWidgetId) {
        long sr = startRange.getTime();
        long er = endRange.getTime();

        StringBuilder sb = new StringBuilder();
        final String dtStart = getItemDtstart();
        final String due = getItemDue();

        // FIXME: This is a mess :(
        if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "useCalendarSearchPeriod", appWidgetId)) {
            // DTSTART >= startRange and DTSTART <= endRange
            sb.append("((((")
                    .append(dtStart).append(">=").append(sr)
                    .append(" AND ")
                    .append(dtStart).append("<=").append(er)
                    .append(")")
                    .append(" or ")
                    // DUE >= startRange and DUE <= endRange
                    .append("(")
                    .append(due).append(">=").append(sr)
                    .append(" AND ")
                    .append(due).append("<=").append(er)
                    .append(")")
                    .append(" or ")
                    // DSTART <= startRange and DUE => endRange
                    .append("(")
                    .append(dtStart).append("<=").append(sr)
                    .append(" AND ")
                    .append(due).append(">=").append(er)
                    .append(")")
                    .append(" or ")
                    //  DSTART = 0 and (DUE = 0 OR DUE <= endRange)
                    .append("(")
                    .append(dtStart).append(" is null")
                    .append(" AND (")
                    .append(due).append(" is null")
                    .append(" or ")
                    .append(due).append("<=").append(er)
                    .append("))")
                    .append(" or ")
                    // DUE = 0 and (DTSTART = 0 OR DTSTART <= endRange)
                    .append("(")
                    .append(due).append(" is null")
                    .append(" AND (")
                    .append(dtStart).append(" is null")
                    .append(" or ")
                    .append(dtStart).append("<=").append(er)
                    .append(")))")
                    .append(" or (")
                    .append(due).append("<=").append(sr)
                    .append("))");

        } else {
            sb.append("(")
                    .append(due).append(" is null")
                    .append(" or ")
                    .append(due).append(">=").append(sr);
        }
        if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "showOverdueTasks", appWidgetId)) {
            sb.append(" or (")
                    .append(due).append("<").append(sr)
                    .append(")");
        }
        sb.append(") AND (")
                .append(dtStart).append(" is null")
                .append(" or ")
                .append(dtStart).append("<=").append(GregorianCalendar.getInstance().getTimeInMillis());
        sb.append(") AND (")
                .append(getItemCompleted()).append(" is null")
                .append(" or ")
                .append(getItemCompleted()).append("=0")
                .append(")");

        return sb.toString();
    }

    @Override
    public String[] getTaskFilterArgs() {
        return null;
    }
}