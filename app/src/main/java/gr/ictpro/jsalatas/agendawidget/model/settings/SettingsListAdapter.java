package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgentaWidgetApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsListAdapter extends ArrayAdapter<ListItem> {

    private LayoutInflater inflater;

    public SettingsListAdapter(Context context, List<ListItem> items) {
        super(context, 0, items);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ListItem item = getItem(position);

        if (item instanceof ListItemCategory) {
            v = inflater.inflate(R.layout.settings_list_category, parent, false);
            v.setClickable(true);

            TextView tv = (TextView) v.findViewById(R.id.tvCategory);
            tv.setText(AgentaWidgetApplication.getResourceString(((ListItemCategory) item).getCategory()));
        } else if (item instanceof ListItemSetting) {
            Setting setting = ((ListItemSetting) item).getSetting();

            if (setting.getType() == SettingType.BOOL) {
                v = inflater.inflate(R.layout.settings_list_item_switch, parent, false);

                SwitchCompat s = (SwitchCompat) v.findViewById(R.id.swcValue);
                s.setChecked(Boolean.parseBoolean(setting.getValue()));
                s.setTrackTintList(this.getContext().getResources().getColorStateList(R.drawable.switch_selector));
            } else {
                v = inflater.inflate(R.layout.settings_list_item, parent, false);
            }

            TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvTitle.setText(AgentaWidgetApplication.getResourceString(setting.getTitle()));
            Date currentTime = Calendar.getInstance().getTime();

            TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
            if (setting.getType() == SettingType.DATE_LONG || setting.getType() == SettingType.DATE_SHORT) {
                tvDescription.setText(Settings.formatDate(setting.getValue(), currentTime));
            } else if (setting.getType() == SettingType.TIME) {
                tvDescription.setText(Settings.formatTime(setting.getValue(), currentTime));
            } else {
                tvDescription.setText(AgentaWidgetApplication.getResourceString(setting.getDescription()));
            }
            v.setClickable(false);

            LinearLayout root = (LinearLayout) v.findViewById(R.id.root);

            if (position != getCount() - 1 && getItem(position + 1) instanceof ListItemSetting) {
                root.setShowDividers(LinearLayout.SHOW_DIVIDER_END);
                root.setDividerDrawable(this.getContext().getResources().getDrawable(R.drawable.settings_divider));
            }
        }


        return v;
    }
}
