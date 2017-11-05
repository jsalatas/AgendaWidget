package gr.ictpro.jsalatas.agendawidget.model.task.providers;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.Events;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskEvent;

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
    public @NonNull Intent getViewIntent(TaskEvent event) {
        Uri contentUri = Uri.parse(getTasksURI());
        Uri uri = ContentUris.withAppendedId(contentUri, event.getId());
        return  new Intent(Intent.ACTION_VIEW).setData(uri);
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
    public @ColorInt @NonNull int getPriorityColor(TaskEvent event) {
        int color = PRIORITY_NONE;
        if(event.getPriority() > 5) {
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
        if(o1 instanceof TaskEvent && o2 instanceof TaskEvent) {
            int o1Priority = ((TaskEvent) o1).getPriority();
            int o2Priority = ((TaskEvent) o2).getPriority();
            if (o1Priority != 0 && o2Priority != 0) {
                return o1Priority - o2Priority;
            } else if (o1Priority != o2Priority) {
                return o2Priority - o1Priority;
            }
        } else if(!(o1 instanceof TaskEvent) && o2 instanceof TaskEvent) {
            return 1;
        } else if(o1 instanceof TaskEvent) {
            return -1;
        }
        return o1.compareTo(o2);
    }

    @Override
    public void adjustAllDayEvents(CalendarEvent event) {
        Events.adjustAllDayEvents(event);
    }

}