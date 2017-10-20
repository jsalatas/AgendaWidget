package gr.ictpro.jsalatas.agendawidget.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendar;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarListAdapter;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarSelectionCallback;
import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendars;

public class CalendarSelectionDialog extends Dialog {
    private CalendarSelectionCallback callback;
    private final String selectedCalendars;

    public CalendarSelectionDialog(Context context, String selectedCalendars) {
        super(context);
        this.selectedCalendars = selectedCalendars;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calendar_selection);


        TextView tv = (TextView) findViewById(R.id.tvCalendarsSelectionOk);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView l = (ListView) findViewById(R.id.lst_calendars_selection);
                StringBuilder selected = new StringBuilder();
                SparseBooleanArray checked = l.getCheckedItemPositions();
                int len = l.getCount();
                for (int i = 0; i < len; i++) {
                    if (checked.get(i)) {
                        Calendar c = (Calendar) l.getAdapter().getItem(i);
                        if (!selected.toString().isEmpty()) {
                            selected.append("@@@");
                        }
                        selected.append(c.getId());
                    }
                }

                callback.onCalendarSelect(selected.toString());
            }
        });
    }

    @Override
    public void show() {
        if (checkForPermission(Manifest.permission.READ_CALENDAR)) {
            super.show();
            loadCalendars();
        } else {
            ActivityCompat.requestPermissions(AgendaWidgetApplication.getActivity(this.getContext()), new String[]{Manifest.permission.READ_CALENDAR}, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_CALENDAR_INSIST);
            cancel();
        }
    }

    private void loadCalendars() {
        Calendars.refreshCalendarList();
        ListView l = (ListView) findViewById(R.id.lst_calendars_selection);

        CalendarListAdapter adapter = new CalendarListAdapter(getContext(), Calendars.getCalendarList());
        l.setAdapter(adapter);

        if(!selectedCalendars.isEmpty()) {
            String[] selectedIds = selectedCalendars.split("@@@");
            for (String selectedId : selectedIds) {
                int id = Integer.parseInt(selectedId);
                for (int j = 0; j < adapter.getCount(); j++) {
                    Calendar c = adapter.getItem(j);
                    if (c.getId() == id) {
                        l.setItemChecked(j, true);
                        break;
                    }
                }
            }
        }
    }

    private boolean checkForPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(AgendaWidgetApplication.getActivity(this.getContext()), permission)) {
                return false;
            } else {
                ActivityCompat.requestPermissions(AgendaWidgetApplication.getActivity(this.getContext()), new String[]{permission}, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_CALENDAR);
                return false;
            }
        }
        return true;
    }

    public void setCallback(CalendarSelectionCallback callback) {
        this.callback = callback;
    }
}
