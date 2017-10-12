package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import gr.ictpro.jsalatas.agendawidget.R;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Root(name="settings")
public class Settings {
    @ElementList(inline= true, entry="setting")
    private List<Setting> settings;

    private Context context;
    private int widgetId;

    public Settings() {
        // Used for deserialization
    }

    public Settings(Context context, int widgetId) {
        this.context = context;
        this.widgetId = widgetId;
        Serializer serializer = new Persister();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.context.getResources().openRawResource(R.raw.settings)));
            settings = serializer.read(Settings.class, br).getSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Setting> getSettings() {
        return settings;
    }

}
