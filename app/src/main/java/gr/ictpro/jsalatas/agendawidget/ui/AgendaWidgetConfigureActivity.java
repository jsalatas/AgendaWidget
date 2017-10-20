package gr.ictpro.jsalatas.agendawidget.ui;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.*;

/**
 * The configuration screen for the {@link AgendaWidget AgendaWidget} AppWidget.
 */
public class AgendaWidgetConfigureActivity extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST_READ_CALENDAR = 1;
    static final int PERMISSIONS_REQUEST_READ_CALENDAR_INSIST = 2;

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Settings settings;

    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.agenda_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        settings = new Settings(this, widgetId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter;
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{widgetId});
                intent.putExtra(AgendaWidget.ACTION_FORCE_UPDATE, true);
                sendBroadcast(intent);

                // create the return intent
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                } else {
//                }
                return;
            }
            case PERMISSIONS_REQUEST_READ_CALENDAR_INSIST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Fragment f = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());

                    ListView l = (ListView) f.getView().findViewById(R.id.lst_settings);
                    l.performItemClick(l.getAdapter().getView(1, null, null),
                            1, l.getAdapter().getItemId(1));
                }
            }
        }
    }

    private Settings getSettings() {
        return settings;
    }

    @SuppressWarnings("WeakerAccess")
    public static class PlaceholderFragment extends Fragment {
        private static final String TAB_NUMBER = "tab_number";
        SettingTab settingTab;

        static PlaceholderFragment newInstance(int tabNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_NUMBER, tabNumber);
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int tab = getArguments().getInt(TAB_NUMBER);
            int fragment = -1;
            settingTab = SettingTab.GENERAL;
            switch (tab) {
                case 0:
                    fragment = R.layout.fragment_configure_general;
                    settingTab = SettingTab.GENERAL;
                    break;
                case 1:
                    fragment = R.layout.fragment_configure_calendar;
                    settingTab = SettingTab.CALENDAR;
                    break;
                case 2:
                    fragment = R.layout.fragment_configure_tasks;
                    settingTab = SettingTab.TASKS;
                    break;
            }
            return inflater.inflate(fragment, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ListView l = (ListView) view.findViewById(R.id.lst_settings);

            AgendaWidgetConfigureActivity activity = (AgendaWidgetConfigureActivity) AgendaWidgetApplication.getActivity(getContext());
            Settings settings = activity.getSettings();
            l.setAdapter(new SettingsListAdapter(getContext(), settings.getListItems(settingTab)));

            l.setOnItemClickListener(new SettingsOnClickListener(settings));
        }
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_general);
                case 1:
                    return getString(R.string.tab_calendar);
                case 2:
                    return getString(R.string.tab_tasks);
            }
            return null;
        }

    }
}