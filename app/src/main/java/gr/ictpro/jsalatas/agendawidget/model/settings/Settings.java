package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.content.SharedPreferences;
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
    private static final String PREFS_NAME = "gr.ictpro.jsalatas.agendawidget.ui.AgendaWidget";
    private static List<Setting> settingsList;

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
            loadSettingsValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSettingsValues() {
        for (Setting setting:settings) {
            setting.setValue(getStringPref(context, setting.getName(), widgetId));
        }
    }

    public void saveSettingsValues() {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        for (Setting setting:settings) {
            switch (setting.getType()) {
                case BOOL:
                        prefs.putBoolean(setting.getName(), Boolean.parseBoolean(setting.getValue()));
                    break;
                default: //String
                    prefs.putString(setting.getName(), setting.getValue());
                     break;
            }

        }
        prefs.apply();
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


    public static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        for(Setting setting: settingsList) {
            prefs.remove(setting.getName() + "_" + appWidgetId);
        }
        prefs.apply();

    }

    public static String getStringPref(Context context, String name, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(name + "_" + appWidgetId, getDefaultPref(name));
    }

    public static Boolean getBoolPref(Context context, String name, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return Boolean.parseBoolean(prefs.getString(name + "_" + appWidgetId, getDefaultPref(name)));
    }

    private static String getDefaultPref(String name) {
        for(Setting setting: settingsList) {
            if(setting.getName().equals(name)) {
                // Value is null so we always get the default value
                // TODO: Maybe I need to reconsider the whole approach
                return setting.getValue();
            }
        }
        throw new IllegalArgumentException("No setting found with name " + name);
    }

    public static void initiallize(Context context) {
        Serializer serializer = new Persister();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.settings)));
            settingsList = serializer.read(Settings.class, br).getSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
