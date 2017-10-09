package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import gr.ictpro.jsalatas.agendawidget.R;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AgendaWidgetConfigureActivity AgendaWidgetConfigureActivity}
 */
public class AgendaWidget extends AppWidgetProvider {

    public static class AgendaUpdateService extends Service {
        private static final String ACTION_UPDATE = "gr.ictpro.jsalatas.agendawidget.action.UPDATE";

        private final static IntentFilter intentFilter;

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
        }

        private final BroadcastReceiver agendaChangedReceiver = new
                BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AgendaWidget.class.getName());
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                        Intent widgetUpdateIntent = new Intent();
                        widgetUpdateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                        sendBroadcast(widgetUpdateIntent);
                    }
                };

        @Override
        public void onCreate() {
            super.onCreate();
            registerReceiver(agendaChangedReceiver, intentFilter);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(agendaChangedReceiver);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
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

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = AgendaWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.agenda_widget);
        views.setViewPadding(R.id.widgetLayout, 16, 16, 16, 16);
        views.setInt(R.id.widgetLayout, "setBackgroundColor", Color.argb(50, 255, 255, 255));

        String currentTime = Calendar.getInstance().getTime().toString();
        views.setTextViewText(R.id.tvCurrentDate, currentTime);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AgendaWidget.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            AgendaWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
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

