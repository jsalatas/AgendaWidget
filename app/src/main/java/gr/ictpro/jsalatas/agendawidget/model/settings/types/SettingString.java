package gr.ictpro.jsalatas.agendawidget.model.settings.types;

abstract class SettingString extends Setting<String> {
    @Override
    protected String getValue() {
        return getStringValue();
    }

    @Override
    protected void setValue(String value) {
        setStringValue(value);
    }
}
