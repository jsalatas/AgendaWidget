package gr.ictpro.jsalatas.agendawidget.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;

import java.util.Locale;

public class AgentaWidgetApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AgentaWidgetApplication.context = this.getApplicationContext();
        Settings.initiallize(context);
    }

    public static Context getContext() {
        return context;
    }

    public static Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            return context.getResources().getConfiguration().locale;
        }
    }

    public static String getResourceString(String name) {
        if (name.startsWith("@string/")) {
            int nameResourceID = context.getResources().getIdentifier(name, "string", context.getApplicationInfo().packageName);
            if (nameResourceID == 0) {
                throw new IllegalArgumentException("No resource string found with name " + name);
            } else {
                return context.getString(nameResourceID);
            }
        } else {
            return name;
        }
    }

    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }

}
