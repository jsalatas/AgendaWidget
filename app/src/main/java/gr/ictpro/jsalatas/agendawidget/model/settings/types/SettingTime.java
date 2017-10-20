package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;

import java.util.Calendar;
import java.util.Date;

public class SettingTime extends SettingDateTime {
    @Override
    public View getView(Context context) {
        View v = super.getView(context);
        Date currentTime = Calendar.getInstance().getTime();

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(Settings.formatTime(getValue(), currentTime));

        return v;
    }

}
