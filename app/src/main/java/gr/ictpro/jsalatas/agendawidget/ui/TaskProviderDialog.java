package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.DateTimeFormatListAdapter;
import gr.ictpro.jsalatas.agendawidget.model.settings.TaskProviderListAdapter;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class TaskProviderDialog extends SettingDialog<String> {

    public TaskProviderDialog(Activity activity, Setting<String> setting) {
        super(activity, setting, R.layout.dialog_task_provider);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView l = (ListView) findViewById(R.id.lst_task_provider);

        TaskProviderListAdapter adapter = new TaskProviderListAdapter(getContext(), setting);
        l.setAdapter(adapter);

        int selectedPos = -1;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (setting.getValue().equals(adapter.getItem(i).getClass().getCanonicalName())) {
                selectedPos = i;
                break;
            }
        }

        l.setItemChecked(selectedPos, true);
    }

    @Override
    protected String getSetting() {
        ListView l = (ListView) findViewById(R.id.lst_task_provider);
        TaskProviderListAdapter adapter = (TaskProviderListAdapter) l.getAdapter();
        return adapter.getItem(l.getCheckedItemPosition()).getClass().getCanonicalName();
    }
}
