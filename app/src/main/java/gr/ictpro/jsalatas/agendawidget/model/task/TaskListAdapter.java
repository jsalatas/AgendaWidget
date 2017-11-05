package gr.ictpro.jsalatas.agendawidget.model.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;

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
        if(item.getAccountName() == null) {
            tvAccountName.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)tvDisplayName.getLayoutParams();
            int margin = AgendaWidgetApplication.getContext().getResources().getDimensionPixelSize(R.dimen.tasklist_margin);
            lp.setMargins(0, margin, 0, margin);
        } else {
            tvAccountName.setText(item.getAccountName());
        }
        View viewCalendarColor = v.findViewById(R.id.viewCalendarColor);
        if(item.getColor() == null) {
            viewCalendarColor.setVisibility(View.GONE);
        } else {
            viewCalendarColor.setBackgroundColor(item.getColor());
        }

        return v;

    }
}