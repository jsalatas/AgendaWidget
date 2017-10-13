package gr.ictpro.jsalatas.agendawidget.model.settings;

class ListItemSetting extends ListItem {
    private Setting setting;

    ListItemSetting(Setting setting) {
        this.setting = setting;
    }

    Setting getSetting() {
        return setting;
    }
}
