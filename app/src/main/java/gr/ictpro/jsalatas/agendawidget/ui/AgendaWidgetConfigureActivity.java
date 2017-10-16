package gr.ictpro.jsalatas.agendawidget.ui;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingTab;
import gr.ictpro.jsalatas.agendawidget.model.settings.Settings;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingsListAdapter;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingsOnClickListener;

/**
 * The configuration screen for the {@link AgendaWidget AgendaWidget} AppWidget.
 */
public class AgendaWidgetConfigureActivity extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST_READ_CALENDAR = 1;
    static final int PERMISSIONS_REQUEST_READ_CALENDAR_INSIST = 2;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Settings settings;

    public AgendaWidgetConfigureActivity() {
        super();
    }

    private TabHost tabHost;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.agenda_widget_configure);
        //mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("tab_general").setIndicator(getString(R.string.tab_general)).setContent(R.id.tab_general));
        tabHost.addTab(tabHost.newTabSpec("tab_calendar").setIndicator(getString(R.string.tab_calendar)).setContent(R.id.tab_calendar));
        tabHost.addTab(tabHost.newTabSpec("tab_tasks").setIndicator(getString(R.string.tab_tasks)).setContent(R.id.tab_tasks));
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View v = tabHost.getTabWidget().getChildAt(i);
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColorStateList(R.drawable.tab_selector));
        }
        settings = new Settings(this, mAppWidgetId);

        tabHost.setCurrentTab(0);
        ListView l = (ListView)tabHost.findViewById(R.id.lst_general);
        l.setAdapter(new SettingsListAdapter(this, settings.getListItems(SettingTab.GENERAL)));

        initializeListListeners();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                ListView l;
                switch (tabId) {
                    case "tab_general":
                        l = (ListView)tabHost.findViewById(R.id.lst_general);
                        l.setAdapter(new SettingsListAdapter(AgendaWidgetConfigureActivity.this, settings.getListItems(SettingTab.GENERAL)));
                        break;
                    case "tab_calendar":
                        l = (ListView)tabHost.findViewById(R.id.lst_calendar);
                        l.setAdapter(new SettingsListAdapter(AgendaWidgetConfigureActivity.this, settings.getListItems(SettingTab.CALENDAR)));
                        break;
                    case "tab_tasks":
                        break;
                }
            }
        });
    }

    private void initializeListListeners() {
        SettingsOnClickListener listener = new SettingsOnClickListener(settings);
        ((ListView)tabHost.findViewById(R.id.lst_general)).setOnItemClickListener(listener);
        ((ListView)tabHost.findViewById(R.id.lst_calendar)).setOnItemClickListener(listener);
        ((ListView)tabHost.findViewById(R.id.lst_tasks)).setOnItemClickListener(listener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                settings.saveSettingsValues();
                //update the widget
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, AgendaWidget.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {mAppWidgetId});
                sendBroadcast(intent);

                // create the return intent
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
                break;
            case R.id.action_help:
                // TODO: Show help activity
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", ">>>>>>>> READ_CALENDAR Granted!");
                } else {
                    Log.d("PERMISSION", ">>>>>>>> READ_CALENDAR Denied!");
                }
                return;
            }
            case PERMISSIONS_REQUEST_READ_CALENDAR_INSIST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We know we are in calendar's tab and the first setting is the one we want
                    // Notice that the first setting is at index (position) 1. The item at 0 is the
                    // category title
                    ListView l = (ListView)tabHost.findViewById(R.id.lst_calendar);
                    l.performItemClick(l.getAdapter().getView(1, null, null),
                            1, l.getAdapter().getItemId(1));
                }
            }
        }
    }

}

