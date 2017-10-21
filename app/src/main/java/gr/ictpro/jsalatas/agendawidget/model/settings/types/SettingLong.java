package gr.ictpro.jsalatas.agendawidget.model.settings.types;

abstract class SettingLong extends Setting<Long> {
    @Override
    public Long getValue() {
        return Long.parseLong(getStringValue());
    }

    @Override
    public void setValue(Long value) {
        setStringValue(value.toString());
    }
}
