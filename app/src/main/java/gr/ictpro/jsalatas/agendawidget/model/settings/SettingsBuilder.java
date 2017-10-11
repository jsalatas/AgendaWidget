package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.util.Log;
import gr.ictpro.jsalatas.agendawidget.R;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SettingsBuilder {
    private Context context;
    private int widgetId;
    private Settings settings;

    public SettingsBuilder(Context context, int widgetId) {
        this.context = context;
        this.widgetId = widgetId;
        Serializer serializer = new Persister();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.context.getResources().openRawResource(R.raw.settings)));
            settings = serializer.read(Settings.class, br);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("SettingsBuild", ">>>>>>>>>>>> loaded");
    }
}
