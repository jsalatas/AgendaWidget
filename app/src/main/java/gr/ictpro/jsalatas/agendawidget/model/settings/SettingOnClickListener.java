package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.support.v7.widget.SwitchCompat;
import android.view.View;

public class SettingOnClickListener implements View.OnClickListener {
    private Setting setting;

    SettingOnClickListener(Setting setting) {
        this.setting = setting;
    }

    @Override
    public void onClick(View v) {
        if(setting.getType() == SettingType.BOOL) {
            Boolean newValue = !Boolean.parseBoolean(setting.getValue());
            setting.setValue(newValue.toString());
            SwitchCompat s = (SwitchCompat) v.findViewWithTag("switch");
            s.setChecked(newValue);

        }
    }
}
