package gr.ictpro.jsalatas.agendawidget.service;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.calendar.*;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.ui.AgendaWidgetConfigureActivity;
import gr.ictpro.jsalatas.agendawidget.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AgendaWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AgendaWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}

class AgendaWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context appContext;
    private final int appWidgetId;

    private List<EventItem> calendarEvents = new ArrayList<>();

    AgendaWidgetRemoteViewsFactory(Context context, Intent intent) {
        appContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        onDataSetChanged();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            calendarEvents = new ArrayList<>();
        } else {
            calendarEvents = Calendars.getEvents(appWidgetId);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (calendarEvents != null) {
            return calendarEvents.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        EventItem item = calendarEvents.get(position);
        RemoteViews v = null;
        if (item instanceof DayGroup) {
            v = new RemoteViews(appContext.getPackageName(), R.layout.calendar_event_header_layout);
            v.setTextViewText(R.id.tvDate, Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), item.getStartDate()));
        } else if (item instanceof TaskEvent) {
            //TODO

        } else if (item instanceof CalendarEvent) {
            // FIXME: This is a mess. I wish I would know how to make it cleaner :\
            // TODO: number of lines
            v = new RemoteViews(appContext.getPackageName(), R.layout.calendar_event_one_line_layout);
            CalendarEvent calendarEvent = (CalendarEvent) item;

            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calendarEvent.getId());

            Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);

            v.setOnClickFillInIntent(R.id.viewCalendarEvent, intent);

            v.setInt(R.id.viewCalendarColor, "setBackgroundColor", calendarEvent.getColor());

            Date now = GregorianCalendar.getInstance().getTime();

            @ColorInt int color = calendarEvent.containsDate(now) || DateUtils.isInSameDay(calendarEvent.getStartDate(), now) || DateUtils.isInSameDay(calendarEvent.getEndDate(), now) ?
                    AgendaWidgetApplication.getContext().getResources().getColor(R.color.colorRed) : Color.parseColor(Settings.getStringPref(AgendaWidgetApplication.getContext(), "eventsColor", appWidgetId));
            v.setInt(R.id.tvDate, "setTextColor", color);
            v.setInt(R.id.tvTitle, "setTextColor", color);

            StringBuilder sb = new StringBuilder();
            boolean startIsToday = DateUtils.isInSameDay(calendarEvent.getStartDate(), now);
            boolean addSpace = false;
            if(startIsToday && !calendarEvent.isAllDay()) {
                sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getStartDate()));
            } else if(calendarEvent.getStartDate().compareTo(now)>0) {
                if(!Settings.getBoolPref(appContext, "groupByDate", appWidgetId)) {
                    sb.append(Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), calendarEvent.getStartDate()));
                }
                if(!calendarEvent.isAllDay() && DateUtils.dayFloor(calendarEvent.getStartDate()).compareTo(calendarEvent.getStartDate()) !=0) {
                    if(!Settings.getBoolPref(appContext, "groupByDate", appWidgetId)) {
                        sb.append(" ");
                    }
                    sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getStartDate()));
                }
                addSpace = true;
            }
            if(calendarEvent.isAllDay()) {
                if(addSpace) {
                    sb.append(" ");
                }
                sb.append("(").append(appContext.getString(R.string.all_day)).append(")");
            } else {
                sb.append(" -");
            }



            boolean endIsToday = DateUtils.isInSameDay(calendarEvent.getEndDate(), now);
            if(endIsToday && !calendarEvent.isAllDay()) {
                sb.append(" ").append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getEndDate()));
            } else if(!calendarEvent.isAllDay()){
                if(!DateUtils.isInSameDay(calendarEvent.getStartDate(), calendarEvent.getEndDate()) && !Settings.getBoolPref(appContext, "repeatMultidayEvents", appWidgetId)) {
                    sb.append(" ").append(Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), calendarEvent.getEndDate()));
                }
                if(DateUtils.dayFloor(calendarEvent.getEndDate()).compareTo(calendarEvent.getEndDate()) !=0) {
                    sb.append(" ");
                    sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getEndDate()));
                }
            }

            if(sb.toString().endsWith("-")) {
                sb.append(" ");
            }
            sb.append(":");

            v.setTextViewText(R.id.tvDate, sb.toString());

            v.setTextViewText(R.id.tvTitle, calendarEvent.getTitle());

        }

        return v;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
