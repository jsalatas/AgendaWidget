package gr.ictpro.jsalatas.agendawidget.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.calendar.*;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgendaWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Service", "    >>>>> onGetViewFactory");
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
        if(appWidgetId ==  AppWidgetManager.INVALID_APPWIDGET_ID) {
            calendarEvents = new ArrayList<>();
        } else {
            calendarEvents = Calendars.getEvents(appWidgetId);
        }
        Log.d("Factory", "    >>>>> getonDataSetChanged " + calendarEvents.size());
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
        if(item instanceof DayGroup) {
            v = new RemoteViews(appContext.getPackageName(), R.layout.calendar_event_header_layout);
            v.setTextViewText(R.id.tvDate, Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), item.getStartDate()));

        } else if(item instanceof CalendarEvent) {
            // FIXME: "Group by Date" together with "Repeat Multiple Day Events", is buggy
            // TODO: number of lines
            v = new RemoteViews(appContext.getPackageName(), R.layout.calendar_event_one_line_layout);
            CalendarEvent calendarEvent = (CalendarEvent) item;

            v.setInt(R.id.viewCalendarColor, "setBackgroundColor", calendarEvent.getColor());

            boolean groupByDate = Settings.getBoolPref(appContext, "groupByDate", appWidgetId);
            boolean repeatMultidayEvents = Settings.getBoolPref(appContext, "repeatMultidayEvents", appWidgetId);
            boolean showStartDate = !repeatMultidayEvents;
            boolean showEndDate = !repeatMultidayEvents || (!DateUtils.isInSameDay(calendarEvent.getStartDate(), calendarEvent.getEndDate()) && !calendarEvent.isAllDay());
            boolean showStartTime = !calendarEvent.isAllDay();
            boolean showEndTime = !calendarEvent.isAllDay();

            StringBuilder sb = new StringBuilder();

            if(showStartDate) {
                sb.append(Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), calendarEvent.getStartDate()));
            }
            if(showStartTime) {
                if(!sb.toString().isEmpty()) {
                    sb.append(" ");
                }
                if(DateUtils.dayFloor(calendarEvent.getStartDate()).compareTo(calendarEvent.getStartDate()) == 0) {
                    sb.append(appContext.getString(R.string.start_of_day));
                } else {
                    sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getStartDate()));
                }
            }

            boolean dateRangeSeparatorAdded = false;

            if(showEndDate) {
                if(!sb.toString().isEmpty()) {
                    sb.append(" ");
                }
                sb.append("> ");
                dateRangeSeparatorAdded = true;
                sb.append(Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), calendarEvent.getEndDate()));
            }

            if(showEndTime) {
                if(!sb.toString().isEmpty()) {
                    sb.append(" ");
                }
                if(!dateRangeSeparatorAdded) {
                    sb.append("> ");
                }

                if(DateUtils.dayFloor(calendarEvent.getEndDate()).compareTo(calendarEvent.getEndDate()) == 0) {
                    sb.append(appContext.getString(R.string.end_of_day));
                } else {
                    sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getEndDate()));
                }
            }
            if(sb.toString().isEmpty()) {
                sb.append(appContext.getString(R.string.all_day));
            }

            sb.append(":");

            v.setTextViewText(R.id.tvDate, sb.toString());

            v.setTextViewText(R.id.tvTitle, calendarEvent.getTitle());

        } else if(item instanceof TaskEvent) {
            //TODO
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
