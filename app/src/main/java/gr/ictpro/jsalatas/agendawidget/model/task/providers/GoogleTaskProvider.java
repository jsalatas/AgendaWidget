package gr.ictpro.jsalatas.agendawidget.model.task.providers;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
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
import gr.ictpro.jsalatas.agendawidget.model.task.Tasks;

import java.util.*;

public class GoogleTaskProvider implements TaskContract {
    @Override
    public String getPermissions() {
        return "org.dayup.gtask.v2.permission.READ_TASKS";
    }

    @Override
    public @NonNull
    String getProviderName() {
        return "GTasks";
    }

    @Override
    public @NonNull
    String getBaseURI() {
        return "content://org.dayup.gtask.data";
    }

    @Override
    public @NonNull
    String getProviderURI() {
        return "org.dayup.gtask.data";
    }

    @Override
    public @NonNull
    String getTasksURI() {
        return getBaseURI() + "/tasks";
    }

    @Override
    public @NonNull
    Intent getIntent(TaskEvent event) {
        Uri contentUri = Uri.parse(event == null? getBaseURI():getTasksURI());
        Intent intent = new Intent(event == null? Intent.ACTION_INSERT:Intent.ACTION_VIEW);
        Uri uri;
        if(event != null) {
            uri = ContentUris.withAppendedId(contentUri, event.getId());
        } else {
            uri = contentUri;
        }
        intent.setDataAndType(uri, "vnd.android.cursor.item/dayup.gtask.task");

        return intent;
    }

    @Override
    public String getTaskListsURI() {
        return getBaseURI() + "/tasklist";
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
        return "null as account_name";
    }

    @Override
    public @NonNull
    String getListColor() {
        return "null as list_color";
    }

    @Override
    public @NonNull
    String getListSyncEnabled() {
        return "1 as sync_enabled";
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
        return o1.compareTo(o2);
    }

    @Override
    public void adjustAllDayEvents(CalendarEvent event) {
        Events.adjustAllDayEvents(event);
    }

    @Override
    public String[] getListSelectFields() {
        return null;
    }

    @Override
    public Task getTaskList(Cursor cursor) {
        Long id = cursor.getLong(0);
        String name = cursor.getString(1);

        Task t = new Task(id, null, name, null);
        return t;
    }

    @Override
    public String getAccountsFilter(int appWidgetId) {
        return null;
    }

    @Override
    public String[] getEventSelectFields() {
        return null;
    }

    @Override
    public TaskEvent getTaskEvent(Cursor cursor, int appWidgetId) {
        long id = cursor.getLong(0);
        int color = 0;
        String title = cursor.getString(1);
        String description = cursor.getString(2);
        Calendar calendarInstance = GregorianCalendar.getInstance();
        calendarInstance.setTimeInMillis(0);
        Date startDate = calendarInstance.getTime();
        calendarInstance.setTimeInMillis(cursor.getLong(3));
        Date endDate = calendarInstance.getTime();
        //int priority = 0;
        //boolean allDay = false;
        long listId = cursor.getLong(5);


        String[] tasksList = Settings.getStringPref(AgendaWidgetApplication.getContext(), "tasks", appWidgetId).split("@@@");
        boolean isSelected = false;
        for (String taskList : tasksList) {
            try {
                int tasklistId = Integer.parseInt(taskList);
                if (tasklistId == listId) {
                    isSelected = true;
                    break;
                }
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }

        if(isSelected) {
            return new TaskEvent(id, color, title, null, description, startDate, endDate, false, 0);
        }
        return null;
    }

    @Override
    public String getTaskFilter(Date startRange, Date endRange, int appWidgetId) {
        return null;
    }

    @Override
    public String[] getTaskFilterArgs() {
        return new String[]{"-1", "false"};
    }


}
