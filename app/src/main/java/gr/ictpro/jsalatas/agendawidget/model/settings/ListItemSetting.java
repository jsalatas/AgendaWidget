package gr.ictpro.jsalatas.agendawidget.model.settings;

import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;

public class ListItemSetting extends ListItem {
    private final Setting setting;

    ListItemSetting(Setting setting) {
        this.setting = setting;
    }

    public Setting<?> getSetting() {
        return setting;
    }
}
