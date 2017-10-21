package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.ui.UpdateFrequencyDialog;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class SettingUpdateFrequency extends SettingLong {
    @Override
    protected SettingDialog<Long> getDialog(View view) {
        return new UpdateFrequencyDialog((Activity) view.getContext(), this);
    }

    @Override
    protected boolean shouldRefreshList() {
        return true;
    }

    @Override
    public View getView(Context context) {
        View v = super.getView(context);

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);

        UpdateFrequencyDialog.UpdateFrequency uf = UpdateFrequencyDialog.getUpdateFrequency(getValue());
        tvDescription.setText(AgendaWidgetApplication.getContext().getString(R.string.every) + " " + uf.getValue() + " " + uf.getTimeUnit());
        return v;
    }

}
