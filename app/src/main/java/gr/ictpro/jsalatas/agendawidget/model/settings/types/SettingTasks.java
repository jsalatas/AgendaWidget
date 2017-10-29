package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.view.View;
import gr.ictpro.jsalatas.agendawidget.ui.CalendarSelectionDialog;
import gr.ictpro.jsalatas.agendawidget.ui.TaskSelectionDialog;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class SettingTasks extends SettingString {
    @Override
    protected SettingDialog<String> getDialog(View view) {
        return new TaskSelectionDialog((Activity) view.getContext(), this);
    }
}
