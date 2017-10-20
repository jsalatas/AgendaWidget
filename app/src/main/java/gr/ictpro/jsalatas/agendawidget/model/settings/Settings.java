package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.content.SharedPreferences;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.*;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Root(name = "settings")
public class Settings {
    private static final String PREFS_NAME = "gr.ictpro.jsalatas.agendawidget.ui.AgendaWidget";
    private static List<Setting> settingsList;

    @ElementListUnion({
            @ElementList(entry = "Bool", inline = true, type = SettingBool.class),
            @ElementList(entry = "Calendars", inline = true, type = SettingCalendars.class),
            @ElementList(entry = "Color", inline = true, type = SettingColor.class),
            @ElementList(entry = "DateLong", inline = true, type = SettingDateLong.class),
            @ElementList(entry = "DateShort", inline = true, type = SettingDateShort.class),
            @ElementList(entry = "Time", inline = true, type = SettingTime.class),
            @ElementList(entry = "Integer", inline = true, type = SettingInteger.class),
            @ElementList(entry = "TransparentColor", inline = true, type = SettingTransparentColor.class),
    })
    private List<Setting> settings;

    private Context context;
    private int widgetId;

    private Settings() {
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
            setting.setStringValue(getStringPref(context, setting.getName(), widgetId));
        }
    }

    public void saveSettingsValues() {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        for (Setting setting:settings) {
            prefs.putString(setting.getName() + "_" + widgetId, setting.getStringValue());
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

    Setting<?> getSetting(String name) {
        for(Setting setting: settings) {
            if(setting.getName().equals(name)) {
                return setting;
            }
        }
        throw new IllegalArgumentException("No setting found with name " + name);
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
                return setting.getStringValue();
            }
        }
        throw new IllegalArgumentException("No setting found with name " + name);
    }

    public static void initialize(Context context) {
        Serializer serializer = new Persister();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.settings)));
            settingsList = serializer.read(Settings.class, br).getSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatDate(String type, Date date) {
            Locale locale = AgendaWidgetApplication.getCurrentLocale();

            DateFormat df;
            String s = type.toUpperCase();
            switch (s) {
                case "FULL":
                    df = DateFormat.getDateInstance(DateFormat.FULL, locale);
                    break;
                case "LONG":
                    df = DateFormat.getDateInstance(DateFormat.LONG, locale);
                    break;
                case "MEDIUM":
                    df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
                    break;
                case "SHORT":
                    df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
                    break;
                default: //custom
                    try {
                        df = new SimpleDateFormat(type);
                    } catch (IllegalArgumentException e) {
                        return "";
                    }
                    break;
            }
            return df.format(date);
    }

    public static String formatTime(String type, Date date) {
        Locale locale = AgendaWidgetApplication.getCurrentLocale();

        DateFormat df;
        String s = type.toUpperCase();
        switch (s) {
            case "SHORT":
                df = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
                break;
            default: //custom
                try {
                    df = new SimpleDateFormat(type);
                } catch (IllegalArgumentException e) {
                    return "";
                }
                break;
        }

        return df.format(date);
    }


}
