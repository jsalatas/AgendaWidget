package gr.ictpro.jsalatas.agendawidget.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.widget.ListView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.model.task.Task;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskListAdapter;
import gr.ictpro.jsalatas.agendawidget.model.task.Tasks;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class TaskSelectionDialog extends SettingDialog<String> {

    public TaskSelectionDialog(Activity activity, Setting<String> setting) {
        super(activity, setting, R.layout.dialog_calendar_selection);
    }

    @Override
    public void show() {
        if (checkForPermission(Tasks.READ_TASKS_PERMISSION)) {
            super.show();
            loadTasks();
        } else {
            ActivityCompat.requestPermissions(AgendaWidgetApplication.getActivity(this.getContext()), new String[]{Tasks.READ_TASKS_PERMISSION}, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_TASK);
            cancel();
        }
    }

    private void loadTasks() {
        Tasks.refreshTaskList();
        ListView l = (ListView) findViewById(R.id.lst_calendars_selection);

        TaskListAdapter adapter = new TaskListAdapter(getContext(), Tasks.getTaskList());
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

    private boolean checkForPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(AgendaWidgetApplication.getActivity(this.getContext()), permission)) {
                return false;
            } else {
                ActivityCompat.requestPermissions(AgendaWidgetApplication.getActivity(this.getContext()), new String[]{permission}, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_READ_TASK);
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getSetting() {
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
