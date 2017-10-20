package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.DateTimeFormatPickerCallback;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingsListAdapter;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;

import java.util.Calendar;
import java.util.Date;

abstract class SettingDateTime extends SettingString {
    @Override
    public View getView(Context context) {
        View v = super.getView(context);
        Date currentTime = Calendar.getInstance().getTime();

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(Settings.formatDate(getValue(), currentTime));

        return v;
    }

    @Override
    public void onClick(final AdapterView<?> parent, View view) {
        final DateFormatDialog df = new DateFormatDialog((Activity) view.getContext(), getValue(), this);
        df.setCallback(new DateTimeFormatPickerCallback() {
            @Override
            public void onFormatChosen(String format) {
                setValue(format);
                ((SettingsListAdapter)parent.getAdapter()).notifyDataSetChanged();
                df.cancel();
            }
        });
        df.show();

    }

}
