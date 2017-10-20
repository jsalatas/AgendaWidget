package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;

import java.util.List;

public class SettingsListAdapter extends ArrayAdapter<ListItem> {

    private final LayoutInflater inflater;

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
            tv.setText(AgendaWidgetApplication.getResourceString(((ListItemCategory) item).getCategory()));
        } else if (item instanceof ListItemSetting) {
            Setting setting = ((ListItemSetting) item).getSetting();

            v = setting.getView(this.getContext());
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
