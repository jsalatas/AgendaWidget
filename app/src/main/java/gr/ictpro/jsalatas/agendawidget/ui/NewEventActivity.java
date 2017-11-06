package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskProvider;
import gr.ictpro.jsalatas.agendawidget.model.task.providers.NoTaskProvider;

public class NewEventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        getWindow().setLayout((int)(300 * Resources.getSystem().getDisplayMetrics().density), ViewGroup.LayoutParams.WRAP_CONTENT);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int widgetId = -1;
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        final TaskContract task;
        if(widgetId != -1) {
            TaskContract t = TaskProvider.getTaskContract(Settings.getStringPref(AgendaWidgetApplication.getContext(), "taskProvider", widgetId));
            task = t instanceof NoTaskProvider ? null : t;
        } else {
            task = null;
        }

        findViewById(R.id.add_calendar_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
                startActivity(intent);
                finish();
            }
        });

        if(task != null) {
            findViewById(R.id.add_task).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(task.getIntent(null));
                    finish();
                }
            });
        } else {
            // start calendar activity immediately. No need for dialog
            Intent i = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
            startActivity(i);
            finish();
        }
    }
}
