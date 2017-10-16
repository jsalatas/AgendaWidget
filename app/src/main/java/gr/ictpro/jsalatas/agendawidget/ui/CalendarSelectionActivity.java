package gr.ictpro.jsalatas.agendawidget.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendars;

public class CalendarSelectionActivity extends Dialog {
    public CalendarSelectionActivity(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_selection);
    }

    @Override
    public void show() {
        super.show();
        if (checkForPermission(Manifest.permission.READ_CALENDAR)) {
            Calendars.refreshCalendarList();
        } else {
            ActivityCompat.requestPermissions(getActivity(this.getContext()), new String[]{Manifest.permission.READ_CALENDAR}, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_CALENDAR_INSIST);
            cancel();
        }
    }

    private boolean checkForPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(this.getContext()), permission)) {
                return false;
            } else {
                ActivityCompat.requestPermissions(getActivity(this.getContext()), new String[]{permission}, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_CALENDAR);
            }
        }
        return true;
    }

    private static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }
}
