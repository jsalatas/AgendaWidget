package gr.ictpro.jsalatas.agendawidget.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.SparseBooleanArray;
import android.widget.ListView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.ListItemSetting;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingsListAdapter;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.model.task.*;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class TaskSelectionDialog extends SettingDialog<String> {
    public TaskSelectionDialog(Activity activity, Setting<String> setting) {
        super(activity, setting, R.layout.dialog_calendar_selection);
    }

    @Override
    public void show() {
        TaskContract tasks = getTaskProvider();
        if (AgendaWidgetConfigureActivity.checkForPermission(AgendaWidgetApplication.getActivity(this.getContext()),tasks.getPermissions(), AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_TASK)) {
            super.show();
            loadTasks();
        } else {
            if(tasks.getPermissions() != null) {
                ActivityCompat.requestPermissions(AgendaWidgetApplication.getActivity(this.getContext()), new String[]{tasks.getPermissions()}, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_TASK);
            }
            cancel();
        }
    }

    private TaskContract getTaskProvider() {
        ViewPager viewPager = ((AgendaWidgetConfigureActivity)AgendaWidgetApplication.getActivity(getContext())).getViewPager();
        Fragment f = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());

        ListView settingsList = (ListView) f.getView().findViewById(R.id.lst_settings);

        SettingsListAdapter sa = (SettingsListAdapter) settingsList.getAdapter();
        int index = sa.indexOf("taskProvider");

        return TaskProvider.getTaskContract(((ListItemSetting) sa.getItem(index)).getSetting().getStringValue());

    }
    private void loadTasks() {
        TaskListAdapter adapter = new TaskListAdapter(getContext(), Tasks.refreshTaskList(getTaskProvider()));

        ListView l = (ListView) findViewById(R.id.lst_calendars_selection);
        l.setAdapter(adapter);
        String selectedTasks = setting.getValue();
        if(!selectedTasks.isEmpty()) {
            String[] selectedIds = selectedTasks.split("@@@");
            for (String selectedId : selectedIds) {
                int id = Integer.parseInt(selectedId);
                for (int j = 0; j < adapter.getCount(); j++) {
                    Task t = adapter.getItem(j);
                    if (t.getId() == id) {
                        l.setItemChecked(j, true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected String getSetting() {
        TaskContract tasks = getTaskProvider();
        AgendaWidgetConfigureActivity.checkForPermission(AgendaWidgetApplication.getActivity(this.getContext()),tasks.getPermissions(), AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_TASK);
        ListView l = (ListView) findViewById(R.id.lst_calendars_selection);
        StringBuilder selected = new StringBuilder();
        SparseBooleanArray checked = l.getCheckedItemPositions();
        int len = l.getCount();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                Task t = (Task) l.getAdapter().getItem(i);
                if (!selected.toString().isEmpty()) {
                    selected.append("@@@");
                }
                selected.append(t.getId());
            }
        }

        return selected.toString();
    }
}
