package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.view.View;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class SettingDateLong extends SettingDateTime {
    @Override
    protected SettingDialog<String> getDialog(View view) {
        return new DateFormatDialog((Activity) view.getContext(), this);
    }
}
