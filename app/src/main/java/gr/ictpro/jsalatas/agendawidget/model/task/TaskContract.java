package gr.ictpro.jsalatas.agendawidget.model.task;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;

import java.util.Comparator;
import java.util.Date;

public interface TaskContract extends Comparator<EventItem> {
    @ColorInt int PRIORITY_NONE = Color.parseColor("#888888");
    @ColorInt int PRIORITY_LOW = Color.parseColor("#00AA00");
    @ColorInt int PRIORITY_MEDIUM = Color.parseColor("#FF8000");
    @ColorInt int PRIORITY_HIGH = Color.parseColor("#FF0000");

    String getPermissions();

    @NonNull String getProviderName();

    @NonNull String getBaseURI();

    @NonNull String getProviderURI();

    @NonNull String getTasksURI();

    @NonNull Intent getIntent(TaskEvent event);

    String getTaskListsURI();

    @NonNull String getListId();

    @NonNull String getListName();

    @NonNull String getListAccountName();

    @NonNull String getListColor();

    @NonNull String getListSyncEnabled();

    @NonNull String getItemId();

    String getItemListId();

    @NonNull String getItemTitle();

    @NonNull String getItemLocation();

    @NonNull String getItemDescription();

    @NonNull String getItemDtstart();

    @NonNull String getItemIsAllday();

    @NonNull String getItemDue();

    @NonNull String getItemPriority();

    @NonNull String getItemCompleted();

    String getExtraFilter();

    @NonNull @ColorInt int getPriorityColor(TaskEvent event);

    void adjustAllDayEvents(CalendarEvent event);

    String[] getListSelectFields();

    String[] getEventSelectFields();

    Task getTaskList(Cursor cursor);

    String getAccountsFilter(int appWidgetId);

    String getTaskFilter(Date startRange, Date endRange, int appWidgetId);

    String[] getTaskFilterArgs();

    TaskEvent getTaskEvent(Cursor cursor, int appWidgetId);
}
