package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

import java.util.Calendar;
import java.util.Date;

public class SettingTime extends SettingDateTime {
    @Override
    public View getView(Context context, boolean isEnabled) {
        View v = super.getView(context, isEnabled);
        Date currentTime = Calendar.getInstance().getTime();

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(Settings.formatTime(getValue(), currentTime));

        return v;
    }

    @Override
    protected SettingDialog<String> getDialog(View view) {
        return new DateFormatDialog((Activity) view.getContext(), this);
    }

}
