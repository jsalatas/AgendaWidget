package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarSelectionCallback;
import gr.ictpro.jsalatas.agendawidget.model.settings.DateTimeFormatPickerCallback;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingsListAdapter;
import gr.ictpro.jsalatas.agendawidget.ui.CalendarSelectionDialog;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;

import java.util.Calendar;
import java.util.Date;

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
