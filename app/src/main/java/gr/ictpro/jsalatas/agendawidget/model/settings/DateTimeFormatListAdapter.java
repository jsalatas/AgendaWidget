package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgentaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;

import java.util.Calendar;
import java.util.Date;

public class DateTimeFormatListAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;
    private SettingType type;
    private String format;

    public DateTimeFormatListAdapter(Context context, String format, SettingType type) {
        super(context, 0);
        this.type = type;
        this.format = format;

        fillItems();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);
        View v = inflater.inflate(R.layout.datetime_format_list_item, parent, false);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText(AgentaWidgetApplication.getResourceString(item));
        Date currentTime = Calendar.getInstance().getTime();

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        View dialog = (View) parent.getParent();
        if (type == SettingType.DATE_LONG || type == SettingType.DATE_SHORT) {
            tvDescription.setText(Settings.formatDate(DateFormatDialog.getDateTimeFormat(dialog, item), currentTime));
        } else if (type == SettingType.TIME) {
            tvDescription.setText(Settings.formatTime(DateFormatDialog.getDateTimeFormat(dialog, item), currentTime));
        }

        return v;
    }

    private void fillItems() {
        switch (type) {
            case DATE_LONG:
                super.add(getContext().getString(R.string.full_format));
                super.add(getContext().getString(R.string.long_format));
                super.add(getContext().getString(R.string.medium_format));
                break;
            case DATE_SHORT:
                super.add(getContext().getString(R.string.medium_format));
                super.add(getContext().getString(R.string.short_format));
                break;
            case TIME:
                super.add(getContext().getString(R.string.short_format));
                break;
        }

        super.add(getContext().getString(R.string.custom_format));
    }
}
