package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;
import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendars;
import gr.ictpro.jsalatas.agendawidget.model.calendar.EventItem;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.service.AgendaWidgetService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AgendaWidgetConfigureActivity AgendaWidgetConfigureActivity}
 */
public class AgendaWidget extends AppWidgetProvider {
    static final String ACTION_FORCE_UPDATE = "gr.ictpro.jsalatas.agendawidget.action.FORCE_UPDATE";
    private static final SparseArray<WidgetValues> widgetValues = new SparseArray<>();

    private static class WidgetValues {
        private long lastUpdate = 0;
    }

    public static class AgendaUpdateService extends Service {
        private static final String ACTION_UPDATE = "gr.ictpro.jsalatas.agendawidget.action.UPDATE";

        private final static IntentFilter intentFilter;

        private final static IntentFilter calendarIntentFilter;

        static {
            intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_DREAMING_STOPPED);
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
            intentFilter.addAction(ACTION_UPDATE);

            calendarIntentFilter = new IntentFilter();
            calendarIntentFilter.addAction(Intent.ACTION_PROVIDER_CHANGED);
            calendarIntentFilter.addDataScheme("content");
            calendarIntentFilter.addDataAuthority("com.android.calendar", null);
        }

        private void sendUpdate(Context context, Intent intent) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AgendaWidget.class.getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            Intent widgetUpdateIntent = new Intent();
            widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            if (!intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                widgetUpdateIntent.putExtra(AgendaWidget.ACTION_FORCE_UPDATE, true);
            }

            sendBroadcast(widgetUpdateIntent);
        }

        private final BroadcastReceiver agendaChangedReceiver = new
                BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        sendUpdate(context, intent);
                    }
                };

        private final BroadcastReceiver calendarChangedReceiver = new
                BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        sendUpdate(context, intent);
                    }
                };

        @Override
        public void onDestroy() {
            unregisterReceiver(agendaChangedReceiver);
            unregisterReceiver(calendarChangedReceiver);
            super.onDestroy();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            registerReceiver(calendarChangedReceiver, calendarIntentFilter);

            registerReceiver(agendaChangedReceiver, intentFilter);
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(ACTION_UPDATE)) {
                    agendaChangedReceiver.onReceive(this, intent);
                }
            }


            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.agenda_widget);

        views.setInt(R.id.widgetLayout, "setBackgroundColor", Color.parseColor(Settings.getStringPref(context, "backgroundColor", appWidgetId)));
        views.setInt(R.id.widgetLayoutShadow, "setBackgroundResource", Settings.getBoolPref(context, "dropShadow", appWidgetId) ? android.R.drawable.dialog_holo_light_frame : R.drawable.widget_transparent);

        Date currentTime = Calendar.getInstance().getTime();

        views.setTextViewText(R.id.tvCurrentDate, Settings.formatDate(Settings.getStringPref(context, "longDateFormat", appWidgetId), currentTime));

        if (widgetValues.indexOfKey(appWidgetId) < 0) {
            widgetValues.put(appWidgetId, new WidgetValues());
        }
        WidgetValues values = widgetValues.get(appWidgetId);

        Uri data = Uri.withAppendedPath(Uri.parse("agenda://widget/id/"), String.valueOf(appWidgetId));

        // Bind widget configuration button
        Intent configIntent = new Intent(context, AgendaWidgetConfigureActivity.class);
        configIntent.setData(data);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.imgSettings, pendingIntent);

        // Bind update button
        Intent refreshIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, AgendaWidget.class);
        refreshIntent.setData(data);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        refreshIntent.putExtra(AgendaWidget.ACTION_FORCE_UPDATE, true);

        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});

        pendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imgRefresh, pendingIntent);


        // TODO: beyond this point update should happen only after the specified time expired

        long now = Calendar.getInstance().getTimeInMillis();
        if (now - values.lastUpdate + 60000 >= Settings.getLongPref(context, "updateFrequency", appWidgetId)) {
            Log.d("Widget", "    >>>>> updating list");
            values.lastUpdate = Calendar.getInstance().getTimeInMillis();

            views.setInt(R.id.tvCurrentDate, "setTextColor", Color.parseColor(Settings.getStringPref(context, "headerColor", appWidgetId)));
            views.setInt(R.id.imgAdd, "setColorFilter", Color.parseColor(Settings.getStringPref(context, "controlColor", appWidgetId)));
            views.setInt(R.id.imgRefresh, "setColorFilter", Color.parseColor(Settings.getStringPref(context, "controlColor", appWidgetId)));
            views.setInt(R.id.imgSettings, "setColorFilter", Color.parseColor(Settings.getStringPref(context, "controlColor", appWidgetId)));


            Intent svcIntent = new Intent(context, AgendaWidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.lvEvents, svcIntent);

            // This forces the widget to replace its factory and get updated
            //appWidgetManager.updateAppWidget(appWidgetId, null);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lvEvents);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            context.stopService(new Intent(context, AgendaUpdateService.class));
            context.startService(new Intent(context, AgendaUpdateService.class));
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && intent.getBooleanExtra(ACTION_FORCE_UPDATE, false)) {
            resetLastUpdate(intent);
        }
        super.onReceive(context, intent);
    }

    private void resetLastUpdate(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (appWidgetIds != null && appWidgetIds.length > 0) {
                for (int appWidgetId : appWidgetIds) {
                    if (widgetValues.indexOfKey(appWidgetId) < 0) {
                        widgetValues.put(appWidgetId, new WidgetValues());
                    }
                    widgetValues.get(appWidgetId).lastUpdate = 0;
                }
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            Settings.deletePrefs(context, appWidgetId);
            widgetValues.remove(appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, AgendaUpdateService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, AgendaUpdateService.class));
    }
}

