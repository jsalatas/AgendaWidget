package gr.ictpro.jsalatas.agendawidget.model.settings.types;

abstract class SettingString extends Setting<String> {
    @Override
    public String getValue() {
        return getStringValue();
    }

    @Override
    public void setValue(String value) {
        setStringValue(value);
    }

}
