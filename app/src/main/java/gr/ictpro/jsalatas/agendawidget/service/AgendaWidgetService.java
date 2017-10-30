package gr.ictpro.jsalatas.agendawidget.service;

import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.Events;
import gr.ictpro.jsalatas.agendawidget.model.calendar.*;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskEvent;
import gr.ictpro.jsalatas.agendawidget.model.task.opentasks.TaskContract;
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

    private List<EventItem> events = new ArrayList<>();

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
            events = new ArrayList<>();
        } else {
            events = Events.getEvents(appWidgetId);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (events != null) {
            return events.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        EventItem item = events.get(position);
        RemoteViews v;

        if (item instanceof DayGroup) {
            v = new RemoteViews(appContext.getPackageName(), R.layout.calendar_event_header_layout);
            v.setTextViewText(R.id.tvDate, Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), item.getStartDate()));
        } else {
            // FIXME: This is a mess. I wish I would know how to make it cleaner :\
            CalendarEvent calendarEvent = (CalendarEvent) item;
            boolean isTask = calendarEvent instanceof TaskEvent;
            if (isTask) {
                v = new RemoteViews(appContext.getPackageName(), R.layout.task_event_layout);
                v.setInt(R.id.imgTaskPriority, "setColorFilter", ((TaskEvent)calendarEvent).getPriorityColor());

            } else { //item instanceof CalendarEvent
                v = new RemoteViews(appContext.getPackageName(), R.layout.calendar_event_layout);
                v.setInt(R.id.viewCalendarColor, "setBackgroundColor", calendarEvent.getColor());
            }

            Date now = GregorianCalendar.getInstance().getTime();

            boolean isToday = (isTask && calendarEvent.getEndDate().getTime()!=0 && calendarEvent.getEndDate().compareTo(now) <= 0) ||
                    (!isTask && (calendarEvent.containsDate(now) ||
                    DateUtils.isInSameDay(calendarEvent.getStartDate(), now) ||
                    DateUtils.isInSameDay(calendarEvent.getEndDate(), now)));

            @ColorInt int dateTitleColor = Color.parseColor(isToday ?
                    Settings.getStringPref(AgendaWidgetApplication.getContext(), "todayDateTitleColor", appWidgetId) :
                    Settings.getStringPref(AgendaWidgetApplication.getContext(), "dateTitleColor", appWidgetId));

            v.setInt(R.id.tvDate, "setTextColor", dateTitleColor);
            v.setInt(R.id.tvTitle, "setTextColor", dateTitleColor);

            StringBuilder sb = new StringBuilder();
            boolean startIsToday = DateUtils.isInSameDay(calendarEvent.getStartDate(), now);
            boolean addSpace = false;

            if(!isTask) {
                if (startIsToday && !calendarEvent.isAllDay()) {
                    sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getStartDate()));
                } else if (calendarEvent.getStartDate().compareTo(now) > 0) {
                    if (!Settings.getBoolPref(appContext, "groupByDate", appWidgetId)) {
                        sb.append(Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), calendarEvent.getStartDate()));
                    }
                    if (!calendarEvent.isAllDay() && DateUtils.dayFloor(calendarEvent.getStartDate()).compareTo(calendarEvent.getStartDate()) != 0) {
                        if (!Settings.getBoolPref(appContext, "groupByDate", appWidgetId)) {
                            sb.append(" ");
                        }
                        sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getStartDate()));
                    }
                    addSpace = true;
                }
                if (calendarEvent.isAllDay()) {
                    if (Settings.getBoolPref(appContext, "showAllDay", appWidgetId)) {
                        if (addSpace) {
                            sb.append(" ");
                        }
                        sb.append("(").append(appContext.getString(R.string.all_day)).append(")");
                    }
                } else {
                    sb.append(" -");
                }
            }

            if(calendarEvent.getEndDate().getTime() !=0) {
                boolean endIsToday = DateUtils.isInSameDay(calendarEvent.getEndDate(), now);
                if (endIsToday && !calendarEvent.isAllDay()) {
                    sb.append(" ").append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getEndDate()));
                } else if (!calendarEvent.isAllDay() || isTask) {
                    if (!DateUtils.isInSameDay(calendarEvent.getStartDate(), calendarEvent.getEndDate()) && !Settings.getBoolPref(appContext, "repeatMultidayEvents", appWidgetId)) {
                        sb.append(" ").append(Settings.formatDate(Settings.getStringPref(appContext, "shortDateFormat", appWidgetId), calendarEvent.getEndDate()));
                    }
                    if (DateUtils.dayFloor(calendarEvent.getEndDate()).compareTo(calendarEvent.getEndDate()) != 0) {
                        sb.append(" ");
                        sb.append(Settings.formatDate(Settings.getStringPref(appContext, "timeFormat", appWidgetId), calendarEvent.getEndDate()));
                    }
                }

                if (sb.toString().endsWith("-")) {
                    sb.append(" ");
                }
            }
            if(!sb.toString().isEmpty()) {
                sb.append(":");
            }

            SpannableString spanDate = new SpannableString(sb.toString());
            SpannableString spanTitle = new SpannableString(calendarEvent.getTitle());
            if (Settings.getBoolPref(appContext, "todayBold", appWidgetId) && isToday) {
                spanDate.setSpan(new StyleSpan(Typeface.BOLD), 0, sb.toString().length(), 0);
                spanTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, calendarEvent.getTitle().length(), 0);
            }
            v.setTextViewText(R.id.tvDate, spanDate);
            v.setTextViewText(R.id.tvTitle, spanTitle);

            @ColorInt int locationNotesColor = Color.parseColor(isToday ?
                    Settings.getStringPref(AgendaWidgetApplication.getContext(), "todayLocationNotesColor", appWidgetId) :
                    Settings.getStringPref(AgendaWidgetApplication.getContext(), "locationNotesColor", appWidgetId));

            v.setInt(R.id.tvLocation, "setTextColor", locationNotesColor);
            v.setInt(R.id.imgLocation, "setColorFilter", locationNotesColor);
            v.setInt(R.id.tvNotes, "setTextColor", locationNotesColor);
            v.setInt(R.id.imgNotes, "setColorFilter", locationNotesColor);


            if (Settings.getBoolPref(appContext, "showLocation", appWidgetId) && calendarEvent.getLocation() != null && !calendarEvent.getLocation().isEmpty()) {
                v.setTextViewText(R.id.tvLocation, calendarEvent.getLocation());
                v.setInt(R.id.tvLocation, "setVisibility", View.VISIBLE);
                v.setInt(R.id.imgLocation, "setVisibility", View.VISIBLE);
            } else {
                v.setTextViewText(R.id.tvLocation, "");
                v.setInt(R.id.tvLocation, "setVisibility", View.GONE);
                v.setInt(R.id.imgLocation, "setVisibility", View.GONE);
            }

            if (Settings.getBoolPref(appContext, "showNotes", appWidgetId) && calendarEvent.getDescription() != null && !calendarEvent.getDescription().isEmpty()) {
                v.setTextViewText(R.id.tvNotes, calendarEvent.getDescription());
                v.setInt(R.id.tvNotes, "setVisibility", View.VISIBLE);
                v.setInt(R.id.imgNotes, "setVisibility", View.VISIBLE);
                v.setInt(R.id.tvNotes, "setMaxLines", Settings.getIntPref(appContext, "notesMaxLines", appWidgetId));
            } else {
                v.setTextViewText(R.id.tvNotes, "");
                v.setInt(R.id.tvNotes, "setVisibility", View.GONE);
                v.setInt(R.id.imgNotes, "setVisibility", View.GONE);
            }

            Uri contentUri = (calendarEvent instanceof TaskEvent) ? Uri.parse(TaskContract.BASE_URI+ TaskContract.Tasks.CONTENT_URI): CalendarContract.Events.CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, calendarEvent.getId());
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
            v.setOnClickFillInIntent(R.id.viewCalendarEvent, intent);
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
