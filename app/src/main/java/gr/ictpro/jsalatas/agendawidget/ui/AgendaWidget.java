package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgentaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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


    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.agenda_widget);
        views.setInt(R.id.widgetLayout, "setBackgroundColor", Color.parseColor(Settings.getStringPref(context, "backgroundColor", appWidgetId)));
        views.setInt(R.id.widgetLayoutShadow, "setBackgroundResource", Settings.getBoolPref(context, "dropShadow", appWidgetId)? android.R.drawable.dialog_holo_light_frame:R.drawable.widget_transparent);
        RelativeLayout l = new RelativeLayout(context);

        Date currentTime = Calendar.getInstance().getTime();
        Locale locale = AgentaWidgetApplication.getCurrentLocale();
        DateFormat longDateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);

        DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        views.setTextViewText(R.id.tvCurrentDate, longDateFormat.format(currentTime));
        views.setTextViewText(R.id.shortDate, shortDateFormat.format(currentTime));
        views.setTextViewText(R.id.shortTime, timeFormat.format(currentTime));



        views.setInt(R.id.tvCurrentDate, "setTextColor", Color.parseColor(Settings.getStringPref(context, "headerColor", appWidgetId)));
        views.setInt(R.id.imgAdd, "setColorFilter", Color.parseColor(Settings.getStringPref(context, "controlColor", appWidgetId)));
        views.setInt(R.id.imgRefresh, "setColorFilter", Color.parseColor(Settings.getStringPref(context, "controlColor", appWidgetId)));
        views.setInt(R.id.imgSettings, "setColorFilter", Color.parseColor(Settings.getStringPref(context, "controlColor", appWidgetId)));


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
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});

        pendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imgRefresh, pendingIntent);

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
        if(intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            context.stopService(new Intent(context, AgendaUpdateService.class));
            context.startService(new Intent(context, AgendaUpdateService.class));
        }

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AgendaWidget.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            Settings.deletePrefs(context, appWidgetId);
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

    private static float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}

