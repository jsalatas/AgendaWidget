package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.Setting;

public class SettingBool extends Setting<Boolean> {
    @Override
    protected Boolean getValue() {
        return Boolean.parseBoolean(getStringValue());
    }

    @Override
    protected void setValue(Boolean value) {
        setStringValue(value.toString());
    }

    @Override
    public View getView(Context context) {
        View v = View.inflate(context, R.layout.settings_list_item_switch, null);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText(AgendaWidgetApplication.getResourceString(getTitle()));

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(AgendaWidgetApplication.getResourceString(getDescription()));

        SwitchCompat s = (SwitchCompat) v.findViewById(R.id.swcValue);
        s.setChecked(getValue());
        s.setTrackTintList(context.getResources().getColorStateList(R.drawable.switch_selector));

        return v;

    }

    @Override
    public void onClick(final AdapterView<?> parent, View view) {
        Boolean newValue = !getValue();
        setValue(newValue);
        SwitchCompat s = (SwitchCompat) view.findViewById(R.id.swcValue);
        s.setChecked(newValue);
    }
}
