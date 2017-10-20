package gr.ictpro.jsalatas.agendawidget.model.settings;

import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;

class ListItemSetting extends ListItem {
    private final Setting setting;

    ListItemSetting(Setting setting) {
        this.setting = setting;
    }

    Setting<?> getSetting() {
        return setting;
    }
}
