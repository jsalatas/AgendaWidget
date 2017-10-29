package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;

import java.util.Calendar;
import java.util.Date;

abstract class SettingDateTime extends SettingString {
    @Override
    public View getView(Context context, boolean isEnabled) {
        View v = super.getView(context, isEnabled);
        Date currentTime = Calendar.getInstance().getTime();

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(Settings.formatDate(getValue(), currentTime));

        return v;
    }

    @Override
    protected boolean shouldRefreshList() {
        return true;
    }
}
