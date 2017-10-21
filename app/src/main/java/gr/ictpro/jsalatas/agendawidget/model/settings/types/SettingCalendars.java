package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.view.View;
import gr.ictpro.jsalatas.agendawidget.ui.CalendarSelectionDialog;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class SettingCalendars extends SettingString {
    @Override
    protected SettingDialog<String> getDialog(View view) {
        return new CalendarSelectionDialog((Activity) view.getContext(), this);
    }
}
