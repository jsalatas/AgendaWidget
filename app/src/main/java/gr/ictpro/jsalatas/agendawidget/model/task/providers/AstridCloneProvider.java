package gr.ictpro.jsalatas.agendawidget.model.task.providers;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.task.Task;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskEvent;
import gr.ictpro.jsalatas.agendawidget.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AstridCloneProvider implements TaskContract {
    @Override
    public String getPermissions() {
        return "org.tasks.READ";
    }

    @Override
    public @NonNull
    String getProviderName() {
        return "Astrid Clone";
    }

    @Override
    public @NonNull
    String getBaseURI() {
        return "content://org.tasks";
    }

    @Override
    public @NonNull
    String getProviderURI() {
        return "org.tasks.tasksprovider";
    }

    @Override
    public @NonNull
    String getTasksURI() {
        return getBaseURI() + "/tasks";
    }

    @Override
    public @NonNull
    Intent getIntent(TaskEvent event) {
        Uri contentUri = Uri.parse("content://org.tasks.tasksprovider/tasks");
        Uri uri;
        if(event != null) {
            uri = ContentUris.withAppendedId(contentUri, event.getId());
        } else {
            uri = contentUri;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "vnd.android.cursor.item/task");

        if(event != null) {
            intent.putExtra("id", event.getId());
        }

        return intent;
    }

    @Override
    public String getTaskListsURI() {
        return null;
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
        return null;
    }

    @Override
    public @NonNull
    String getItemTitle() {
        return "title";
    }

    @Override
    public @NonNull
    String getItemLocation() {
        return "NULL as location";
    }

    @Override
    public @NonNull
    String getItemDescription() {
        return "notes";
    }

    @Override
    public @NonNull
    String getItemDtstart() {
        return "case hideUntil when 0 then null else hideUntil end ";
    }

    @Override
    public @NonNull
    String getItemIsAllday() {
        return "dueDate <> 0 AND (dueDate % (1000*60)) <> 1000 as is_allday";
    }

    @Override
    public @NonNull
    String getItemDue() {
        return " case dueDate when 0 then null else dueDate end ";
    }

    @Override
    public @NonNull
    String getItemPriority() {
        return "importance";
    }

    @Override
    public @NonNull
    String getItemCompleted() {
        return "completed";
    }

    @Override
    public String getExtraFilter() {
        return " AND (deleted = 0)";
    }

    @Override
    public @ColorInt
    int getPriorityColor(TaskEvent event) {
        int color = PRIORITY_NONE;
        switch (event.getPriority()) {
            case 0:
                color = PRIORITY_HIGH;
                break;
            case 1:
                color = PRIORITY_MEDIUM;
                break;
            case 2:
                color = PRIORITY_LOW;
                break;
            case 3:
                color = PRIORITY_NONE;
                break;
        }

        return color;
    }


    @Override
    public int compare(EventItem o1, EventItem o2) {
        if (o1 instanceof TaskEvent && o2 instanceof TaskEvent) {
            int o1Priority = ((TaskEvent) o1).getPriority();
            int o2Priority = ((TaskEvent) o2).getPriority();
            if (o1Priority != o2Priority) {
                return o1Priority - o2Priority;
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
        // Assume current time zone for all day events
        if (event.isAllDay()) {
            if (event.getStartDate().getTime() != 0) {
                event.setStartDate(DateUtils.dayFloor(event.getStartDate()));
            }

            if (event.getEndDate().getTime() != 0) {
                event.setEndDate(DateUtils.dayFloor(event.getEndDate()));
            }
        }
    }

    @Override
    public String[] getListSelectFields() {
        return null;
    }

    @Override
    public Task getTaskList(Cursor cursor) {
        return null;
    }

    @Override
    public String getAccountsFilter(int appWidgetId) {
        return null;
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
            sb.append("(((((")
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
                    .append("((")
                    .append(dtStart).append(" is null")
                    .append(" or ")
                    .append(dtStart).append(" =0")
                    .append(") AND (")
                    .append(due).append(" is null")
                    .append(" or ")
                    .append(due).append("<=").append(er)
                    .append("))")
                    .append(" or ")
                    // DUE = 0 and (DTSTART = 0 OR DTSTART <= endRange)
                    .append("((")
                    .append(due).append(" is null")
                    .append(" or ")
                    .append(due).append(" =0")
                    .append(") AND (")
                    .append(dtStart).append(" is null")
                    .append(" or ")
                    .append(dtStart).append("<=").append(er)
                    .append(")))")
                    .append(" or (")
                    .append(due).append("<=").append(sr)
                    .append("))");

        } else {
            sb.append("((")
                    .append(due).append(" is null")
                    .append(" or ")
                    .append(due).append(" =0")
                    .append(" or ")
                    .append(due).append(">=").append(sr);
        }
        if (Settings.getBoolPref(AgendaWidgetApplication.getContext(), "showOverdueTasks", appWidgetId)) {
            sb.append(") or (")
                    .append(due).append("<").append(sr);
        }
        sb.append(")");
        if (!Settings.getBoolPref(AgendaWidgetApplication.getContext(), "showFutureTasks", appWidgetId)) {
            sb.append(") AND (")
                    .append(dtStart).append(" is null")
                    .append(" or ")
                    .append(dtStart).append("<=").append(GregorianCalendar.getInstance().getTimeInMillis());
        }
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