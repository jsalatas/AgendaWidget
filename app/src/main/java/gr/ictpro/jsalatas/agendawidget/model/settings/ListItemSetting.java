package gr.ictpro.jsalatas.agendawidget.model.settings;

class ListItemSetting extends ListItem {
    private final Setting setting;

    ListItemSetting(Setting setting) {
        this.setting = setting;
    }

    Setting<?> getSetting() {
        return setting;
    }
}
