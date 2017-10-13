package gr.ictpro.jsalatas.agendawidget.application;

import android.app.Application;
import android.content.Context;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;

public class AgentaWidgetApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        AgentaWidgetApplication.context = this.getApplicationContext();
        Settings.initiallize(context);
    }
}
