package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.view.View;
import android.widget.AdapterView;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarSelectionCallback;
import gr.ictpro.jsalatas.agendawidget.ui.CalendarSelectionDialog;

public class SettingCalendars extends SettingString {

    @Override
    public void onClick(final AdapterView<?> parent, View view) {
        final CalendarSelectionDialog cs = new CalendarSelectionDialog(view.getContext(), getValue());
        cs.show();
        cs.setCallback(new CalendarSelectionCallback() {
            @Override
            public void onCalendarSelect(String selectedCalendars) {
                setValue(selectedCalendars);
                cs.cancel();
            }
        });
    }

}
