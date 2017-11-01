package gr.ictpro.jsalatas.agendawidget.model.settings;

import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import org.simpleframework.xml.ElementMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PersistentSettings {
    @ElementMap(entry="setting", key="key", attribute=true, inline=true)
    private Map<String, String> persistentSettings;

    public PersistentSettings() {}

    PersistentSettings(List<Setting> settings) {
        persistentSettings = new HashMap<>();
        for(Setting setting: settings) {
            persistentSettings .put(setting.getName(), setting.getStringValue());
        }
    }

    Map<String, String> getPersistentSettings() {
        return persistentSettings;
    }
}
