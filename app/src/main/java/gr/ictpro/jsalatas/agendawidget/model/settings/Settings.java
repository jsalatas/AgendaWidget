package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Root(name = "settings")
public class Settings {
    @ElementList(inline = true, entry = "setting")
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

    public List<ListItem> getListItems(SettingTab tab) {
        List<ListItem> items = new ArrayList<>();

        String category = "";
        for (Setting setting : settings) {
            if(tab != setting.getTab()) {
                continue;
            }

            if (!category.equals(setting.getCategory())) {
                category = setting.getCategory();
                items.add(new ListItemCategory(category));
            }
            items.add(new ListItemSetting(setting));
        }

        return items;
    }

}
