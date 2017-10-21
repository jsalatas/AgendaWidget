package gr.ictpro.jsalatas.agendawidget.model.settings.types;

abstract class SettingInteger extends Setting<Integer> {
    @Override
    public Integer getValue() {
        return Integer.parseInt(getStringValue());
    }

    @Override
    public void setValue(Integer value) {
        setStringValue(value.toString());
    }
}
