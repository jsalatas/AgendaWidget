package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class SettingBool extends Setting<Boolean> {
    @Override
    public Boolean getValue() {
        return Boolean.parseBoolean(getStringValue());
    }

    @Override
    public void setValue(Boolean value) {
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

    @Override
    protected SettingDialog<Boolean> getDialog(View view) {
        throw new IllegalArgumentException("No dialog needed for bool settings");
    }
}
