package gr.ictpro.jsalatas.agendawidget.model.task.providers;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskEvent;

public class NoTaskProvider implements TaskContract {
    @Override
    public String getPermissions() {
        return null;
    }

    @Override
    public @NonNull
    String getProviderName() {
        // TODO: add resource string
        return "None";
    }

    @Override
    public @NonNull
    String getBaseURI() {
        return "";
    }

    @Override
    public @NonNull
    String getProviderURI() {
        return "";
    }

    @Override
    public @NonNull
    String getTasksURI() {
        return "";
    }

    @Override
    public @NonNull Intent getViewIntent(TaskEvent event) {
        return new Intent();
    }

    @Override
    public String getTaskListsURI() {
        return null;
    }

    @Override
    public @NonNull
    String getListId() {
        return "";
    }

    @Override
    public @NonNull
    String getListName() {
        return "";
    }

    @Override
    public @NonNull
    String getListAccountName() {
        return "";
    }

    @Override
    public @NonNull
    String getListColor() {
        return "";
    }

    @Override
    public @NonNull
    String getListSyncEnabled() {
        return "";
    }

    @Override
    public @NonNull
    String getItemId() {
        return "";
    }

    @Override
    public String getItemListId() {
        return null;
    }

    @Override
    public @NonNull
    String getItemTitle() {
        return "";
    }

    @Override
    public @NonNull
    String getItemLocation() {
        return "";
    }

    @Override
    public @NonNull
    String getItemDescription() {
        return "";
    }

    @Override
    public @NonNull
    String getItemDtstart() {
        return "";
    }

    @Override
    public @NonNull
    String getItemIsAllday() {
        return "";
    }

    @Override
    public @NonNull
    String getItemDue() {
        return "";
    }

    @Override
    public @NonNull
    String getItemPriority() {
        return "";
    }

    @Override
    public @NonNull
    String getItemCompleted() {
        return "";
    }

    @Override
    public String getExtraFilter() {
        return null;
    }

    @Override
    public int compare(EventItem o1, EventItem o2) {
        return 0;
    }

    @Override
    public @ColorInt @NonNull int getPriorityColor(TaskEvent event) {
        return PRIORITY_NONE;
    }

    @Override
    public void adjustAllDayEvents(CalendarEvent event) {
        // Do Nothing
    }

}