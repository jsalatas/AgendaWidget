package gr.ictpro.jsalatas.agendawidget.ui;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
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
    private static final int PERMISSIONS_REQUEST_ACCESS_EXTERNAL_STORAGE = 2;
    static final int PERMISSIONS_REQUEST_READ_TASK = 3;
    private static final int BACKUP_FILE_WRITE = 4;
    private static final int BACKUP_FILE_READ = 5;
    private boolean savingBackup;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
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
            case R.id.action_backup:
                savingBackup = true;
                String filename = "AgendaWidget_" + widgetId + ".xml";
                try {
                    saveSettings(filename);
                } catch (RuntimeException e) {
                    String dialogTitle = this.getString(R.string.backup_failed);
                    String dialogText = getString(R.string.error_occurred) + System.getProperty("line.separator") + e.getMessage();
                    int resId = R.drawable.ic_dialog_error;
                    showAlert(resId, dialogTitle, dialogText);
                }
                break;
            case R.id.action_restore:
                savingBackup = false;
                try {
                    loadSettings();
                } catch (RuntimeException e) {
                    String dialogTitle = this.getString(R.string.restore_failed);
                    String dialogText = getString(R.string.error_occurred) + System.getProperty("line.separator") + e.getMessage();
                    int resId = R.drawable.ic_dialog_error;
                    showAlert(resId, dialogTitle, dialogText);
                }
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Fragment f = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());

                    ListView l = (ListView) f.getView().findViewById(R.id.lst_settings);
                    SettingsListAdapter adapter = (SettingsListAdapter) l.getAdapter();
                    int index = adapter.indexOf("calendars");
                    if (index != -1) {
                        l.performItemClick(l.getAdapter().getView(index, null, null),
                                index, l.getAdapter().getItemId(index));
                    }
                }
                break;
            }
            case PERMISSIONS_REQUEST_ACCESS_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (savingBackup) {
                        String filename = "AgendaWidget_" + widgetId + ".xml";
                        saveSettings(filename);
                    } else {
                        loadSettings();
                    }
                }
                break;
            }
            case PERMISSIONS_REQUEST_READ_TASK: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Fragment f = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());

                    ListView l = (ListView) f.getView().findViewById(R.id.lst_settings);
                    SettingsListAdapter adapter = (SettingsListAdapter) l.getAdapter();
                    int index = adapter.indexOf("tasks");
                    if (index != -1) {
                        l.performItemClick(l.getAdapter().getView(index, null, null),
                                index, l.getAdapter().getItemId(index));
                    }
                }
                break;
            }
        }
    }

    private void saveSettings(String filename) {
        if (!checkForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Insist
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_ACCESS_EXTERNAL_STORAGE);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/xml");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        startActivityForResult(intent, BACKUP_FILE_WRITE);
    }

    private void loadSettings() {
        if (!checkForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Insist
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_ACCESS_EXTERNAL_STORAGE);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/xml");
        //intent.putExtra(Intent.EXTRA_TITLE, "*.xml");

        startActivityForResult(intent, BACKUP_FILE_READ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String dialogTitle;
        String dialogText;
        int resId;
        if(data == null) {
            return;
        }
        try {
            if (requestCode == BACKUP_FILE_WRITE) {
                ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(data.getData(), "w");
                settings.saveSettings(pfd.getFileDescriptor());
            } else if (requestCode == BACKUP_FILE_READ) {
                ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(data.getData(), "r");
                settings.loadSettings(pfd.getFileDescriptor());
            }
            dialogTitle = this.getString(savingBackup ? R.string.backup_success : R.string.restore_success);

            dialogText = this.getString(savingBackup ? R.string.backup_created : R.string.backup_restored);
            resId = R.drawable.ic_dialog_info;

            if (!savingBackup) {
                Fragment f = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                ListView l = (ListView) f.getView().findViewById(R.id.lst_settings);
                ((SettingsListAdapter) l.getAdapter()).notifyDataSetChanged();

            }
        } catch (Exception e) {
            dialogTitle = this.getString(savingBackup ? R.string.backup_failed : R.string.restore_failed);
            dialogText = getString(R.string.error_occurred) + System.getProperty("line.separator") + e.getMessage();
            resId = R.drawable.ic_dialog_error;
        }

        showAlert(resId, dialogTitle, dialogText);
    }

    private void showAlert(int iconRes, String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(iconRes);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private boolean checkForPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions;
            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions = PERMISSIONS_STORAGE;
            } else {
                permissions = new String[]{permission};
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return false;
            } else {
                ActivityCompat.requestPermissions(this, permissions, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_ACCESS_EXTERNAL_STORAGE);
                return false;
            }
        }
        return true;
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