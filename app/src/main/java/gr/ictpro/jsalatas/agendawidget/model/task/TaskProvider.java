package gr.ictpro.jsalatas.agendawidget.model.task;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.ui.AgendaWidget;

import java.util.ArrayList;
import java.util.List;

public class TaskProvider {
    private static List<TaskContract> providers;

    public static List<TaskContract> getProvidersInUse() {
        List<TaskContract> providersInUse = new ArrayList<>();
        ComponentName thisAppWidget = new ComponentName(AgendaWidgetApplication.getContext().getPackageName(), AgendaWidget.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(AgendaWidgetApplication.getContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        if(appWidgetIds != null) {
            for(int appWidgetId: appWidgetIds) {
                providersInUse.add(TaskProvider.getTaskContract(Settings.getStringPref(AgendaWidgetApplication.getContext(), "taskProvider", appWidgetId)));
            }
        }

        return providersInUse;
    }

    public synchronized static List<TaskContract> getProviders() {
        if (providers == null) {
            String[] providersList = AgendaWidgetApplication.getContext().getResources().getStringArray(R.array.task_providers);
            providers = new ArrayList<>();
            providers.add(getTaskContract("gr.ictpro.jsalatas.agendawidget.model.task.providers.NoTaskProvider"));
            for (String provider : providersList) {
                providers.add(getTaskContract(provider));
            }
        }
        return providers;
    }


    public static TaskContract getTaskContract(String className) {
        Class c;
        try {
            c = Class.forName(className);
            Object o = c.newInstance();
            if (o instanceof TaskContract) {
                return (TaskContract) o;
            }
            return null;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return null;
        }
    }
}
