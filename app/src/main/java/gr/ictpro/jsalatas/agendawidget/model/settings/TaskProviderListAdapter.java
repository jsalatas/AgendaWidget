package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.ContentProviderClient;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskProvider;
import gr.ictpro.jsalatas.agendawidget.model.task.providers.NoTaskProvider;

public class TaskProviderListAdapter extends ArrayAdapter<TaskContract> {
    private final LayoutInflater inflater;
    private final Setting setting;

    public TaskProviderListAdapter(Context context, Setting setting) {
        super(context, 0);
        this.setting = setting;

        super.addAll(TaskProvider.getProviders());
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskContract item = getItem(position);
        final View v = inflater.inflate(R.layout.task_provider_list_item, parent, false);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText(AgendaWidgetApplication.getResourceString(item.getProviderName()));
        if(!isEnabled(position)) {
            tvTitle.setTextColor(AgendaWidgetApplication.getContext().getResources().getColor(R.color.colorDisabledItem));
            v.findViewById(R.id.compound_button).setEnabled(false);
        }

        return v;
    }

    public static boolean providerExists(TaskContract taskProvider) {
        boolean exists ;
        // TODO: needs permissions???
        ContentProviderClient taskClient = AgendaWidgetApplication.getContext().getContentResolver().acquireContentProviderClient(taskProvider.getProviderURI());
        if (taskClient == null) {
            exists = false;
        }
        else {
            taskClient.release();
            exists = true;
        }
        return exists;
    }

    @Override
    public boolean isEnabled(int position) {
        TaskContract tasks = getItem(position);
        return tasks instanceof NoTaskProvider || providerExists(tasks);
    }
}
