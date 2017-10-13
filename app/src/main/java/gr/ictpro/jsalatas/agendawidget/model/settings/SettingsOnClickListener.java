package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import gr.ictpro.jsalatas.agendawidget.R;

public class SettingsOnClickListener implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListItemSetting item = (ListItemSetting) parent.getItemAtPosition(position);
        Setting setting = item.getSetting();
        if(setting.getType() == SettingType.BOOL) {
            Boolean newValue = !Boolean.parseBoolean(setting.getValue());
            setting.setValue(newValue.toString());
            SwitchCompat s = (SwitchCompat) view.findViewById(R.id.swcValue);
            s.setChecked(newValue);
        } else {

        }

    }
}
