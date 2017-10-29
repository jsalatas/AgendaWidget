package gr.ictpro.jsalatas.agendawidget.model.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendar;

import java.util.List;

public class TaskListAdapter extends ArrayAdapter<Task> {
    private final LayoutInflater inflater;

    public TaskListAdapter(Context context, List<Task> items) {
        super(context, 0, items);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task item = getItem(position);
        View v = inflater.inflate(R.layout.calendar_select_list_item, parent, false);
        TextView tvDisplayName = (TextView)v.findViewById(R.id.tvDisplayName);
        tvDisplayName.setText(item.getName());
        TextView tvAccountName = (TextView)v.findViewById(R.id.tvAccountName);
        tvAccountName.setText(item.getAccountName());
        View viewCalendarColor = v.findViewById(R.id.viewCalendarColor);
        viewCalendarColor.setBackgroundColor(item.getColor());

        return v;

    }
}