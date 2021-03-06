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
import android.os.Handler;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.Toast;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.settings.TaskProviderListAdapter;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskProvider;
import gr.ictpro.jsalatas.agendawidget.service.AgendaWidgetService;
import gr.ictpro.jsalatas.agendawidget.utils.DateUtils;

import java.util.*;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AgendaWidgetConfigureActivity AgendaWidgetConfigureActivity}
 */
public class AgendaWidget extends AppWidgetProvider {
    static final String ACTION_FORCE_UPDATE = "gr.ictpro.jsalatas.agendawidget.action.FORCE_UPDATE";
    private static final String ACTION_PROVIDER_REMOVED = "gr.ictpro.jsalatas.agendawidget.action.PROVIDER_REMOVED";
    private static final String EXTRA_PACKAGE_NAME = "gr.ictpro.jsalatas.agendawidget.action.EXTRA_PACKAGE_NAME";
    public static final SparseArray<WidgetValues> widgetValues = new SparseArray<>();

    public static class WidgetValues {
        public long nextUpdate = 0;
        public long lastUpdate = 0;
        String removedProvider = null;
    }

    static class CalendarObserver extends ContentObserver {
        CalendarObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PROVIDER_CHANGED);
            sendUpdate(AgendaWidgetApplication.getContext(), intent);
        }
    }

    static class TaskObserver extends ContentObserver {
        final String uri;

        TaskObserver(Handler handler, String uri) {
            super(handler);
            this.uri = uri;
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PROVIDER_CHANGED);
            sendUpdate(AgendaWidgetApplication.getContext(), intent);
        }
    }

    public static class AgendaUpdateService extends Service {
        private static final String ACTION_UPDATE = "gr.ictpro.jsalatas.agendawidget.action.UPDATE";

        private final static IntentFilter intentFilter;

        private final static CalendarObserver calendarObserver = new CalendarObserver(new Handler());
        private static TaskObserver[] taskObservers;

        static {
            intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
            intentFilter.addAction(Intent.ACTION_MY_PACKAGE_REPLACED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addDataScheme("package");
            intentFilter.addAction(ACTION_UPDATE);

            updateTaskObservers();
        }

        private final BroadcastReceiver agendaChangedReceiver = new
                BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
                            AgendaWidget.updateTaskObservers();
                        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) && !intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                            String packageName = intent.getData().toString().replace("package:", "");
                            updateTaskProviders(context, packageName);
                            AgendaWidget.updateTaskObservers();
                        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                            AgendaWidget.updateTaskObservers();
                        }

                        sendUpdate(context, intent);
                    }
                };

        @Override
        public void onDestroy() {
            try {
                unregisterReceiver(agendaChangedReceiver);
            } catch (IllegalArgumentException e) {
                // java.lang.IllegalArgumentException: Receiver not registered: gr.ictpro.jsalatas.agendawidget.ui.AgendaWidget$AgendaUpdateService
                // do nothing
            }
            for (TaskObserver taskObserver : taskObservers) {
                getContentResolver().unregisterContentObserver(taskObserver);
            }
            getContentResolver().unregisterContentObserver(calendarObserver);
            super.onDestroy();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            try {
                getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, calendarObserver);
            } catch (SecurityException e) {
                // java.lang.SecurityException: Permission Denial: opening provider com.android.providers.calendar.CalendarProvider2
                Context context = AgendaWidgetApplication.getContext();
                Toast toast = Toast.makeText(context, context.getString(R.string.select_calendars), Toast.LENGTH_LONG);
                toast.show();
            }
            for (TaskObserver taskObserver : taskObservers) {
                getContentResolver().registerContentObserver(Uri.parse("content://" + taskObserver.uri), true, taskObserver);
            }
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

    public synchronized static void updateTaskObservers() {
        List<TaskContract> providersInUser = TaskProvider.getProvidersInUse();
        List<String> taskProviderURIs = new ArrayList<>();
        for (TaskContract t : providersInUser) {
            try {
                if (!t.getProviderURI().isEmpty() && TaskProviderListAdapter.providerExists(t)) {
                    taskProviderURIs.add(t.getProviderURI());
                }
            } catch (SecurityException e) {
                // Do nothing
            }
        }

        Context context = AgendaWidgetApplication.getContext();
        context.stopService(new Intent(context, AgendaUpdateService.class));

        AgendaUpdateService.taskObservers = new TaskObserver[taskProviderURIs.size()];
        int i = 0;
        for (String uri : taskProviderURIs) {
            AgendaUpdateService.taskObservers[i] = new TaskObserver(new Handler(), uri);
            i++;
        }

        context.startForegroundService(new Intent(context, AgendaUpdateService.class));
        context.startService(new Intent(context, AgendaUpdateService.class));
    }

    private static void updateTaskProviders(Context context, String packageName) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AgendaWidget.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        widgetUpdateIntent.putExtra(AgendaWidget.ACTION_PROVIDER_REMOVED, true);
        widgetUpdateIntent.putExtra(EXTRA_PACKAGE_NAME, packageName);

        updateTaskObservers();
        context.sendBroadcast(widgetUpdateIntent);
    }

    private static void sendUpdate(Context context, Intent intent) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AgendaWidget.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        if (!intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            widgetUpdateIntent.putExtra(AgendaWidget.ACTION_FORCE_UPDATE, true);
        }

        context.sendBroadcast(widgetUpdateIntent);
    }


    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Date currentTime = Calendar.getInstance().getTime();

        if (widgetValues.indexOfKey(appWidgetId) < 0) {
            widgetValues.put(appWidgetId, new WidgetValues());
        }
        WidgetValues values = widgetValues.get(appWidgetId);

        if (values.removedProvider != null) {
            if (Settings.getStringPref(context, "taskProvider", appWidgetId).equals(values.removedProvider)) {
                Settings.setPref(context, "taskProvider", appWidgetId, "gr.ictpro.jsalatas.agendawidget.model.task.providers.NoTaskProvider");
            }
            values.removedProvider = null;
        } else if (values.nextUpdate > currentTime.getTime()) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTimeInMillis(values.lastUpdate);
            if (DateUtils.isInSameDay(currentTime, calendar.getTime())) {
                // We need to force an update in case of a removed provider
                // no need to update
                return;
            }
        }


        values.lastUpdate = currentTime.getTime();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.agenda_widget);

        views.setInt(R.id.widgetLayout, "setBackgroundColor", Color.parseColor(Settings.getStringPref(context, "backgroundColor", appWidgetId)));
        views.setInt(R.id.widgetLayoutShadow, "setBackgroundResource", Settings.getBoolPref(context, "dropShadow", appWidgetId) ? android.R.drawable.dialog_holo_light_frame : R.drawable.widget_transparent);

        views.setTextViewText(R.id.tvCurrentDate, Settings.formatDate(Settings.getStringPref(context, "longDateFormat", appWidgetId), currentTime));

        Uri data = Uri.withAppendedPath(Uri.parse("agenda://widget/id/"), String.valueOf(appWidgetId));

        // Bind widget add button
        Intent newEventIntent = new Intent(context, NewEventActivity.class);
        newEventIntent.setData(data);
        newEventIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newEventIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.imgAdd, pendingIntent);

        // Bind widget configuration button
        Intent configIntent = new Intent(context, AgendaWidgetConfigureActivity.class);
        configIntent.setData(data);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        pendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.imgSettings, pendingIntent);

        // Bind update button
        Intent refreshIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, AgendaWidget.class);
        refreshIntent.setData(data);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        refreshIntent.putExtra(AgendaWidget.ACTION_FORCE_UPDATE, true);

        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});

        pendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imgRefresh, pendingIntent);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        PendingIntent calendarIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lvEvents, calendarIntent);

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
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && intent.getBooleanExtra(ACTION_FORCE_UPDATE, false)) {
            resetLastUpdate(intent);
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && intent.getBooleanExtra(ACTION_PROVIDER_REMOVED, false)) {
            resetProvider(intent);
        }
        super.onReceive(context, intent);
    }

    private void resetProvider(Intent intent) {
        String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);

        int[] appWidgetIds = intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            for (int appWidgetId : appWidgetIds) {
                if (widgetValues.indexOfKey(appWidgetId) < 0) {
                    widgetValues.put(appWidgetId, new WidgetValues());
                }
                widgetValues.get(appWidgetId).removedProvider = packageName;
            }
        }
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
                    widgetValues.get(appWidgetId).nextUpdate = 0;
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
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, AgendaUpdateService.class));
    }
}

