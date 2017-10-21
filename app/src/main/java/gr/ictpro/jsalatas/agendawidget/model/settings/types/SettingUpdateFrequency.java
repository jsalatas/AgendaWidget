package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.view.View;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;
import gr.ictpro.jsalatas.agendawidget.ui.UpdateFrequencyDialog;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class SettingUpdateFrequency extends SettingLong {
    @Override
    protected SettingDialog<Long> getDialog(View view) {
        return new UpdateFrequencyDialog((Activity) view.getContext(), this);
    }

}
