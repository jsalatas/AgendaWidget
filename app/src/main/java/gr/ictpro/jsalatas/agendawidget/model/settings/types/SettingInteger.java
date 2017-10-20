package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.view.View;
import android.widget.AdapterView;

public class SettingInteger extends Setting<Integer> {
    @Override
    protected Integer getValue() {
        return Integer.parseInt(getStringValue());
    }

    @Override
    protected void setValue(Integer value) {
        setStringValue(value.toString());
    }

    @Override
    public void onClick(AdapterView<?> parent, View view) {
        // TODO: hjhg
    }
}
