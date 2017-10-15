package gr.ictpro.jsalatas.agendawidget.application;

import android.app.Application;
import android.content.Context;
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

    public static Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            return context.getResources().getConfiguration().locale;
        }
    }

}
